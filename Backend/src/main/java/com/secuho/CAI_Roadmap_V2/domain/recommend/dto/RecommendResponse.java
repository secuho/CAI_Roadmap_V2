package com.secuho.CAI_Roadmap_V2.domain.recommend.dto;

import java.util.List;

public record RecommendResponse(
        List<MajorRecommendation> majors,
        List<LiberalArtsRecommendation> liberalArts
) {
}
