package com.example.gacha.controller;

import com.example.gacha.dto.request.LoginRequest;
import com.example.gacha.dto.request.SignupRequest;
import com.example.gacha.dto.response.ApiResponse;
import com.example.gacha.dto.response.AuthResponse;
import com.example.gacha.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

/**
 * 인증 API 컨트롤러 (회원가입, 로그인)
 */
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Slf4j
public class AuthController {

    private final AuthService authService;

    /**
     * 회원가입
     *
     * @param request 회원가입 요청 정보
     * @return 생성된 사용자 정보
     */
    @PostMapping("/signup")
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<AuthResponse> signup(@Valid @RequestBody SignupRequest request) {
        log.info("POST /api/auth/signup - username: {}", request.getUsername());

        AuthResponse response = authService.signup(request);

        return ApiResponse.success(response, "회원가입이 완료되었습니다.");
    }

    /**
     * 로그인
     *
     * @param request 로그인 요청 정보
     * @return JWT 토큰 및 사용자 정보
     */
    @PostMapping("/login")
    public ApiResponse<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        log.info("POST /api/auth/login - username: {}", request.getUsername());

        AuthResponse response = authService.login(request);

        return ApiResponse.success(response, "로그인 성공");
    }
}
