package com.secuho.CAI_Roadmap_V2.domain.auth.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * V1 {@code students} 테이블을 그대로 포팅한 개인정보 프로필.
 * NDRIMS 실 연동(Phase 4) 전까지는 대부분 null이며, signup 시 빈 행만 생성된다.
 * 관리자용 프로필 다양화를 위해 암호화 없이 원본 그대로 저장하기로 결정됨 — 추후 개인정보 보호 조치 필요(ROADMAP.md 참고).
 */
@Entity
@Table(name = "students")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Student {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    private String name;

    @Column(name = "std_nm")
    private String stdNm;

    private String email;
    private String phone;
    private String sex;
    private String campus;

    @Column(name = "entry_date")
    private String entryDate;

    @Column(name = "entry_yy_sem")
    private String entryYySem;

    @Column(name = "foreign_yn")
    private String foreignYn;

    @Column(name = "club_name")
    private String clubName;

    @Column(name = "address_full")
    private String addressFull;

    @Column(name = "bank_name")
    private String bankName;

    @Column(name = "bank_account_no")
    private String bankAccountNo;

    @Column(name = "level_test_teps")
    private String levelTestTeps;

    @Column(name = "deep_yn")
    private String deepYn;

    @Column(name = "ent_topik_grd_cd_nm")
    private String entTopikGrdCdNm;

    @Column(name = "grad_topik_grd_cd_nm")
    private String gradTopikGrdCdNm;

    @Column(name = "grad_topik_gain_dt")
    private String gradTopikGainDt;

    @Column(name = "ent_topik_gain_dt")
    private String entTopikGainDt;

    private String college;
    private String faculty;
    private String major;

    @Column(name = "dpt_mjr_nm_raw")
    private String dptMjrNmRaw;

    private String birth;

    @Column(name = "rsdn_dt_sex_raw")
    private String rsdnDtSexRaw;

    @Column(name = "mrks_avg")
    private String mrksAvg;

    @Builder
    public Student(User user) {
        this.user = user;
    }
}
