package com.secuho.CAI_Roadmap_V2.domain.auth.repository;

import com.secuho.CAI_Roadmap_V2.domain.auth.entity.Student;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface StudentRepository extends JpaRepository<Student, Long> {

    Optional<Student> findByUserId(Long userId);
}
