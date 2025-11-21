package com.example.gacha.controller;

import com.example.gacha.dto.request.UserUpdateRequest;
import com.example.gacha.dto.response.ApiResponse;
import com.example.gacha.dto.response.UserResponse;
import com.example.gacha.service.UserService;
import com.example.gacha.util.JwtUtil;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * 사용자(마이페이지) 관련 API
 */
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final JwtUtil jwtUtil;

    /**
     * 내 정보 조회 (마이페이지)
     * GET /api/users/me
     */
    @GetMapping("/me")
    public ApiResponse<UserResponse> getMyInfo(
            @RequestHeader("Authorization") String authorization
    ) {
        String token = jwtUtil.extractToken(authorization);
        Long userId = jwtUtil.getUserIdFromToken(token);

        UserResponse response = userService.getMyInfo(userId);
        return ApiResponse.success(response, "내 정보 조회 성공");
    }

    /**
     * 내 정보 수정
     * PUT /api/users/me
     */
    @PutMapping("/me")
    public ApiResponse<UserResponse> updateMyInfo(
            @RequestHeader("Authorization") String authorization,
            @Valid @RequestBody UserUpdateRequest request
    ) {
        String token = jwtUtil.extractToken(authorization);
        Long userId = jwtUtil.getUserIdFromToken(token);

        UserResponse response = userService.updateMyInfo(userId, request);
        return ApiResponse.success(response, "내 정보가 수정되었습니다.");
    }
}
