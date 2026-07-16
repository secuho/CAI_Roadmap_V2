package com.secuho.CAI_Roadmap_V2.domain.course.dto;

import com.secuho.CAI_Roadmap_V2.domain.course.entity.Enrollment;

public record CompletedCourseResponse(
        String openYySem,
        Integer year,
        String gradeCd,
        Double credit,
        String sbjNo,
        String dvcls,
        String name,
        String cpdvNm,
        String mainProfNm
) {
    public static CompletedCourseResponse from(Enrollment enrollment) {
        var course = enrollment.getCourse();
        return new CompletedCourseResponse(
                enrollment.getOpenYySem(),
                enrollment.getYear(),
                enrollment.getGradeCd(),
                course.getCredit(),
                course.getSbjNo(),
                course.getDvcls(),
                course.getName(),
                course.getCpdvNm(),
                course.getMainProfNm()
        );
    }
}
