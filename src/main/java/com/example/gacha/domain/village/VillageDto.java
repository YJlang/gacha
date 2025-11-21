package com.example.gacha.domain.village;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * CSV 파일에서 읽은 여행지(마을) 데이터를 담는 DTO
 * DB 테이블이 아닌 CSV 파일 기반 데이터
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VillageDto {
    /**
     * CSV 인덱스 기반 ID (1부터 시작)
     */
    private Long villageId;

    /**
     * 마을명
     */
    private String villageName;

    /**
     * 시도명 (예: 경상남도, 전라북도)
     */
    private String sidoName;

    /**
     * 시군구명 (예: 산청군, 남원시)
     */
    private String sigunguName;

    /**
     * 주소
     */
    private String address;

    /**
     * 전화번호
     */
    private String phoneNumber;

    /**
     * 위도
     */
    private Double latitude;

    /**
     * 경도
     */
    private Double longitude;

    /**
     * 체험프로그램명
     */
    private String programName;

    /**
     * 체험프로그램 상세 내용
     */
    private String programContent;
}
