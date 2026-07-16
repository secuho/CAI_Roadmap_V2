package com.secuho.CAI_Roadmap_V2.domain.auth.dto;

import jakarta.validation.constraints.NotBlank;

public record LogoutRequest(
        @NotBlank(message = "리프레시 토큰이 필요합니다.") String refreshToken
) {
}
