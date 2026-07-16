package com.secuho.CAI_Roadmap_V2.domain.checklist;

import com.secuho.CAI_Roadmap_V2.domain.auth.CurrentUserResolver;
import com.secuho.CAI_Roadmap_V2.global.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class ChecklistController {

    private final ChecklistService checklistService;
    private final CurrentUserResolver currentUserResolver;

    @GetMapping("/checklist")
    public ApiResponse<ChecklistResponse> getChecklist(Authentication authentication) {
        return ApiResponse.success(checklistService.getChecklist(currentUserResolver.resolve(authentication)));
    }
}
