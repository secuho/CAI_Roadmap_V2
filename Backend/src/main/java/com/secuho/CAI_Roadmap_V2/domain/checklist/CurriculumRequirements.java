package com.secuho.CAI_Roadmap_V2.domain.checklist;

import java.util.List;

/**
 * V1 {@code get_checklist}에 하드코딩되어 있던 졸업요건 과목명/과목코드 목록을 그대로 포팅.
 * 커리큘럼이 자주 바뀌지 않는다는 전제로 상수 클래스로 유지(필요해지면 DB 설정 테이블로 분리 검토).
 */
public final class CurriculumRequirements {

    private CurriculumRequirements() {
    }

    public static final String DESIGN1_SBJ_NO = "CSC4018";
    public static final String DESIGN2_SBJ_NO = "CSC4019";

    public static final List<String> BASIC_COURSES = List.of("기술창조와특허", "공학경제", "공학윤리");

    /** BSM 학점 합산용 광의 목록. */
    public static final List<String> BSM_CREDIT_COURSES = List.of(
            "미적분학및연습1", "미적분학및연습2", "확률및통계학", "공학선형대수학",
            "공학수학1", "이산수학", "수치해석", "일반물리학및실험1",
            "일반물리학및실험2", "일반화학및실험1", "일반화학및실험2",
            "일반생물학및실험1", "일반생물학및실험2", "물리학개론", "화학개론",
            "생물학개론", "지구환경과학", "프로그래밍기초와실습", "인터넷프로그래밍",
            "데이터프로그래밍기초와실습", "인공지능프로그래밍기초와실습"
    );

    /** BSM 이수완료 판정용 협의(필수 4과목) 목록. */
    public static final List<String> BSM_REQUIRED_COURSES = List.of(
            "미적분학및연습1", "확률및통계학", "공학선형대수학", "이산수학"
    );

    public static final List<String> DONGGUK_ATTITUDE_COURSES = List.of("자아와명상1", "자아와명상2", "불교와인간");

    public static final List<String> SELF_DEV_COURSES = List.of("커리어 디자인", "기업가정신과 리더십");

    public static final List<String> THINKNCOM_COURSES = List.of("기술보고서작성및발표");

    public static final List<String> DIGITALITER_COURSES = List.of(
            "디지털 기술과 사회의 이해", "프로그래밍 이해와 실습", "빅데이터와인공지능의이해"
    );

    public static final String CREATIVE_NAME_PATTERN = "세미나";
    public static final String EXPERIMENT_NAME_PATTERN = "실험";
    public static final String ENGLISH_NAME_PATTERN = "English";
}
