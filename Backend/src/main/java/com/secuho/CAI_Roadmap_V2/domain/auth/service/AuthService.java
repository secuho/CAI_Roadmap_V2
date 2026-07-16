package com.secuho.CAI_Roadmap_V2.domain.auth.service;

import com.secuho.CAI_Roadmap_V2.domain.auth.dto.LoginRequest;
import com.secuho.CAI_Roadmap_V2.domain.auth.dto.LogoutRequest;
import com.secuho.CAI_Roadmap_V2.domain.auth.dto.RefreshRequest;
import com.secuho.CAI_Roadmap_V2.domain.auth.dto.SignupRequest;
import com.secuho.CAI_Roadmap_V2.domain.auth.dto.TokenResponse;
import com.secuho.CAI_Roadmap_V2.domain.auth.dto.WithdrawRequest;
import com.secuho.CAI_Roadmap_V2.domain.auth.entity.RefreshToken;
import com.secuho.CAI_Roadmap_V2.domain.auth.entity.Student;
import com.secuho.CAI_Roadmap_V2.domain.auth.entity.User;
import com.secuho.CAI_Roadmap_V2.domain.auth.repository.RefreshTokenRepository;
import com.secuho.CAI_Roadmap_V2.domain.auth.repository.StudentRepository;
import com.secuho.CAI_Roadmap_V2.domain.auth.repository.UserRepository;
import com.secuho.CAI_Roadmap_V2.domain.course.repository.EnrollmentRepository;
import com.secuho.CAI_Roadmap_V2.domain.course.repository.TermSummaryRepository;
import com.secuho.CAI_Roadmap_V2.domain.ndrims.NdrimsClient;
import com.secuho.CAI_Roadmap_V2.domain.ndrims.NdrimsVerificationResult;
import com.secuho.CAI_Roadmap_V2.global.exception.BusinessException;
import com.secuho.CAI_Roadmap_V2.global.exception.ErrorCode;
import com.secuho.CAI_Roadmap_V2.global.jwt.JwtProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.util.HexFormat;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AuthService {

    private final UserRepository userRepository;
    private final StudentRepository studentRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final EnrollmentRepository enrollmentRepository;
    private final TermSummaryRepository termSummaryRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtProvider jwtProvider;
    private final NdrimsClient ndrimsClient;

    @Transactional
    public TokenResponse signup(SignupRequest request) {
        if (userRepository.existsByStudentId(request.studentId())) {
            throw new BusinessException(ErrorCode.DUPLICATE_STUDENT_ID);
        }

        NdrimsVerificationResult verification = ndrimsClient.verify(request.studentId(), request.password());
        if (!verification.success()) {
            throw new BusinessException(ErrorCode.NDRIMS_VERIFICATION_FAILED, verification.message());
        }

        User user = User.builder()
                .studentId(request.studentId())
                .password(passwordEncoder.encode(request.password()))
                .track(request.track())
                .build();
        userRepository.save(user);
        studentRepository.save(Student.builder().user(user).build());

        return issueTokens(user);
    }

    @Transactional
    public TokenResponse login(LoginRequest request) {
        User user = userRepository.findByStudentId(request.studentId())
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        if (!passwordEncoder.matches(request.password(), user.getPassword())) {
            throw new BusinessException(ErrorCode.INVALID_PASSWORD);
        }

        return issueTokens(user);
    }

    @Transactional
    public TokenResponse refresh(RefreshRequest request) {
        String tokenHash = hash(request.refreshToken());
        RefreshToken stored = refreshTokenRepository.findByTokenHash(tokenHash)
                .orElseThrow(() -> new BusinessException(ErrorCode.INVALID_REFRESH_TOKEN));

        if (stored.isExpired()) {
            refreshTokenRepository.delete(stored);
            throw new BusinessException(ErrorCode.EXPIRED_REFRESH_TOKEN);
        }

        if (!jwtProvider.isValid(request.refreshToken())) {
            refreshTokenRepository.delete(stored);
            throw new BusinessException(ErrorCode.INVALID_REFRESH_TOKEN);
        }

        String studentId = jwtProvider.getStudentId(request.refreshToken());
        User user = userRepository.findByStudentId(studentId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        refreshTokenRepository.delete(stored);
        refreshTokenRepository.flush();

        return issueTokens(user);
    }

    @Transactional
    public void logout(LogoutRequest request) {
        refreshTokenRepository.deleteByTokenHash(hash(request.refreshToken()));
    }

    @Transactional
    public void withdraw(String studentId, WithdrawRequest request) {
        User user = userRepository.findByStudentId(studentId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        if (!passwordEncoder.matches(request.password(), user.getPassword())) {
            throw new BusinessException(ErrorCode.INVALID_PASSWORD);
        }

        studentRepository.findByUserId(user.getId()).ifPresent(studentRepository::delete);
        enrollmentRepository.deleteByUser(user);
        termSummaryRepository.deleteByUser(user);
        refreshTokenRepository.deleteByUserId(user.getId());
        userRepository.delete(user);
    }

    private TokenResponse issueTokens(User user) {
        String accessToken = jwtProvider.createAccessToken(user.getStudentId());
        String refreshToken = jwtProvider.createRefreshToken(user.getStudentId());

        RefreshToken tokenEntity = RefreshToken.builder()
                .userId(user.getId())
                .tokenHash(hash(refreshToken))
                .expiresAt(LocalDateTime.now().plusSeconds(jwtProvider.getRefreshTokenExpirationMs() / 1000))
                .build();
        refreshTokenRepository.save(tokenEntity);

        return TokenResponse.of(accessToken, refreshToken, jwtProvider.getAccessTokenExpirationMs());
    }

    private String hash(String value) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hashed = digest.digest(value.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(hashed);
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("SHA-256 알고리즘을 사용할 수 없습니다.", e);
        }
    }
}
