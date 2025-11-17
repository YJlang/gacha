package com.example.gacha.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 가챠 뽑기 요청 DTO
 * 필터링 옵션 (선택사항)
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GachaDrawRequest {

    /**
     * 시도명 필터 (선택사항, 예: "경상남도", "전라북도")
     */
    private String region;

    /**
     * 프로그램 유형 필터 (선택사항, 예: "체험프로그램", "숙박")
     */
    private String programType;
}
