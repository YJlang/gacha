package com.example.gacha.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 가챠 상태 응답 DTO
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GachaStatusResponse {

    /**
     * 오늘 가챠 가능 여부
     */
    private Boolean canDraw;

    /**
     * 남은 가챠 횟수 (1 또는 0)
     */
    private Integer remainingCount;

    /**
     * 마지막 가챠 뽑기 일시 (오늘 뽑았다면 해당 시간, 없으면 null)
     */
    private LocalDateTime lastDrawTime;

    /**
     * 오늘 뽑은 횟수
     */
    private Long todayDrawCount;
}
