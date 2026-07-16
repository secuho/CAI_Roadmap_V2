package com.secuho.CAI_Roadmap_V2.domain.course;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

/**
 * 개발/검증 전용: extra/db_dump의 학기별 개설강좌 원본 데이터를 courses 테이블로 적재한다.
 * 실 NDRIMS 연동(Phase 4) 전까지 추천 기능의 "개설여부(is_opened)"/정원(capacity) 판정을
 * 실제 데이터로 검증하기 위한 목적. {@code app.seed-course-catalog=true}일 때만 동작하며 기본은 off.
 *
 * term1은 원본 JSON(data.json, 108개 필드 전체 보유)에서 파싱해 정원/폐강사유/다국어 설명 등
 * 고가치 필드까지 채우고, term2는 축소된 컬럼만 있는 MySQL 덤프의 INSERT 문을 직접 파싱한다
 * (H2가 mysqldump의 COLLATE/ENGINE 절이 섞인 CREATE TABLE을 그대로 실행하지 못해, SQL을 실행시키는
 * 대신 값 자체만 추출해서 바인딩한다).
 */
@Component
@Profile("local")
@RequiredArgsConstructor
@Slf4j
public class CourseCatalogSeeder implements ApplicationRunner {

    private final DataSource dataSource;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Value("${app.seed-course-catalog:false}")
    private boolean seedEnabled;

    private static final String DUMP_DIR = "extra/db_dump";

    private static final String INSERT_SQL = """
            INSERT INTO courses (
                sbj_no, dvcls, name, cpdv_nm, colg_nm, dpt_nm, main_prof_nm, credit, open_year, sem_cd, open_yy_sem,
                timetable_kor, timetable_eng, cpdiv_cd, cpdiv_cd_nm, detl_curi_cd, detl_curi_cd_nm, obj_schgrd,
                lesn_sty_cd, foreign_language_course, classroom, recod_grd_typ_cd_nm, recod_eval_meth_cd_nm, prof_kor_dsc,
                capacity, capacity_limited, cancelled_reason, cancelled_date, name_eng, description_kor, description_eng,
                classroom_eng, plan_url
            ) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)
            """;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        if (!seedEnabled) {
            return;
        }

