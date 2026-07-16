package com.secuho.CAI_Roadmap_V2.domain.auth.controller;

import com.secuho.CAI_Roadmap_V2.domain.auth.dto.LoginRequest;
import com.secuho.CAI_Roadmap_V2.domain.auth.dto.LogoutRequest;
import com.secuho.CAI_Roadmap_V2.domain.auth.dto.RefreshRequest;
import com.secuho.CAI_Roadmap_V2.domain.auth.dto.SignupRequest;
import com.secuho.CAI_Roadmap_V2.domain.auth.dto.TokenResponse;
import com.secuho.CAI_Roadmap_V2.domain.auth.dto.WithdrawRequest;
import com.secuho.CAI_Roadmap_V2.domain.auth.service.AuthService;
import com.secuho.CAI_Roadmap_V2.global.response.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<TokenResponse>> login(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(ApiResponse.success(authService.login(request)));
    }

    @PostMapping("/account")
    public ResponseEntity<ApiResponse<TokenResponse>> signup(@Valid @RequestBody SignupRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(authService.signup(request)));
    }

    @DeleteMapping("/account")
    public ResponseEntity<ApiResponse<Void>> withdraw(Authentication authentication,
                                                        @Valid @RequestBody WithdrawRequest request) {
        authService.withdraw(authentication.getName(), request);
        return ResponseEntity.ok(ApiResponse.success(null));
    }

    @PostMapping("/refresh")
    public ResponseEntity<ApiResponse<TokenResponse>> refresh(@Valid @RequestBody RefreshRequest request) {
        return ResponseEntity.ok(ApiResponse.success(authService.refresh(request)));
    }

    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<Void>> logout(@Valid @RequestBody LogoutRequest request) {
        authService.logout(request);
        return ResponseEntity.ok(ApiResponse.success(null));
    }
}
