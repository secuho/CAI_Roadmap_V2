package com.secuho.CAI_Roadmap_V2.domain.course.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * V1의 수강기록용 {@code courses} 테이블 + 학기별 공개 카탈로그({@code courses_2025termN}) 필드를
 * 하나의 엔티티로 통합. 자연키(sbjNo, dvcls, openYear, semCd) 자체가 "그 학기에 개설된 그 분반" 단위라
 * 교수/강의실/학점이 학기마다 달라져도 별도 행으로 자연스럽게 구분된다.
 */
@Entity
@Table(name = "courses", uniqueConstraints = @UniqueConstraint(columnNames = {"sbj_no", "dvcls", "open_year", "sem_cd"}))
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Course {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "sbj_no", nullable = false)
    private String sbjNo;

    @Column(nullable = false)
    private String dvcls;

    @Column(nullable = false)
    private String name;

    @Column(name = "cpdv_nm")
    private String cpdvNm;

    @Column(name = "dept_all_nm")
    private String deptAllNm;

    @Column(name = "dpt_nm")
    private String dptNm;

    @Column(name = "colg_nm")
    private String colgNm;

    @Column(name = "cors_nm")
    private String corsNm;

    @Column(name = "detl_curi_nm")
    private String detlCuriNm;

    @Column(name = "main_prof_nm")
    private String mainProfNm;

    private Double credit;

    @Column(name = "open_year", nullable = false)
    private Integer openYear;

    @Column(name = "sem_cd", nullable = false)
    private String semCd;

    @Column(name = "open_yy_sem")
    private String openYySem;

    @Column(name = "timetable_kor")
    private String timetableKor;

    @Column(name = "timetable_eng")
    private String timetableEng;

    @Column(name = "recrs_recod_yn")
    private String recrsRecodYn;

    @Column(name = "sys_ins_dttm")
    private String sysInsDttm;

    @Column(name = "cpdiv_cd")
    private String cpdivCd;

    @Column(name = "cpdiv_cd_nm")
    private String cpdivCdNm;

    @Column(name = "detl_curi_cd")
    private String detlCuriCd;

    @Column(name = "detl_curi_cd_nm")
    private String detlCuriCdNm;

    @Column(name = "obj_schgrd")
    private String objSchgrd;

    @Column(name = "lesn_sty_cd")
    private String lesnStyCd;

    @Column(name = "foreign_language_course", nullable = false)
    private boolean foreignLanguageCourse;

    private String classroom;

    @Column(name = "recod_grd_typ_cd_nm")
    private String recodGrdTypCdNm;

    @Column(name = "recod_eval_meth_cd_nm")
    private String recodEvalMethCdNm;

    @Column(name = "prof_kor_dsc")
    private String profKorDsc;

    /** TKCRS_PCNT — 수강정원. V1은 이 필드를 안 쓰고 50으로 하드코딩했으나, 실 데이터에 존재해 V2는 실값을 사용. */
    private Integer capacity;

    @Column(name = "capacity_limited")
    private String capacityLimited;

    @Column(name = "cancelled_reason")
    private String cancelledReason;

    @Column(name = "cancelled_date")
    private String cancelledDate;

    @Column(name = "name_eng")
    private String nameEng;

    @Column(name = "description_kor", columnDefinition = "TEXT")
    private String descriptionKor;

    @Column(name = "description_eng", columnDefinition = "TEXT")
    private String descriptionEng;

    @Column(name = "classroom_eng")
    private String classroomEng;

    @Column(name = "plan_url")
    private String planUrl;

    @Builder
    public Course(String sbjNo, String dvcls, String name, String cpdvNm, String deptAllNm, String dptNm,
                  String colgNm, String corsNm, String detlCuriNm, String mainProfNm, Double credit,
                  Integer openYear, String semCd, String openYySem, String timetableKor, String timetableEng,
                  String recrsRecodYn, String sysInsDttm, String cpdivCd, String cpdivCdNm, String detlCuriCd,
                  String detlCuriCdNm, String objSchgrd, String lesnStyCd, String classroom,
                  String recodGrdTypCdNm, String recodEvalMethCdNm, String profKorDsc,
                  Integer capacity, String capacityLimited, String cancelledReason, String cancelledDate,
                  String nameEng, String descriptionKor, String descriptionEng, String classroomEng, String planUrl) {
        this.sbjNo = sbjNo;
        this.dvcls = dvcls;
        this.name = name;
        this.cpdvNm = cpdvNm;
        this.deptAllNm = deptAllNm;
        this.dptNm = dptNm;
        this.colgNm = colgNm;
        this.corsNm = corsNm;
        this.detlCuriNm = detlCuriNm;
        this.mainProfNm = mainProfNm;
        this.credit = credit;
        this.openYear = openYear;
        this.semCd = semCd;
        this.openYySem = openYySem;
        this.timetableKor = timetableKor;
        this.timetableEng = timetableEng;
        this.recrsRecodYn = recrsRecodYn;
        this.sysInsDttm = sysInsDttm;
        this.cpdivCd = cpdivCd;
        this.cpdivCdNm = cpdivCdNm;
        this.detlCuriCd = detlCuriCd;
        this.detlCuriCdNm = detlCuriCdNm;
        this.objSchgrd = objSchgrd;
        this.lesnStyCd = lesnStyCd;
        this.foreignLanguageCourse = "외국어강의".equals(lesnStyCd);
        this.classroom = classroom;
        this.recodGrdTypCdNm = recodGrdTypCdNm;
        this.recodEvalMethCdNm = recodEvalMethCdNm;
        this.profKorDsc = profKorDsc;
        this.capacity = capacity;
        this.capacityLimited = capacityLimited;
        this.cancelledReason = cancelledReason;
        this.cancelledDate = cancelledDate;
        this.nameEng = nameEng;
        this.descriptionKor = descriptionKor;
        this.descriptionEng = descriptionEng;
        this.classroomEng = classroomEng;
        this.planUrl = planUrl;
    }
}