        try (Connection connection = dataSource.getConnection()) {
            if (alreadySeeded(connection)) {
                log.info("courses 테이블에 이미 데이터가 있어 카탈로그 시딩을 건너뜁니다.");
                return;
            }
            seedFromJson(connection, "data.json", "1");
            seedFromSqlDump(connection, "courses_courses_2025term2.sql", "2");
        }
    }

    private boolean alreadySeeded(Connection connection) throws Exception {
        try (Statement statement = connection.createStatement()) {
            var resultSet = statement.executeQuery("SELECT COUNT(*) FROM courses");
            resultSet.next();
            return resultSet.getInt(1) > 0;
        }
    }

    private void seedFromJson(Connection connection, String fileName, String semCd) throws Exception {
        File file = Path.of(DUMP_DIR, fileName).toFile();
        if (!file.exists()) {
            log.warn("카탈로그 JSON 파일을 찾을 수 없어 건너뜁니다: {}", file.getAbsolutePath());
            return;
        }

        JsonNode records = objectMapper.readTree(file).path("dsMain");

        try (PreparedStatement ps = connection.prepareStatement(INSERT_SQL)) {
            int count = 0;
            for (JsonNode r : records) {
                Integer openYear = intOrNull(r, "OPEN_YY");
                String lesnStyCd = textOrNull(r, "LESN_STY_CD");

                bindRow(ps,
                        textOrNull(r, "SBJ_NO"), textOrNull(r, "DVCLS"), textOrNull(r, "SBJ_NM"),
                        textOrNull(r, "CPDIV_CD_NM"), textOrNull(r, "COLG_NM"), textOrNull(r, "DPT_NM"),
                        textOrNull(r, "EMP_NM"), doubleOrNull(r, "CDT"), openYear, semCd,
                        openYear != null ? openYear + "-" + semCd + "학기" : null,
                        textOrNull(r, "TMTBL_KOR_DSC"), textOrNull(r, "TMTBL_ENG_DSC"),
                        textOrNull(r, "CPDIV_CD"), textOrNull(r, "CPDIV_CD_NM"),
                        textOrNull(r, "DETL_CURI_CD"), textOrNull(r, "DETL_CURI_CD_NM"), textOrNull(r, "OBJ_SCHGRD"),
                        lesnStyCd, "외국어강의".equals(lesnStyCd), textOrNull(r, "ROOM_KOR_DSC"),
                        textOrNull(r, "RECOD_GRD_TYP_CD_NM"), textOrNull(r, "RECOD_EVAL_METH_CD_NM"), textOrNull(r, "PROF_KOR_DSC"),
                        intOrNull(r, "TKCRS_PCNT"), textOrNull(r, "TKCRS_PRSN_LIMT_YN"),
                        textOrNull(r, "CLSLESN_RESN_DSC"), textOrNull(r, "CLSLESN_DT"),
                        textOrNull(r, "SBJ_ENG_NM"), textOrNull(r, "SBJ_KOR_EXPLN_DSC"), textOrNull(r, "SBJ_ENG_EXPLN_DSC"),
                        textOrNull(r, "ROOM_ENG_DSC"), textOrNull(r, "PLAN_URL"));
                count++;
            }
            ps.executeBatch();
            log.info("{} 카탈로그 적재 완료 ({}건, sem_cd={})", fileName, count, semCd);
        }
    }

    /** courses_2025termN 원본 CREATE TABLE의 컬럼 순서 (INSERT 문의 값 순서와 동일). */
    private static final List<String> DUMP_COLUMNS = List.of(
            "CDT", "CPDIV_CD_NM", "DETL_CURI_CD_NM", "TMTBL_ENG_DSC", "OBJ_SCHGRD", "DETL_CURI_CD", "COLG_NM",
            "CPDIV_CD", "DPT_NM", "EMP_NM", "LESN_STY_CD", "TMTBL_KOR_DSC", "OPEN_YY", "SBJ_NO", "SBJ_NM",
            "RECOD_GRD_TYP_CD_NM", "DVCLS", "ROOM_KOR_DSC", "RECOD_EVAL_METH_CD_NM", "PROF_KOR_DSC"
    );

    private void seedFromSqlDump(Connection connection, String fileName, String semCd) throws Exception {
        Path dumpPath = Path.of(DUMP_DIR, fileName);
        if (!dumpPath.toFile().exists()) {
            log.warn("카탈로그 덤프 파일을 찾을 수 없어 건너뜁니다: {}", dumpPath.toAbsolutePath());
            return;
        }

        List<String[]> rows = parseInsertTuples(dumpPath);

        try (PreparedStatement ps = connection.prepareStatement(INSERT_SQL)) {
            for (String[] row : rows) {
                java.util.Map<String, String> byCol = new java.util.HashMap<>();
                for (int i = 0; i < DUMP_COLUMNS.size(); i++) {
                    byCol.put(DUMP_COLUMNS.get(i), row[i]);
                }

                Integer openYear = byCol.get("OPEN_YY") != null ? Integer.valueOf(byCol.get("OPEN_YY")) : null;
                String lesnStyCd = byCol.get("LESN_STY_CD");
                Double credit = byCol.get("CDT") != null ? Double.valueOf(byCol.get("CDT")) : null;

                bindRow(ps,
                        byCol.get("SBJ_NO"), byCol.get("DVCLS"), byCol.get("SBJ_NM"),
                        byCol.get("CPDIV_CD_NM"), byCol.get("COLG_NM"), byCol.get("DPT_NM"),
                        byCol.get("EMP_NM"), credit, openYear, semCd,
                        openYear != null ? openYear + "-" + semCd + "학기" : null,
                        byCol.get("TMTBL_KOR_DSC"), byCol.get("TMTBL_ENG_DSC"),
                        byCol.get("CPDIV_CD"), byCol.get("CPDIV_CD_NM"),
                        byCol.get("DETL_CURI_CD"), byCol.get("DETL_CURI_CD_NM"), byCol.get("OBJ_SCHGRD"),
                        lesnStyCd, "외국어강의".equals(lesnStyCd), byCol.get("ROOM_KOR_DSC"),
                        byCol.get("RECOD_GRD_TYP_CD_NM"), byCol.get("RECOD_EVAL_METH_CD_NM"), byCol.get("PROF_KOR_DSC"),
                        null, null, null, null, null, null, null, null, null);
            }
            ps.executeBatch();
            log.info("{} 카탈로그 적재 완료 ({}건, sem_cd={}, 정원 등 고가치 필드는 이 소스에 없어 null)", fileName, rows.size(), semCd);
        }
    }

    /** mysqldump 확장 INSERT(다중 행 VALUES)의 값만 직접 파싱한다. H2로 원본 SQL을 그대로 실행하지 않는다. */
    private List<String[]> parseInsertTuples(Path dumpPath) throws Exception {
        List<String> lines = Files.readAllLines(dumpPath, StandardCharsets.UTF_8);
        String insertLine = lines.stream()
                .filter(line -> line.startsWith("INSERT INTO"))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException(dumpPath + "에서 INSERT 문을 찾을 수 없습니다."));

        String text = insertLine.substring(insertLine.indexOf("VALUES ") + "VALUES ".length()).trim();
        if (text.endsWith(";")) {
            text = text.substring(0, text.length() - 1);
        }

        List<String[]> rows = new ArrayList<>();
        int i = 0;
        int n = text.length();
        while (i < n) {
            if (text.charAt(i) != '(') {
                i++;
                continue;
            }
            i++;
            List<String> fields = new ArrayList<>();
            boolean rowDone = false;
            while (!rowDone) {
                char c = text.charAt(i);
                if (c == '\'') {
                    i++;
                    StringBuilder sb = new StringBuilder();
                    while (true) {
                        char cc = text.charAt(i);
                        if (cc == '\\') {
                            sb.append(text.charAt(i + 1));
                            i += 2;
                        } else if (cc == '\'') {
                            i++;
                            break;
                        } else {
                            sb.append(cc);
                            i++;
                        }
                    }
                    fields.add(sb.toString());
                } else {
                    int start = i;
                    while (text.charAt(i) != ',' && text.charAt(i) != ')') {
                        i++;
                    }
                    String token = text.substring(start, i).trim();
                    fields.add("NULL".equalsIgnoreCase(token) ? null : token);
                }
                while (Character.isWhitespace(text.charAt(i))) {
                    i++;
                }
                if (text.charAt(i) == ',') {
                    i++;
                    while (Character.isWhitespace(text.charAt(i))) {
                        i++;
                    }
                } else if (text.charAt(i) == ')') {
                    i++;
                    rowDone = true;
                }
            }
            rows.add(fields.toArray(new String[0]));
        }
        return rows;
    }

    private void bindRow(PreparedStatement ps, String sbjNo, String dvcls, String name, String cpdvNm,
                          String colgNm, String dptNm, String mainProfNm, Double credit, Integer openYear,
                          String semCd, String openYySem, String timetableKor, String timetableEng, String cpdivCd,
                          String cpdivCdNm, String detlCuriCd, String detlCuriCdNm, String objSchgrd, String lesnStyCd,
                          boolean foreignLanguageCourse, String classroom, String recodGrdTypCdNm,
                          String recodEvalMethCdNm, String profKorDsc, Integer capacity, String capacityLimited,
                          String cancelledReason, String cancelledDate, String nameEng, String descriptionKor,
                          String descriptionEng, String classroomEng, String planUrl) throws Exception {
        int i = 1;
        ps.setString(i++, sbjNo);
        ps.setString(i++, dvcls);
        ps.setString(i++, name);
        ps.setString(i++, cpdvNm);
        ps.setString(i++, colgNm);
        ps.setString(i++, dptNm);
        ps.setString(i++, mainProfNm);
        setDoubleOrNull(ps, i++, credit);
        setIntOrNull(ps, i++, openYear);
        ps.setString(i++, semCd);
        ps.setString(i++, openYySem);
        ps.setString(i++, timetableKor);
        ps.setString(i++, timetableEng);
        ps.setString(i++, cpdivCd);
        ps.setString(i++, cpdivCdNm);
        ps.setString(i++, detlCuriCd);
        ps.setString(i++, detlCuriCdNm);
        ps.setString(i++, objSchgrd);
        ps.setString(i++, lesnStyCd);
        ps.setBoolean(i++, foreignLanguageCourse);
        ps.setString(i++, classroom);
        ps.setString(i++, recodGrdTypCdNm);
        ps.setString(i++, recodEvalMethCdNm);
        ps.setString(i++, profKorDsc);
        setIntOrNull(ps, i++, capacity);
        ps.setString(i++, capacityLimited);
        ps.setString(i++, cancelledReason);
        ps.setString(i++, cancelledDate);
        ps.setString(i++, nameEng);
        ps.setString(i++, descriptionKor);
        ps.setString(i++, descriptionEng);
        ps.setString(i++, classroomEng);
        ps.setString(i, planUrl);
        ps.addBatch();
    }

    private String textOrNull(JsonNode node, String field) {
        JsonNode value = node.get(field);
        return (value == null || value.isNull()) ? null : value.asText();
    }

    private Integer intOrNull(JsonNode node, String field) {
        JsonNode value = node.get(field);
        return (value == null || value.isNull()) ? null : value.asInt();
    }

    private Double doubleOrNull(JsonNode node, String field) {
        String text = textOrNull(node, field);
        return text == null ? null : Double.valueOf(text);
    }

    private void setIntOrNull(PreparedStatement ps, int index, Integer value) throws Exception {
        if (value == null) {
            ps.setNull(index, Types.INTEGER);
        } else {
            ps.setInt(index, value);
        }
    }

    private void setDoubleOrNull(PreparedStatement ps, int index, Double value) throws Exception {
        if (value == null) {
            ps.setNull(index, Types.DOUBLE);
        } else {
            ps.setDouble(index, value);
        }
    }
}
