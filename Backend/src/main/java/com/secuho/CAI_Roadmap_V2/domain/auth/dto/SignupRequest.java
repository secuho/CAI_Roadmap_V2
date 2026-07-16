package com.secuho.CAI_Roadmap_V2.domain.auth.dto;

import jakarta.validation.constraints.NotBlank;

public record SignupRequest(
        @NotBlank(message = "학번을 입력해주세요.") String studentId,
        @NotBlank(message = "비밀번호를 입력해주세요.") String password,
        String track
) {
}
