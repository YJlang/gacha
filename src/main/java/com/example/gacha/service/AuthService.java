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
 * 인증 관련 서비스 클래스입니다.
 * 회원가입, 로그인 및 JWT 토큰으로 사용자 정보를 처리합니다.
 */
@Service // 해당 클래스를 Spring의 Service Bean으로 등록합니다.
@RequiredArgsConstructor // final이 붙은 필드들에 대해 생성자를 자동 생성해줍니다.
@Transactional(readOnly = true) // 클래스의 기본 트랜잭션 속성을 읽기 전용으로 지정합니다.
@Slf4j // 자동으로 로그 객체(log)를 생성해줍니다.
public class AuthService {

    // 의존성 주입: 생성자 주입방식
    private final UserRepository userRepository; // 사용자 관련 DB 처리를 위한 Repository
    private final PasswordUtil passwordUtil;     // 비밀번호 암호화 및 검증을 위한 유틸리티
    private final JwtUtil jwtUtil;               // JWT 토큰 관련 유틸리티

    /**
     * 회원가입 메서드
     * @param request 회원가입 요청(pojo 형태로 전달)
     * @return AuthResponse 생성된 사용자 정보(JWT 미포함)
     * @throws BusinessException 아이디 또는 이메일이 이미 있으면 발생
     */
    @Transactional // 쓰기 작업이므로 별도 트랜잭션 어노테이션을 붙임
    public AuthResponse signup(SignupRequest request) {
        log.info("Signup attempt for username: {}", request.getUsername());

        // 아이디(Username) 중복 체크
        if (userRepository.existsByUsername(request.getUsername())) {
            log.warn("Username already exists: {}", request.getUsername());
            throw new BusinessException(ErrorCode.USERNAME_ALREADY_EXISTS); // 예외 발생
        }

        // 이메일 중복 체크
        if (userRepository.existsByEmail(request.getEmail())) {
            log.warn("Email already exists: {}", request.getEmail());
            throw new BusinessException(ErrorCode.EMAIL_ALREADY_EXISTS); // 예외 발생
        }

        // 비밀번호 암호화 처리
        String encodedPassword = passwordUtil.encode(request.getPassword());

        // User 엔티티 객체 생성 (빌더 패턴)
        User user = User.builder()
                .username(request.getUsername())
                .password(encodedPassword) // 암호화된 비밀번호 저장
                .email(request.getEmail())
                .build();

        // DB에 사용자 저장 (INSERT)
        User savedUser = userRepository.save(user);

        log.info("User created successfully: userId={}, username={}", savedUser.getUserId(), savedUser.getUsername());

        // 생성된 사용자 정보로 DTO 응답 생성
        return AuthResponse.from(savedUser);
    }

    /**
     * 로그인 메서드
     * @param request 로그인 요청 정보(아이디, 비밀번호)
     * @return AuthResponse(JWT 토큰 포함)
     * @throws BusinessException 인증 실패시 발생
     */
    @Transactional // 쓰기 작업(로그인 기록 등)이 추가될 수 있으니 명시적으로 선언
    public AuthResponse login(LoginRequest request) {
        log.info("Login attempt for username: {}", request.getUsername());

        // 사용자를 아이디로 조회 (없으면 예외 발생)
        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> {
                    log.warn("Login failed: User not found - {}", request.getUsername());
                    return new BusinessException(ErrorCode.INVALID_CREDENTIALS);
                });

        // 입력한 비밀번호와 저장된 비밀번호(암호화) 비교
        if (!passwordUtil.matches(request.getPassword(), user.getPassword())) {
            log.warn("Login failed: Invalid password for user - {}", request.getUsername());
            throw new BusinessException(ErrorCode.INVALID_CREDENTIALS); // 인증 실패 시 예외
        }

        // 인증 성공시 JWT 토큰 생성 (userId, username 포함)
        String token = jwtUtil.generateToken(user.getUserId(), user.getUsername());

        log.info("User logged in successfully: userId={}, username={}", user.getUserId(), user.getUsername());

        // 사용자 정보 + JWT 토큰을 담아 응답
        return AuthResponse.of(user, token);
    }

    /**
     * JWT 토큰을 이용해 사용자 정보를 조회합니다.
     * @param token JWT 토큰 문자열
     * @return User 엔티티
     * @throws BusinessException 토큰이 유효하지 않거나 사용자가 존재하지 않을 때 예외 발생
     */
    public User getUserByToken(String token) {
        // 토큰 유효성 검증
        if (!jwtUtil.validateToken(token)) {
            throw new BusinessException(ErrorCode.INVALID_TOKEN); // 유효하지 않으면 예외
        }

        // 토큰에서 사용자 PK(userId) 추출
        Long userId = jwtUtil.getUserIdFromToken(token);

        // PK로 사용자 조회 (없으면 예외)
        return userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));
    }
}
