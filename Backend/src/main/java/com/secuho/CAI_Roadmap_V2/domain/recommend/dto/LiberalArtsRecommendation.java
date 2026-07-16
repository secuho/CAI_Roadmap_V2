package com.secuho.CAI_Roadmap_V2.domain.recommend.dto;

public record LiberalArtsRecommendation(
        String code,
        String name,
        Double credit,
        Double totalCredit,
        String midArea,
        String category,
        String description
) {
}
