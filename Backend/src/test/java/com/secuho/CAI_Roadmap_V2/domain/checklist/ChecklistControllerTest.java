package com.secuho.CAI_Roadmap_V2.domain.checklist;

import com.secuho.CAI_Roadmap_V2.domain.auth.entity.User;
import com.secuho.CAI_Roadmap_V2.domain.auth.repository.UserRepository;
import com.secuho.CAI_Roadmap_V2.domain.course.entity.Course;
import com.secuho.CAI_Roadmap_V2.domain.course.entity.Enrollment;
import com.secuho.CAI_Roadmap_V2.domain.course.entity.EnrollmentStatus;
import com.secuho.CAI_Roadmap_V2.domain.course.entity.TermSummary;
import com.secuho.CAI_Roadmap_V2.domain.course.repository.CourseRepository;
import com.secuho.CAI_Roadmap_V2.domain.course.repository.EnrollmentRepository;
import com.secuho.CAI_Roadmap_V2.domain.course.repository.TermSummaryRepository;
import com.secuho.CAI_Roadmap_V2.global.jwt.JwtProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@ActiveProfiles("local")
@Transactional
class ChecklistControllerTest {

    @Autowired
    private WebApplicationContext context;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private CourseRepository courseRepository;
    @Autowired
    private EnrollmentRepository enrollmentRepository;
    @Autowired
    private TermSummaryRepository termSummaryRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private JwtProvider jwtProvider;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(context)
                .apply(SecurityMockMvcConfigurers.springSecurity())
                .build();
    }

    @Test
    void checklistAggregatesAcrossCategories() throws Exception {
        String studentId = "checklist-test-" + System.currentTimeMillis();
        User user = userRepository.save(User.builder()
                .studentId(studentId)
                .password(passwordEncoder.encode("pass1234"))
                .build());
        String accessToken = jwtProvider.createAccessToken(studentId);

        // 기본소양 2/3 (공학윤리 누락)
        complete(user, "BAS1", "기술창조와특허", "공교", 2.0, "A");
        complete(user, "BAS2", "공학경제", "공교", 2.0, "B");

        // BSM 필수 3/4 (이산수학 누락) + 실험 과목 1개(광의 BSM 목록에도 포함)
        complete(user, "BSM1", "미적분학및연습1", "전공", 3.0, "A");
        complete(user, "BSM2", "확률및통계학", "전공", 3.0, "A");
        complete(user, "BSM3", "공학선형대수학", "전공", 3.0, "A");
        complete(user, "EXP1", "일반물리학및실험1", "전공", 1.0, "B");

        // 동국인성 3/3 완료
        complete(user, "ATT1", "자아와명상1", "공교", 1.0, "P");
        complete(user, "ATT2", "자아와명상2", "공교", 1.0, "P");
        complete(user, "ATT3", "불교와인간", "공교", 2.0, "A");

        // F 학점 전공 과목 -> 학점 합산에서 제외되어야 함
        complete(user, "FAIL1", "실패과목", "전공", 3.0, "F");

        // 종합설계1만 이수 (설계2는 미이수)
        complete(user, "CSC4018", "종합설계1", "전공", 3.0, "A");

        // 외국어강의 1개
        Course englishCourse = courseRepository.save(Course.builder()
                .sbjNo("ENG1").dvcls("01").name("영어회화").cpdvNm("교양")
                .credit(2.0).openYear(2025).semCd("2").lesnStyCd("외국어강의")
                .build());
        enrollmentRepository.save(Enrollment.builder()
                .user(user).course(englishCourse).year(2025).semCd("2")
                .status(EnrollmentStatus.COMPLETED).gradeCd("A").build());

        termSummaryRepository.save(TermSummary.builder()
                .user(user).year(2025).semCd("1").yySemName("2025-1학기").gpa(4.0).build());
        termSummaryRepository.save(TermSummary.builder()
                .user(user).year(2025).semCd("2").yySemName("2025-2학기").gpa(3.5).build());

        String body = mockMvc.perform(get("/api/checklist").header("Authorization", "Bearer " + accessToken))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        assertEquals(13.0, extractNumber(body, "major"));
        assertEquals(8.0, extractNumber(body, "common"));
        assertEquals(23.0, extractNumber(body, "totalCredit"));
        assertEquals(4.0, extractNumber(body, "basic"));
        assertEquals(10.0, extractNumber(body, "bsm"));
        assertEquals(3.75, extractNumber(body, "gpa"));
        assertEquals(1, (int) extractNumber(body, "design1"));
        assertEquals(0, (int) extractNumber(body, "design2"));
        assertEquals(1, (int) extractNumber(body, "engCredit"));

        assertTrue(extractBoolean(body, "basicCompleted"));
        assertTrue(extractArray(body, "missingBasicCourses").contains("공학윤리"));

        assertTrue(!extractBoolean(body, "bsmCompleted"));
        assertTrue(extractArray(body, "missingBsmCourses").contains("이산수학"));
        assertEquals("실험 과목 수강 완료", extractString(body, "experimentCourseStatus"));

        assertTrue(extractBoolean(body, "commonDonggukAttitude"));
        assertEquals("", extractArray(body, "missingCommonDonggukAttitude"));

        assertTrue(!extractBoolean(body, "commonSelfDev"));
        assertTrue(extractArray(body, "missingCommonSelfDev").contains("커리어 디자인"));

        assertTrue(!extractBoolean(body, "commonThinkncom"));
        assertTrue(!extractBoolean(body, "commonCreative"));
        assertTrue(!extractBoolean(body, "commonDigitaliter"));
    }

    private void complete(User user, String sbjNo, String name, String cpdvNm, double credit, String gradeCd) {
        Course course = courseRepository.save(Course.builder()
                .sbjNo(sbjNo).dvcls("01").name(name).cpdvNm(cpdvNm)
                .credit(credit).openYear(2025).semCd("2")
                .build());
        enrollmentRepository.save(Enrollment.builder()
                .user(user).course(course).year(2025).semCd("2")
                .status(EnrollmentStatus.COMPLETED).gradeCd(gradeCd).build());
    }

    private double extractNumber(String json, String field) {
        Matcher matcher = Pattern.compile("\"" + field + "\":(-?\\d+\\.?\\d*)").matcher(json);
        return matcher.find() ? Double.parseDouble(matcher.group(1)) : Double.NaN;
    }

    private boolean extractBoolean(String json, String field) {
        Matcher matcher = Pattern.compile("\"" + field + "\":(true|false)").matcher(json);
        return matcher.find() && Boolean.parseBoolean(matcher.group(1));
    }

    private String extractString(String json, String field) {
        Matcher matcher = Pattern.compile("\"" + field + "\":\"([^\"]*)\"").matcher(json);
        return matcher.find() ? matcher.group(1) : null;
    }

    private String extractArray(String json, String field) {
        Matcher matcher = Pattern.compile("\"" + field + "\":\\[(.*?)]").matcher(json);
        return matcher.find() ? matcher.group(1) : null;
    }
}
