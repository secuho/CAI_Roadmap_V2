package com.secuho.CAI_Roadmap_V2.domain.course.entity;

import com.secuho.CAI_Roadmap_V2.domain.auth.entity.User;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * V1 {@code enrollments}. {@code credit}은 항상 {@code course.credit}과 동일한 값이 중복 저장되어 있었으므로
 * 컬럼을 두지 않고 {@link #getCourse()}.getCredit()}로 대체한다.
 */
@Entity
@Table(name = "enrollments", uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "course_id"}))
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Enrollment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "course_id", nullable = false)
    private Course course;

    @Column(name = "enroll_year", nullable = false)
    private Integer year;

    @Column(name = "sem_cd", nullable = false)
    private String semCd;

    @Column(name = "open_yy_sem")
    private String openYySem;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EnrollmentStatus status;

    @Column(name = "grade_cd")
    private String gradeCd;

    @Builder
    public Enrollment(User user, Course course, Integer year, String semCd, String openYySem,
                       EnrollmentStatus status, String gradeCd) {
        this.user = user;
        this.course = course;
        this.year = year;
        this.semCd = semCd;
        this.openYySem = openYySem;
        this.status = status;
        this.gradeCd = gradeCd;
    }
}
