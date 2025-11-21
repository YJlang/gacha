package com.example.gacha.controller;

import com.example.gacha.domain.collection.CollectionRepository;
import com.example.gacha.domain.village.VillageDto;
import com.example.gacha.domain.village.VillageService;
import com.example.gacha.dto.response.ApiResponse;
import com.example.gacha.dto.response.VillageResponse;
import com.example.gacha.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

/**
 * 여행지 관련 API
 */
@RestController
@RequestMapping("/api/villages")
@RequiredArgsConstructor
public class VillageController {

    private final VillageService villageService;
    private final CollectionRepository collectionRepository;
    private final JwtUtil jwtUtil;

    /**
     * 여행지 상세 조회
     * GET /api/villages/{villageId}
     */
    @GetMapping("/{villageId}")
    public ApiResponse<VillageResponse> getVillageById(
            @RequestHeader(value = "Authorization", required = false) String authorization,
            @PathVariable Long villageId
    ) {
        VillageDto village = villageService.getVillageById(villageId);

        // JWT 토큰이 있으면 컬렉션 여부 확인
        boolean isCollected = false;
        if (authorization != null) {
            try {
                String token = jwtUtil.extractToken(authorization);
                Long userId = jwtUtil.getUserIdFromToken(token);
                isCollected = collectionRepository.existsByUserIdAndVillageId(userId, villageId);
            } catch (Exception e) {
                // 토큰이 유효하지 않으면 무시
            }
        }

        VillageResponse response = VillageResponse.from(village, isCollected);
        return ApiResponse.success(response, "여행지 조회 성공");
    }

    /**
     * 여행지 목록 조회 (페이지네이션, 필터링)
     * GET /api/villages?page=0&size=20&region=경상남도&programType=체험
     */
    @GetMapping
    public ApiResponse<Page<VillageDto>> getAllVillages(
            @RequestParam(required = false) String region,
            @RequestParam(required = false) String programType,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        Pageable pageable = PageRequest.of(page, size);
        Page<VillageDto> response = villageService.getAllVillages(region, programType, pageable);
        return ApiResponse.success(response, "여행지 목록 조회 성공");
    }
}
