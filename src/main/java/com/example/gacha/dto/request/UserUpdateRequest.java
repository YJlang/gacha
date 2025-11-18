package com.example.gacha.dto.request;

import jakarta.validation.constraints.Email;
import lombok.*;

/**
 * 사용자 정보 수정 요청 DTO
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserUpdateRequest {

    /**
     * 이메일 (선택사항)
     */
    @Email(message = "올바른 이메일 형식이 아닙니다.")
    private String email;
}
