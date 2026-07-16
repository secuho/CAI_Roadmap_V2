package com.secuho.CAI_Roadmap_V2.domain.course.entity;

import com.secuho.CAI_Roadmap_V2.domain.auth.entity.User;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
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

@Entity
@Table(name = "term_summaries", uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "year", "sem_cd"}))
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class TermSummary {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "summary_year", nullable = false)
    private Integer year;

    @Column(name = "sem_cd", nullable = false)
    private String semCd;

    @Column(name = "yy_sem_name")
    private String yySemName;

    @Column(name = "applied_cdt")
    private Double appliedCdt;

    @Column(name = "gained_cdt")
    private Double gainedCdt;

    private Double gpa;

    @Column(name = "rank_text")
    private String rankText;

    @Column(name = "dept_rank")
    private String deptRank;

    @Column(name = "per_sco")
    private Double perSco;

    @Builder
    public TermSummary(User user, Integer year, String semCd, String yySemName, Double appliedCdt,
                        Double gainedCdt, Double gpa, String rankText, String deptRank, Double perSco) {
        this.user = user;
        this.year = year;
        this.semCd = semCd;
        this.yySemName = yySemName;
        this.appliedCdt = appliedCdt;
        this.gainedCdt = gainedCdt;
        this.gpa = gpa;
        this.rankText = rankText;
        this.deptRank = deptRank;
        this.perSco = perSco;
    }
}
