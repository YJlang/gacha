package com.example.gacha.controller;

// LoginRequest와 SignupRequest는 각각 로그인, 회원가입에 필요한 정보를 담는 DTO입니다.
import com.example.gacha.dto.request.LoginRequest;
import com.example.gacha.dto.request.SignupRequest;

// ApiResponse와 AuthResponse는 API 응답에 사용되는 DTO입니다.
import com.example.gacha.dto.response.ApiResponse;
import com.example.gacha.dto.response.AuthResponse;

// 인증 로직을 처리하는 Service 입니다.
import com.example.gacha.service.AuthService;

// javax.validation의 Valid 어노테이션으로 요청값 유효성 검사 수행
import jakarta.validation.Valid;

// Lombok 어노테이션입니다.
// @RequiredArgsConstructor는 final 필드에 대해 생성자를 자동 생성해줍니다.
// @Slf4j는 로그 객체(log)를 자동으로 생성해줍니다.
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

// Spring Web 관련 어노테이션들입니다.
// 아래의 어노테이션들은 각각의 목적이 있습니다.
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

/**
 * 인증 관련 API를 제공하는 컨트롤러입니다. (회원가입, 로그인)
 */
@RestController // 해당 클래스가 REST API의 컨트롤러임을 나타냅니다. JSON 형태로 객체를 반환합니다.
@RequestMapping("/api/auth") // 해당 클래스의 모든 메서드는 /api/auth로 시작하는 URL로 매핑됩니다.
@RequiredArgsConstructor // final로 선언된 필드(authService)에 대해 생성자를 자동 생성합니다.
@Slf4j // 로그를 사용할 수 있도록 지원합니다.
public class AuthController {

    // 인증 관련 비즈니스 로직을 담당하는 Service 레이어 입니다.
    private final AuthService authService;

    /**
     * 회원가입 API입니다.
     *
     * @param request 회원가입 요청 정보를 받는 DTO (유효성 검사가 적용됩니다)
     * @return 생성된 사용자 정보 (AuthResponse)와 상태 메시지를 ApiResponse 형태로 반환합니다.
     */
    @PostMapping("/signup") // HTTP POST /api/auth/signup 요청과 매핑됩니다.
    @ResponseStatus(HttpStatus.CREATED) // 성공 시 HTTP 201(CREATED) 상태 코드를 응답합니다.
    public ApiResponse<AuthResponse> signup(
            @Valid // 요청 바디의 유효성 검사 수행(예: NotBlank, Size 등)
            @RequestBody // HTTP 요청의 body에서 데이터를 받아옵니다.
            SignupRequest request) {
        // 요청 들어올 때 username을 로그로 남깁니다.
        log.info("POST /api/auth/signup - username: {}", request.getUsername());

        // 실제 회원가입 로직은 AuthService에서 처리합니다.
        AuthResponse response = authService.signup(request);

        // ApiResponse.success를 통해 사용자 정보와 메시지로 응답합니다.
        return ApiResponse.success(response, "회원가입이 완료되었습니다.");
    }

    /**
     * 로그인 API입니다.
     *
     * @param request 로그인 요청 정보를 받는 DTO (유효성 검사가 적용됩니다)
     * @return JWT 토큰 및 사용자 정보를 AuthResponse에 담아 ApiResponse로 반환합니다.
     */
    @PostMapping("/login") // HTTP POST /api/auth/login 요청과 매핑됩니다.
    public ApiResponse<AuthResponse> login(
            @Valid // 요청 바디의 유효성 검사 수행
            @RequestBody // HTTP 요청의 body에서 데이터를 받아옵니다.
            LoginRequest request) {
        // 로그인 요청 사용자명을 로그로 남깁니다.
        log.info("POST /api/auth/login - username: {}", request.getUsername());

        // 로그인 처리 로직은 AuthService에서 실행
        AuthResponse response = authService.login(request);

        // 성공적으로 로그인된 경우 사용자 정보와 메시지로 응답
        return ApiResponse.success(response, "로그인 성공");
    }
}
