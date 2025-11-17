package com.example.gacha.controller;

import com.example.gacha.dto.request.GachaDrawRequest;
import com.example.gacha.dto.response.ApiResponse;
import com.example.gacha.dto.response.GachaStatusResponse;
import com.example.gacha.dto.response.VillageResponse;
import com.example.gacha.service.AuthService;
import com.example.gacha.service.GachaService;
import com.example.gacha.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

/**
 * 가챠 API 컨트롤러
 * 랜덤 여행지 뽑기 및 상태 확인
 */
@RestController
@RequestMapping("/api/gacha")
@RequiredArgsConstructor
@Slf4j
public class GachaController {

    private final GachaService gachaService;
    private final JwtUtil jwtUtil;

    /**
     * 랜덤 여행지 뽑기 (가챠)
     *
     * @param authorization Authorization 헤더 (Bearer {token})
     * @param request       가챠 요청 (필터링 옵션, 선택사항)
     * @return 랜덤으로 선택된 여행지
     */
    @PostMapping("/draw")
    public ApiResponse<VillageResponse> drawGacha(
            @RequestHeader("Authorization") String authorization,
            @RequestBody(required = false) GachaDrawRequest request
    ) {
        // 토큰에서 사용자 ID 추출
        String token = jwtUtil.extractToken(authorization);
        Long userId = jwtUtil.getUserIdFromToken(token);

        log.info("POST /api/gacha/draw - userId: {}, region: {}, programType: {}",
                userId,
                request != null ? request.getRegion() : null,
                request != null ? request.getProgramType() : null);

        // 요청이 null인 경우 빈 요청 객체 생성
        if (request == null) {
            request = GachaDrawRequest.builder().build();
        }

        VillageResponse response = gachaService.drawGacha(userId, request);

        return ApiResponse.success(response, "가챠 뽑기 성공");
    }

    /**
     * 오늘 가챠 가능 여부 확인
     *
     * @param authorization Authorization 헤더 (Bearer {token})
     * @return 가챠 상태 정보 (가능 여부, 남은 횟수 등)
     */
    @GetMapping("/status")
    public ApiResponse<GachaStatusResponse> getGachaStatus(
            @RequestHeader("Authorization") String authorization
    ) {
        // 토큰에서 사용자 ID 추출
        String token = jwtUtil.extractToken(authorization);
        Long userId = jwtUtil.getUserIdFromToken(token);

        log.info("GET /api/gacha/status - userId: {}", userId);

        GachaStatusResponse response = gachaService.getGachaStatus(userId);

        return ApiResponse.success(response);
    }
}
