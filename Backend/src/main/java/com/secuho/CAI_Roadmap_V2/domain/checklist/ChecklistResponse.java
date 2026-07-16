package com.secuho.CAI_Roadmap_V2.domain.checklist;

import java.util.List;

public record ChecklistResponse(
        double major,
        double common,
        double gpa,
        double totalCredit,
        int engCredit,
        double basic,
        double bsm,
        int design1,
        int design2,
        boolean basicCompleted,
        List<String> missingBasicCourses,
        boolean bsmCompleted,
        List<String> missingBsmCourses,
        String experimentCourseStatus,
        boolean commonDonggukAttitude,
        boolean commonSelfDev,
        boolean commonThinkncom,
        boolean commonCreative,
        boolean commonDigitaliter,
        List<String> missingCommonDonggukAttitude,
        List<String> missingCommonSelfDev,
        List<String> missingCommonThinkncom,
        List<String> missingCommonCreative,
        List<String> missingCommonDigitaliter
) {
}
