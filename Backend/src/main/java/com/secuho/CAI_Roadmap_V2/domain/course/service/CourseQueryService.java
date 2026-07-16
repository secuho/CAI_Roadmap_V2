package com.secuho.CAI_Roadmap_V2.domain.course.service;

import com.secuho.CAI_Roadmap_V2.domain.auth.entity.User;
import com.secuho.CAI_Roadmap_V2.domain.course.dto.CompletedCourseResponse;
import com.secuho.CAI_Roadmap_V2.domain.course.dto.CurrentCourseResponse;
import com.secuho.CAI_Roadmap_V2.domain.course.dto.TermSummaryResponse;
import com.secuho.CAI_Roadmap_V2.domain.course.entity.EnrollmentStatus;
import com.secuho.CAI_Roadmap_V2.domain.course.repository.EnrollmentRepository;
import com.secuho.CAI_Roadmap_V2.domain.course.repository.TermSummaryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CourseQueryService {

    private final EnrollmentRepository enrollmentRepository;
    private final TermSummaryRepository termSummaryRepository;

    public List<TermSummaryResponse> getTermSummaries(User user) {
        return termSummaryRepository.findByUserOrderByYearAscSemCdAsc(user).stream()
                .map(TermSummaryResponse::from)
                .toList();
    }

    public List<CurrentCourseResponse> getCurrentCourses(User user, Integer year, String term) {
        return enrollmentRepository.findByUserAndStatusAndYearAndSemCd(user, EnrollmentStatus.REGISTERED, year, term)
                .stream()
                .map(enrollment -> CurrentCourseResponse.from(enrollment.getCourse()))
                .toList();
    }

    public List<CompletedCourseResponse> getCompletedCourses(User user) {
        return enrollmentRepository.findByUserAndStatus(user, EnrollmentStatus.COMPLETED).stream()
                .map(CompletedCourseResponse::from)
                .toList();
    }

    public List<String> getAllCourseNames(User user) {
        return enrollmentRepository.findByUser(user).stream()
                .map(enrollment -> enrollment.getCourse().getName())
                .distinct()
                .sorted(Comparator.naturalOrder())
                .toList();
    }
}
