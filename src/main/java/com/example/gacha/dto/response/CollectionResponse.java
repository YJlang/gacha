package com.example.gacha.dto.response;

import com.example.gacha.domain.collection.Collection;
import com.example.gacha.domain.village.VillageDto;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CollectionResponse {

    private Long collectionId;
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
    private String imageUrl;
    private LocalDateTime collectedAt;

    /**
     * Collection Entity와 VillageDto를 조합하여 Response 생성
     */
    public static CollectionResponse from(Collection collection, VillageDto village) {
        return CollectionResponse.builder()
                .collectionId(collection.getCollectionId())
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
                .collectedAt(collection.getCollectedAt())
                .build();
    }
}
