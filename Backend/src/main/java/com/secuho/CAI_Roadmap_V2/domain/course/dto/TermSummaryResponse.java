package com.secuho.CAI_Roadmap_V2.domain.course.dto;

import com.secuho.CAI_Roadmap_V2.domain.course.entity.TermSummary;

public record TermSummaryResponse(
        Integer year,
        String semCd,
        String yySemName,
        Double appliedCdt,
        Double gainedCdt,
        Double gpa,
        String rankText,
        String deptRank,
        Double perSco
) {
    public static TermSummaryResponse from(TermSummary termSummary) {
        return new TermSummaryResponse(
                termSummary.getYear(),
                termSummary.getSemCd(),
                termSummary.getYySemName(),
                termSummary.getAppliedCdt(),
                termSummary.getGainedCdt(),
                termSummary.getGpa(),
                termSummary.getRankText(),
                termSummary.getDeptRank(),
                termSummary.getPerSco()
        );
    }
}
