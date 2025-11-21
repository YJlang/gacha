package com.example.gacha.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MemoryRequest {

    /**
     * 여행지 ID (작성 시에만 필수, 수정 시에는 불필요)
     */
    private Long villageId;

    /**
     * 추억 내용 (1~1000자)
     */
    @NotBlank(message = "추억 내용은 필수입니다.")
    @Size(min = 1, max = 1000, message = "추억 내용은 1자 이상 1000자 이하여야 합니다.")
    private String content;

    /**
     * 방문 날짜 (선택사항)
     */
    private LocalDate visitDate;
}
