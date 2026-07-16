package com.secuho.CAI_Roadmap_V2.domain.course.repository;

import com.secuho.CAI_Roadmap_V2.domain.auth.entity.User;
import com.secuho.CAI_Roadmap_V2.domain.course.entity.TermSummary;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TermSummaryRepository extends JpaRepository<TermSummary, Long> {

    List<TermSummary> findByUserOrderByYearAscSemCdAsc(User user);

    void deleteByUser(User user);
}
