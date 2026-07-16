package com.secuho.CAI_Roadmap_V2.domain.recommend.entity;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.HashSet;
import java.util.Set;

/**
 * 전공 추천 후보 과목 마스터. V1의 {@code courses.courses} + {@code curriculum.courses}(capacity) +
 * {@code curriculum.track_courses}(트랙 매핑)를 하나로 통합. 지금은 스키마만 두고 데이터는 비어 있음 —
 * 실 데이터 적재 경로는 아직 없음(Phase 4 또는 향후 관리자 도구에서 채울 예정).
 */
@Entity
@Table(name = "course_candidates")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CourseCandidate {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String code;

    private String title;

    private Double credit;

    /** 콤마로 구분된 선수과목 코드 목록. */
    private String prereq;

    /** V1 T1.enrollment — 추천 점수의 score_unlock(선호도/신청 지표)으로 사용. */
    @Column(name = "enrollment_score")
    private Double enrollmentScore;

    /** curriculum.courses.capacity — (capacity IS NULL OR capacity > -1) 필터에만 사용. */
    private Integer capacity;

    @Column(columnDefinition = "TEXT")
    private String description;

    @ElementCollection
    @CollectionTable(name = "course_candidate_tracks", joinColumns = @JoinColumn(name = "course_candidate_id"))
    @Column(name = "track_name")
    private Set<String> trackNames = new HashSet<>();

    @Builder
    public CourseCandidate(String code, String title, Double credit, String prereq, Double enrollmentScore,
                           Integer capacity, String description, Set<String> trackNames) {
        this.code = code;
        this.title = title;
        this.credit = credit;
        this.prereq = prereq;
        this.enrollmentScore = enrollmentScore;
        this.capacity = capacity;
        this.description = description;
        this.trackNames = trackNames != null ? trackNames : new HashSet<>();
    }
}
