package com.secuho.CAI_Roadmap_V2.domain.course.repository;

import com.secuho.CAI_Roadmap_V2.domain.auth.entity.User;
import com.secuho.CAI_Roadmap_V2.domain.course.entity.Enrollment;
import com.secuho.CAI_Roadmap_V2.domain.course.entity.EnrollmentStatus;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EnrollmentRepository extends JpaRepository<Enrollment, Long> {

    @EntityGraph(attributePaths = "course")
    List<Enrollment> findByUserAndStatus(User user, EnrollmentStatus status);

    @EntityGraph(attributePaths = "course")
    List<Enrollment> findByUserAndStatusAndYearAndSemCd(User user, EnrollmentStatus status, Integer year, String semCd);

    @EntityGraph(attributePaths = "course")
    List<Enrollment> findByUser(User user);

    void deleteByUser(User user);
}
