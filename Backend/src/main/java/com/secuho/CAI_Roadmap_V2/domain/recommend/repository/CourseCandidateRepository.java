package com.secuho.CAI_Roadmap_V2.domain.recommend.repository;

import com.secuho.CAI_Roadmap_V2.domain.recommend.entity.CourseCandidate;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CourseCandidateRepository extends JpaRepository<CourseCandidate, Long> {
}
