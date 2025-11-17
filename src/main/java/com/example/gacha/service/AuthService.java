package com.example.gacha.service;

import com.example.gacha.domain.user.User;
import com.example.gacha.domain.user.UserRepository;
import com.example.gacha.dto.request.LoginRequest;
import com.example.gacha.dto.request.SignupRequest;
import com.example.gacha.dto.response.AuthResponse;
import com.example.gacha.exception.BusinessException;
import com.example.gacha.exception.ErrorCode;
import com.example.gacha.util.JwtUtil;
import com.example.gacha.util.PasswordUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 인증 서비스 (회원가입, 로그인)
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordUtil passwordUtil;
    private final JwtUtil jwtUtil;

    /**
     * 회원가입
     *
     * @param request 회원가입 요청 정보
     * @return 생성된 사용자 정보 (토큰 없음)
     * @throws BusinessException 아이디 또는 이메일 중복 시
     */
    @Transactional
    public AuthResponse signup(SignupRequest request) {
        log.info("Signup attempt for username: {}", request.getUsername());

        // 아이디 중복 확인
        if (userRepository.existsByUsername(request.getUsername())) {
            log.warn("Username already exists: {}", request.getUsername());
            throw new BusinessException(ErrorCode.USERNAME_ALREADY_EXISTS);
        }

        // 이메일 중복 확인
        if (userRepository.existsByEmail(request.getEmail())) {
            log.warn("Email already exists: {}", request.getEmail());
            throw new BusinessException(ErrorCode.EMAIL_ALREADY_EXISTS);
        }

        // 비밀번호 암호화
        String encodedPassword = passwordUtil.encode(request.getPassword());

        // 사용자 생성
        User user = User.builder()
                .username(request.getUsername())
                .password(encodedPassword)
                .email(request.getEmail())
                .build();

        User savedUser = userRepository.save(user);

        log.info("User created successfully: userId={}, username={}", savedUser.getUserId(), savedUser.getUsername());

        return AuthResponse.from(savedUser);
    }

    /**
     * 로그인
     *
     * @param request 로그인 요청 정보
     * @return JWT 토큰 및 사용자 정보
     * @throws BusinessException 인증 실패 시
     */
    @Transactional
    public AuthResponse login(LoginRequest request) {
        log.info("Login attempt for username: {}", request.getUsername());

        // 사용자 조회
        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> {
                    log.warn("Login failed: User not found - {}", request.getUsername());
                    return new BusinessException(ErrorCode.INVALID_CREDENTIALS);
                });

        // 비밀번호 확인
        if (!passwordUtil.matches(request.getPassword(), user.getPassword())) {
            log.warn("Login failed: Invalid password for user - {}", request.getUsername());
            throw new BusinessException(ErrorCode.INVALID_CREDENTIALS);
        }

        // JWT 토큰 생성
        String token = jwtUtil.generateToken(user.getUserId(), user.getUsername());

        log.info("User logged in successfully: userId={}, username={}", user.getUserId(), user.getUsername());

        return AuthResponse.of(user, token);
    }

    /**
     * 토큰으로 사용자 조회
     *
     * @param token JWT 토큰
     * @return 사용자 정보
     * @throws BusinessException 토큰이 유효하지 않거나 사용자를 찾을 수 없는 경우
     */
    public User getUserByToken(String token) {
        if (!jwtUtil.validateToken(token)) {
            throw new BusinessException(ErrorCode.INVALID_TOKEN);
        }

        Long userId = jwtUtil.getUserIdFromToken(token);

        return userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));
    }
}
