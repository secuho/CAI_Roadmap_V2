package com.secuho.CAI_Roadmap_V2.domain.recommend.dto;

import java.util.List;

public record MajorRecommendation(
        String name,
        String code,
        int recommendGrade,
        List<String> tracks,
        Double credit,
        String description,
        double score
) {
}
