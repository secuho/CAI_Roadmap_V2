package com.secuho.CAI_Roadmap_V2.domain.mypage;

import com.secuho.CAI_Roadmap_V2.domain.auth.CurrentUserResolver;
import com.secuho.CAI_Roadmap_V2.domain.mypage.dto.MyPageResponse;
import com.secuho.CAI_Roadmap_V2.domain.mypage.dto.MyTrackRequest;
import com.secuho.CAI_Roadmap_V2.global.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class MyPageController {

    private final MyPageService myPageService;
    private final CurrentUserResolver currentUserResolver;

    @GetMapping("/mypage")
    public ApiResponse<MyPageResponse> getMyPage(Authentication authentication) {
        return ApiResponse.success(myPageService.getMyPage(currentUserResolver.resolve(authentication)));
    }

    @PatchMapping("/mytrack")
    public ApiResponse<Void> updateTrack(Authentication authentication, @RequestBody MyTrackRequest request) {
        myPageService.updateTrack(currentUserResolver.resolve(authentication), request.track());
        return ApiResponse.success(null);
    }
}
