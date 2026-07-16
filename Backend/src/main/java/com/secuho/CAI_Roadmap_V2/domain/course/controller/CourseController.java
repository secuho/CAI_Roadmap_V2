package com.secuho.CAI_Roadmap_V2.domain.course.controller;

import com.secuho.CAI_Roadmap_V2.domain.auth.CurrentUserResolver;
import com.secuho.CAI_Roadmap_V2.domain.course.dto.CompletedCourseResponse;
import com.secuho.CAI_Roadmap_V2.domain.course.dto.CurrentCourseResponse;
import com.secuho.CAI_Roadmap_V2.domain.course.dto.TermSummaryResponse;
import com.secuho.CAI_Roadmap_V2.domain.course.service.CourseQueryService;
import com.secuho.CAI_Roadmap_V2.global.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class CourseController {

    private final CourseQueryService courseQueryService;
    private final CurrentUserResolver currentUserResolver;

    @GetMapping("/term")
    public ApiResponse<List<TermSummaryResponse>> getTerm(Authentication authentication) {
        return ApiResponse.success(courseQueryService.getTermSummaries(currentUserResolver.resolve(authentication)));
    }

    @GetMapping("/current-courses")
    public ApiResponse<List<CurrentCourseResponse>> getCurrentCourses(
            Authentication authentication,
            @RequestParam Integer year,
            @RequestParam String term) {
        return ApiResponse.success(courseQueryService.getCurrentCourses(currentUserResolver.resolve(authentication), year, term));
    }

    @GetMapping("/completed-courses")
    public ApiResponse<List<CompletedCourseResponse>> getCompletedCourses(Authentication authentication) {
        return ApiResponse.success(courseQueryService.getCompletedCourses(currentUserResolver.resolve(authentication)));
    }

    @GetMapping("/all-courses")
    public ApiResponse<List<String>> getAllCourses(Authentication authentication) {
        return ApiResponse.success(courseQueryService.getAllCourseNames(currentUserResolver.resolve(authentication)));
    }
}
