package com.example.gacha.dto.response;

import lombok.*;

import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CollectionStatsResponse {

    /**
     * 전체 컬렉션 개수
     */
    private Long totalCount;

    /**
     * 지역별 컬렉션 통계
     * 예: {"경상남도": 5, "전라북도": 3, "강원도": 2}
     */
    private Map<String, Long> regionStats;
}
