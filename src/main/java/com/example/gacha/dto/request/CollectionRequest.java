package com.example.gacha.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CollectionRequest {

    @NotNull(message = "여행지 ID는 필수입니다.")
    private Long villageId;
}
