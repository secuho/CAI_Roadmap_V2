package com.secuho.CAI_Roadmap_V2.domain.course.controller;

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

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@ActiveProfiles("local")
@Transactional
class CourseControllerTest {

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
    private String accessToken;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(context)
                .apply(SecurityMockMvcConfigurers.springSecurity())
                .build();

        String studentId = "course-test-" + System.currentTimeMillis();
        User user = userRepository.save(User.builder()
                .studentId(studentId)
                .password(passwordEncoder.encode("pass1234"))
                .track("AI")
                .build());
        accessToken = jwtProvider.createAccessToken(studentId);

        Course registeredCourse = courseRepository.save(Course.builder()
                .sbjNo("CSC1001").dvcls("01").name("자료구조").cpdvNm("전공")
                .mainProfNm("김교수").credit(3.0).openYear(2026).semCd("1")
                .openYySem("2026-1학기").timetableKor("월 1-2교시").classroom("공학관 101")
                .build());

        Course completedCourse = courseRepository.save(Course.builder()
                .sbjNo("CSC4018").dvcls("01").name("종합설계1").cpdvNm("전공")
                .mainProfNm("이교수").credit(3.0).openYear(2025).semCd("2")
                .openYySem("2025-2학기").build());

        enrollmentRepository.save(Enrollment.builder()
                .user(user).course(registeredCourse).year(2026).semCd("1")
                .openYySem("2026-1학기").status(EnrollmentStatus.REGISTERED).build());

        enrollmentRepository.save(Enrollment.builder()
                .user(user).course(completedCourse).year(2025).semCd("2")
                .openYySem("2025-2학기").status(EnrollmentStatus.COMPLETED).gradeCd("A+").build());

        termSummaryRepository.save(TermSummary.builder()
                .user(user).year(2025).semCd("2").yySemName("2025-2학기")
                .appliedCdt(18.0).gainedCdt(18.0).gpa(4.3).build());
    }

    @Test
    void getTermReturnsSummaries() throws Exception {
        String body = mockMvc.perform(get("/api/term").header("Authorization", "Bearer " + accessToken))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        assertTrue(body.contains("\"yySemName\":\"2025-2학기\""));
        assertTrue(body.contains("\"gpa\":4.3"));
    }

    @Test
    void getCurrentCoursesFiltersByYearAndTerm() throws Exception {
        String body = mockMvc.perform(get("/api/current-courses")
                        .header("Authorization", "Bearer " + accessToken)
                        .param("year", "2026")
                        .param("term", "1"))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        assertTrue(body.contains("\"name\":\"자료구조\""));
        assertTrue(body.contains("\"classroom\":\"공학관 101\""));
        assertTrue(!body.contains("종합설계1"));
    }

    @Test
    void getCompletedCoursesReturnsGrade() throws Exception {
        String body = mockMvc.perform(get("/api/completed-courses").header("Authorization", "Bearer " + accessToken))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        assertTrue(body.contains("\"name\":\"종합설계1\""));
        assertTrue(body.contains("\"gradeCd\":\"A+\""));
    }

    @Test
    void getAllCoursesReturnsBothRegisteredAndCompleted() throws Exception {
        String body = mockMvc.perform(get("/api/all-courses").header("Authorization", "Bearer " + accessToken))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        assertTrue(body.contains("자료구조"));
        assertTrue(body.contains("종합설계1"));
    }

    @Test
    void unauthenticatedRequestReturns401() throws Exception {
        mockMvc.perform(get("/api/term"))
                .andExpect(status().isUnauthorized());
    }
}
