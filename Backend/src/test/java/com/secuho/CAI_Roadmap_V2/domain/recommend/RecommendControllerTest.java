package com.secuho.CAI_Roadmap_V2.domain.recommend;

import com.secuho.CAI_Roadmap_V2.domain.auth.entity.User;
import com.secuho.CAI_Roadmap_V2.domain.auth.repository.UserRepository;
import com.secuho.CAI_Roadmap_V2.domain.course.entity.Course;
import com.secuho.CAI_Roadmap_V2.domain.course.entity.Enrollment;
import com.secuho.CAI_Roadmap_V2.domain.course.entity.EnrollmentStatus;
import com.secuho.CAI_Roadmap_V2.domain.course.repository.CourseRepository;
import com.secuho.CAI_Roadmap_V2.domain.course.repository.EnrollmentRepository;
import com.secuho.CAI_Roadmap_V2.domain.recommend.entity.CourseCandidate;
import com.secuho.CAI_Roadmap_V2.domain.recommend.entity.LiberalArtsCourse;
import com.secuho.CAI_Roadmap_V2.domain.recommend.repository.CourseCandidateRepository;
import com.secuho.CAI_Roadmap_V2.domain.recommend.repository.LiberalArtsCourseRepository;
import com.secuho.CAI_Roadmap_V2.global.jwt.JwtProvider;
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

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@ActiveProfiles("local")
@Transactional
class RecommendControllerTest {

    @Autowired
    private WebApplicationContext context;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private CourseRepository courseRepository;
    @Autowired
    private EnrollmentRepository enrollmentRepository;
    @Autowired
    private CourseCandidateRepository courseCandidateRepository;
    @Autowired
    private LiberalArtsCourseRepository liberalArtsCourseRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private JwtProvider jwtProvider;

