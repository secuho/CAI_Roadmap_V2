package com.secuho.CAI_Roadmap_V2.domain.course.dto;

import com.secuho.CAI_Roadmap_V2.domain.course.entity.Course;

public record CurrentCourseResponse(
        String name,
        String mainProfNm,
        Double credit,
        String timetable,
        String classroom,
        String sbjNo,
        String dvcls,
        String cpdvNm
) {
    public static CurrentCourseResponse from(Course course) {
        return new CurrentCourseResponse(
                course.getName(),
                course.getMainProfNm(),
                course.getCredit(),
                course.getTimetableKor(),
                course.getClassroom(),
                course.getSbjNo(),
                course.getDvcls(),
                course.getCpdvNm()
        );
    }
}
