package com.secuho.CAI_Roadmap_V2.domain.recommend.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 교양 추천 후보 과목 마스터. V1의 {@code courses.course_cat}을 포팅. 지금은 스키마만 두고
 * 데이터는 비어 있음 — 실 데이터 적재 경로는 아직 없음.
 */
@Entity
@Table(name = "liberal_arts_courses")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class LiberalArtsCourse {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String code;

    private String name;

    private Double credit;

    /** 중영역 내 이수 가능한 총학점 상한. */
    @Column(name = "total_credit")
    private Double totalCredit;

    /** 동일과목(등가) 그룹 식별자, nullable. */
    @Column(name = "same_group")
    private String sameGroup;

    /** 중영역, nullable. */
    @Column(name = "mid_area")
    private String midArea;

    private String category;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Builder
    public LiberalArtsCourse(String code, String name, Double credit, Double totalCredit, String sameGroup,
                             String midArea, String category, String description) {
        this.code = code;
        this.name = name;
        this.credit = credit;
        this.totalCredit = totalCredit;
        this.sameGroup = sameGroup;
        this.midArea = midArea;
        this.category = category;
        this.description = description;
    }
}
