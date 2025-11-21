package com.example.gacha.dto.response;

import com.example.gacha.domain.village.VillageDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 여행지(마을) 응답 DTO
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VillageResponse {

    private Long villageId;
    private String villageName;
    private String sidoName;
    private String sigunguName;
    private String address;
    private String phoneNumber;
    private Double latitude;
    private Double longitude;
    private String programName;
    private String programContent;

    /**
     * 이미지 URL
     */
    private String imageUrl;

    /**
     * 새로 뽑은 여행지인지 여부 (가챠 응답 시에만 사용)
     */
    private Boolean isNew;

    /**
     * 가챠 뽑기 일시 (가챠 응답 시에만 사용)
     */
    private LocalDateTime drawnAt;

    /**
     * 컬렉션 여부 (여행지 상세 조회 시 사용)
     */
    private Boolean isCollected;

    /**
     * 컬렉션 추가 일시 (여행지 상세 조회 시 사용)
     */
    private LocalDateTime collectedAt;

    /**
     * VillageDto로부터 VillageResponse 생성 (기본)
     */
    public static VillageResponse from(VillageDto village) {
        return VillageResponse.builder()
                .villageId(village.getVillageId())
                .villageName(village.getVillageName())
                .sidoName(village.getSidoName())
                .sigunguName(village.getSigunguName())
                .address(village.getAddress())
                .phoneNumber(village.getPhoneNumber())
                .latitude(village.getLatitude())
                .longitude(village.getLongitude())
                .programName(village.getProgramName())
                .programContent(village.getProgramContent())
                .imageUrl(village.getImageUrl())
                .build();
    }

    /**
     * VillageDto로부터 VillageResponse 생성 (가챠 응답용)
     */
    public static VillageResponse forGacha(VillageDto village, boolean isNew, LocalDateTime drawnAt) {
        return VillageResponse.builder()
                .villageId(village.getVillageId())
                .villageName(village.getVillageName())
                .sidoName(village.getSidoName())
                .sigunguName(village.getSigunguName())
                .address(village.getAddress())
                .phoneNumber(village.getPhoneNumber())
                .latitude(village.getLatitude())
                .longitude(village.getLongitude())
                .programName(village.getProgramName())
                .programContent(village.getProgramContent())
                .imageUrl(village.getImageUrl())
                .isNew(isNew)
                .drawnAt(drawnAt)
                .build();
    }

    /**
     * VillageDto로부터 VillageResponse 생성 (여행지 상세 조회용 - 컬렉션 여부 포함)
     */
    public static VillageResponse from(VillageDto village, boolean isCollected) {
        return VillageResponse.builder()
                .villageId(village.getVillageId())
                .villageName(village.getVillageName())
                .sidoName(village.getSidoName())
                .sigunguName(village.getSigunguName())
                .address(village.getAddress())
                .phoneNumber(village.getPhoneNumber())
                .latitude(village.getLatitude())
                .longitude(village.getLongitude())
                .programName(village.getProgramName())
                .programContent(village.getProgramContent())
                .imageUrl(village.getImageUrl())
                .isCollected(isCollected)
                .build();
    }
}
