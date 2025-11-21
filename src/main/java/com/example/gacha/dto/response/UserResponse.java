package com.example.gacha.dto.response;

import com.example.gacha.domain.user.User;
import lombok.*;

import java.time.LocalDateTime;

/**
 * 사용자 정보 응답 DTO (마이페이지용)
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserResponse {

    private Long userId;
    private String username;
    private String email;
    private LocalDateTime createdAt;

    /**
     * 컬렉션 개수
     */
    private Long collectionCount;

    /**
     * 추억 개수
     */
    private Long memoryCount;

    /**
     * User Entity로부터 UserResponse 생성 (기본)
     */
    public static UserResponse from(User user) {
        return UserResponse.builder()
                .userId(user.getUserId())
                .username(user.getUsername())
                .email(user.getEmail())
                .createdAt(user.getCreatedAt())
                .build();
    }

    /**
     * User Entity로부터 UserResponse 생성 (마이페이지용 - 통계 포함)
     */
    public static UserResponse withStats(User user, Long collectionCount, Long memoryCount) {
        return UserResponse.builder()
                .userId(user.getUserId())
                .username(user.getUsername())
                .email(user.getEmail())
                .createdAt(user.getCreatedAt())
                .collectionCount(collectionCount)
                .memoryCount(memoryCount)
                .build();
    }
}
