package com.secuho.CAI_Roadmap_V2.domain.course.repository;

import com.secuho.CAI_Roadmap_V2.domain.course.entity.Course;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CourseRepository extends JpaRepository<Course, Long> {

    Optional<Course> findBySbjNoAndDvclsAndOpenYearAndSemCd(String sbjNo, String dvcls, Integer openYear, String semCd);

    List<Course> findBySbjNoAndOpenYearAndSemCd(String sbjNo, Integer openYear, String semCd);

    boolean existsBySbjNoAndOpenYearAndSemCd(String sbjNo, Integer openYear, String semCd);
}
