package com.example.gacha.dto.response;

import com.example.gacha.domain.memory.Memory;
import com.example.gacha.domain.village.VillageDto;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MemoryResponse {

    private Long memoryId;
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
    private String content;
    private LocalDate visitDate;
    private String imageUrl;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    /**
     * Memory Entity와 VillageDto를 조합하여 Response 생성
     */
    public static MemoryResponse from(Memory memory, VillageDto village) {
        return MemoryResponse.builder()
                .memoryId(memory.getMemoryId())
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
                .content(memory.getContent())
                .visitDate(memory.getVisitDate())
                .imageUrl(memory.getImageUrl())
                .createdAt(memory.getCreatedAt())
                .updatedAt(memory.getUpdatedAt())
                .build();
    }
}
