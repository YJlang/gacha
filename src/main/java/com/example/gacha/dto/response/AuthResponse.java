package com.example.gacha.dto.response;

import com.example.gacha.domain.user.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 인증 응답 DTO (회원가입, 로그인)
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuthResponse {

    /**
     * JWT 토큰 (로그인 시에만 포함)
     */
    private String token;

    /**
     * 사용자 정보
     */
    private UserInfo user;

    /**
     * 사용자 정보 내부 클래스
     */
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class UserInfo {
        private Long userId;
        private String username;
        private String email;
        private LocalDateTime createdAt;
    }

    /**
     * User 엔티티로부터 AuthResponse 생성 (회원가입용 - 토큰 없음)
     */
    public static AuthResponse from(User user) {
        return AuthResponse.builder()
                .user(UserInfo.builder()
                        .userId(user.getUserId())
                        .username(user.getUsername())
                        .email(user.getEmail())
                        .createdAt(user.getCreatedAt())
                        .build())
                .build();
    }

    /**
     * User 엔티티와 토큰으로부터 AuthResponse 생성 (로그인용)
     */
    public static AuthResponse of(User user, String token) {
        return AuthResponse.builder()
                .token(token)
                .user(UserInfo.builder()
                        .userId(user.getUserId())
                        .username(user.getUsername())
                        .email(user.getEmail())
                        .createdAt(user.getCreatedAt())
                        .build())
                .build();
    }
}