    @Test
    void recommendationsFilterAndScoreCorrectly() throws Exception {
        MockMvc mockMvc = MockMvcBuilders.webAppContextSetup(context)
                .apply(SecurityMockMvcConfigurers.springSecurity())
                .build();

        String studentId = "recommend-test-" + System.currentTimeMillis();
        User user = userRepository.save(User.builder()
                .studentId(studentId)
                .password(passwordEncoder.encode("pass1234"))
                .track("AI")
                .build());
        String accessToken = jwtProvider.createAccessToken(studentId);

        // 이미 수강한(이수 완료) 과목들 -> excludedCodes
        completedCourse(user, "CSC3005");
        completedCourse(user, "RGC1000");
        completedCourse(user, "RGC2004");
        completedCourse(user, "PRI4002");

        // 다음 학기(2026-1)에 개설되는 후보 과목 카탈로그
        openCourse("CSC3001", "01", 30);
        openCourse("CSC3002", "01", 0);   // 정원 0 -> 제외
        openCourse("CSC3004", "01", 20);
        openCourse("CSC3005", "02", 20);  // 이미 수강한 과목도 개설은 되어 있음 -> 그래도 제외돼야 함
        openCourse("CSC3006", "01", 15);
        openCourse("RGC2001", "01", 10);
        openCourse("RGC2002", "01", 10);
        openCourse("RGC2003", "01", 10);
        openCourse("PRI4029", "01", 10);
        // RGC2005는 의도적으로 다음 학기 카탈로그에 없음 (개설여부 필터 테스트)

        courseCandidateRepository.save(CourseCandidate.builder()
                .code("CSC3001").title("데이터베이스").credit(3.0).enrollmentScore(5.0)
                .trackNames(Set.of("AI")).build());
        courseCandidateRepository.save(CourseCandidate.builder()
                .code("CSC3002").title("운영체제").credit(3.0).build());
        courseCandidateRepository.save(CourseCandidate.builder()
                .code("CSC3003").title("컴파일러").credit(3.0).build()); // 카탈로그에 없음 -> 미개설 제외
        courseCandidateRepository.save(CourseCandidate.builder()
                .code("CSC3004").title("고급알고리즘").credit(3.0).prereq("CSC9999").build());
        courseCandidateRepository.save(CourseCandidate.builder()
                .code("CSC3005").title("이미수강").credit(3.0).build());
        courseCandidateRepository.save(CourseCandidate.builder()
                .code("CSC3006").title("보안개론").credit(3.0).trackNames(Set.of("Security")).build());

        liberalArtsCourseRepository.save(LiberalArtsCourse.builder()
                .code("RGC1000").name("이미수강 교양").credit(4.0).totalCredit(4.0).midArea("MID_SATURATED").build());
        liberalArtsCourseRepository.save(LiberalArtsCourse.builder()
                .code("RGC2001").name("포화영역 교양").credit(2.0).totalCredit(4.0).midArea("MID_SATURATED").build());
        liberalArtsCourseRepository.save(LiberalArtsCourse.builder()
                .code("RGC2002").name("정상 교양").credit(2.0).totalCredit(4.0).midArea("MID_OPEN").build());
        liberalArtsCourseRepository.save(LiberalArtsCourse.builder()
                .code("RGC2004").name("이미수강 동일과목군").credit(2.0).sameGroup("G1").build());
        liberalArtsCourseRepository.save(LiberalArtsCourse.builder()
                .code("RGC2003").name("동일과목군 중복").credit(2.0).sameGroup("G1").build());
        liberalArtsCourseRepository.save(LiberalArtsCourse.builder()
                .code("PRI4029").name("상호배타 과목").credit(2.0).build());
        liberalArtsCourseRepository.save(LiberalArtsCourse.builder()
                .code("RGC2005").name("미개설 교양").credit(2.0).totalCredit(4.0).midArea("MID_OPEN2").build());

        String body = mockMvc.perform(get("/api/recommends")
                        .header("Authorization", "Bearer " + accessToken)
                        .param("nextTerm", "2026-1"))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        // 전공 추천: CSC3001(트랙 일치, 점수 높음), CSC3006(트랙 불일치, 점수 낮음)만 포함
        assertTrue(body.contains("\"code\":\"CSC3001\""));
        assertTrue(body.contains("\"code\":\"CSC3006\""));
        assertFalse(body.contains("\"code\":\"CSC3002\""));
        assertFalse(body.contains("\"code\":\"CSC3003\""));
        assertFalse(body.contains("\"code\":\"CSC3004\""));
        assertFalse(body.contains("\"code\":\"CSC3005\""));

        int csc3001Index = body.indexOf("\"code\":\"CSC3001\"");
        int csc3006Index = body.indexOf("\"code\":\"CSC3006\"");
        assertTrue(csc3001Index < csc3006Index, "트랙이 일치하는 CSC3001이 더 높은 점수로 먼저 나와야 함");

        // 교양 추천: RGC2002만 통과 (나머지는 포화영역/동일과목군/상호배타/미개설로 제외)
        assertTrue(body.contains("\"code\":\"RGC2002\""));
        assertFalse(body.contains("\"code\":\"RGC2001\""));
        assertFalse(body.contains("\"code\":\"RGC2003\""));
        assertFalse(body.contains("\"code\":\"PRI4029\""));
        assertFalse(body.contains("\"code\":\"RGC2005\""));
    }

    private void completedCourse(User user, String sbjNo) {
        Course course = courseRepository.save(Course.builder()
                .sbjNo(sbjNo).dvcls("99").name(sbjNo + " 과거 수강").credit(2.0)
                .openYear(2025).semCd("2").build());
        enrollmentRepository.save(Enrollment.builder()
                .user(user).course(course).year(2025).semCd("2")
                .status(EnrollmentStatus.COMPLETED).gradeCd("A").build());
    }

    private void openCourse(String sbjNo, String dvcls, int capacity) {
        courseRepository.save(Course.builder()
                .sbjNo(sbjNo).dvcls(dvcls).name(sbjNo + " 다음학기").credit(3.0)
                .openYear(2026).semCd("1").capacity(capacity).build());
    }
}
