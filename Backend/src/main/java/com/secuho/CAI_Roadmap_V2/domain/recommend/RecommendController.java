package com.secuho.CAI_Roadmap_V2.domain.recommend;

import com.secuho.CAI_Roadmap_V2.domain.auth.CurrentUserResolver;
import com.secuho.CAI_Roadmap_V2.domain.recommend.dto.RecommendResponse;
import com.secuho.CAI_Roadmap_V2.global.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class RecommendController {

    private final RecommendService recommendService;
    private final CurrentUserResolver currentUserResolver;

    @GetMapping("/recommends")
    public ApiResponse<RecommendResponse> getRecommendations(Authentication authentication,
                                                               @RequestParam String nextTerm) {
        return ApiResponse.success(recommendService.getRecommendations(currentUserResolver.resolve(authentication), nextTerm));
    }
}
