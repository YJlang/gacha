package com.example.gacha.controller;

import com.example.gacha.dto.request.CollectionRequest;
import com.example.gacha.dto.response.ApiResponse;
import com.example.gacha.dto.response.CollectionResponse;
import com.example.gacha.dto.response.CollectionStatsResponse;
import com.example.gacha.dto.response.PageResponse;
import com.example.gacha.service.CollectionService;
import com.example.gacha.util.JwtUtil;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

/**
 * 컬렉션 관련 API
 */
@RestController
@RequestMapping("/api/collections")
@RequiredArgsConstructor
public class CollectionController {

    private final CollectionService collectionService;
    private final JwtUtil jwtUtil;

    /**
     * 컬렉션에 여행지 추가
     * POST /api/collections
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<CollectionResponse> addCollection(
            @RequestHeader("Authorization") String authorization,
            @Valid @RequestBody CollectionRequest request) {
        String token = jwtUtil.extractToken(authorization);
        Long userId = jwtUtil.getUserIdFromToken(token);

        CollectionResponse response = collectionService.addCollection(userId, request);
        return ApiResponse.success(response, "컬렉션에 추가되었습니다.");
    }

    /**
     * 컬렉션 통계 조회 (지역별 집계)
     * GET /api/collections/stats
     */
    @GetMapping("/stats")
    public ApiResponse<CollectionStatsResponse> getCollectionStats(
            @RequestHeader("Authorization") String authorization) {
        String token = jwtUtil.extractToken(authorization);
        Long userId = jwtUtil.getUserIdFromToken(token);

        CollectionStatsResponse response = collectionService.getCollectionStats(userId);
        return ApiResponse.success(response, "컬렉션 통계 조회 성공");
    }

    /**
     * 내 컬렉션 조회 (페이지네이션)
     * GET /api/collections?page=0&size=20
     */
    @GetMapping
    public ApiResponse<PageResponse<CollectionResponse>> getMyCollections(
            @RequestHeader("Authorization") String authorization,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        String token = jwtUtil.extractToken(authorization);
        Long userId = jwtUtil.getUserIdFromToken(token);

        Pageable pageable = PageRequest.of(page, size);
        Page<CollectionResponse> pageResult = collectionService.getMyCollections(userId, pageable);
        PageResponse<CollectionResponse> response = PageResponse.from(pageResult);
        return ApiResponse.success(response, "내 컬렉션 조회 성공");
    }

    /**
     * 컬렉션에서 제거
     * DELETE /api/collections/{collectionId}
     */
    @DeleteMapping("/{collectionId}")
    public ApiResponse<Void> removeCollection(
            @RequestHeader("Authorization") String authorization,
            @PathVariable Long collectionId) {
        String token = jwtUtil.extractToken(authorization);
        Long userId = jwtUtil.getUserIdFromToken(token);

        collectionService.removeCollection(userId, collectionId);
        return ApiResponse.success(null, "컬렉션에서 제거되었습니다.");
    }
}
