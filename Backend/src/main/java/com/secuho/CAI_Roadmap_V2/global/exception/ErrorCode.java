package com.secuho.CAI_Roadmap_V2.global.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ErrorCode {

    DUPLICATE_STUDENT_ID(HttpStatus.CONFLICT, "이미 등록된 학번입니다."),
    USER_NOT_FOUND(HttpStatus.UNAUTHORIZED, "등록되지 않은 사용자입니다."),
    INVALID_PASSWORD(HttpStatus.UNAUTHORIZED, "비밀번호가 일치하지 않습니다."),
    INVALID_REFRESH_TOKEN(HttpStatus.UNAUTHORIZED, "유효하지 않은 리프레시 토큰입니다."),
    EXPIRED_REFRESH_TOKEN(HttpStatus.UNAUTHORIZED, "만료된 리프레시 토큰입니다."),
    NDRIMS_VERIFICATION_FAILED(HttpStatus.BAD_REQUEST, "NDRIMS 포털 인증에 실패했습니다."),
    INVALID_NEXT_TERM(HttpStatus.BAD_REQUEST, "nextTerm은 'YYYY-N' 형식이어야 합니다. (예: 2025-2)");

    private final HttpStatus status;
    private final String defaultMessage;

    ErrorCode(HttpStatus status, String defaultMessage) {
        this.status = status;
        this.defaultMessage = defaultMessage;
    }
}
