package com.secuho.CAI_Roadmap_V2.domain.auth.dto;

import jakarta.validation.constraints.NotBlank;

public record WithdrawRequest(
        @NotBlank(message = "비밀번호를 입력해주세요.") String password
) {
}
