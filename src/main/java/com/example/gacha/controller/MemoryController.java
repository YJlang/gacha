package com.example.gacha.controller;

import com.example.gacha.dto.request.MemoryRequest;
import com.example.gacha.dto.response.ApiResponse;
import com.example.gacha.dto.response.MemoryResponse;
import com.example.gacha.dto.response.PageResponse;
import com.example.gacha.service.FileStorageService;
import com.example.gacha.service.MemoryService;
import com.example.gacha.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

/**
 * 추억 관련 API
 */
@RestController
@RequestMapping("/api/memories")
@RequiredArgsConstructor
public class MemoryController {

    private final MemoryService memoryService;
    private final FileStorageService fileStorageService;
    private final JwtUtil jwtUtil;

    /**
     * 추억 작성
     * POST /api/memories
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<MemoryResponse> createMemory(
            @RequestHeader("Authorization") String authorization,
            @ModelAttribute MemoryRequest request,
            @RequestParam(value = "image", required = false) MultipartFile image) {
        String token = jwtUtil.extractToken(authorization);
        Long userId = jwtUtil.getUserIdFromToken(token);

        // 이미지 파일이 있으면 저장
        if (image != null && !image.isEmpty()) {
            String imageUrl = fileStorageService.storeMemoryImage(image);
            request.setImageUrl(imageUrl);
        }

        MemoryResponse response = memoryService.createMemory(userId, request);
        return ApiResponse.success(response, "추억이 작성되었습니다.");
    }

    /**
     * 내 추억 목록 조회 (페이지네이션)
     * GET /api/memories?page=0&size=20
     */
    @GetMapping
    public ApiResponse<PageResponse<MemoryResponse>> getMyMemories(
            @RequestHeader("Authorization") String authorization,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        String token = jwtUtil.extractToken(authorization);
        Long userId = jwtUtil.getUserIdFromToken(token);

        Pageable pageable = PageRequest.of(page, size);
        Page<MemoryResponse> pageResult = memoryService.getMyMemories(userId, pageable);
        PageResponse<MemoryResponse> response = PageResponse.from(pageResult);
        return ApiResponse.success(response, "내 추억 목록 조회 성공");
    }

    /**
     * 추억 상세 조회
     * GET /api/memories/{memoryId}
     */
    @GetMapping("/{memoryId}")
    public ApiResponse<MemoryResponse> getMemoryById(
            @RequestHeader("Authorization") String authorization,
            @PathVariable Long memoryId) {
        String token = jwtUtil.extractToken(authorization);
        Long userId = jwtUtil.getUserIdFromToken(token);

        MemoryResponse response = memoryService.getMemoryById(userId, memoryId);
        return ApiResponse.success(response, "추억 조회 성공");
    }

    /**
     * 추억 수정
     * PUT /api/memories/{memoryId}
     */
    @PutMapping("/{memoryId}")
    public ApiResponse<MemoryResponse> updateMemory(
            @RequestHeader("Authorization") String authorization,
            @PathVariable Long memoryId,
            @ModelAttribute MemoryRequest request,
            @RequestParam(value = "image", required = false) MultipartFile image) {
        String token = jwtUtil.extractToken(authorization);
        Long userId = jwtUtil.getUserIdFromToken(token);

        // 이미지 파일이 있으면 저장
        if (image != null && !image.isEmpty()) {
            String imageUrl = fileStorageService.storeMemoryImage(image);
            request.setImageUrl(imageUrl);
        }

        MemoryResponse response = memoryService.updateMemory(userId, memoryId, request);
        return ApiResponse.success(response, "추억이 수정되었습니다.");
    }

    /**
     * 추억 삭제
     * DELETE /api/memories/{memoryId}
     */
    @DeleteMapping("/{memoryId}")
    public ApiResponse<Void> deleteMemory(
            @RequestHeader("Authorization") String authorization,
            @PathVariable Long memoryId) {
        String token = jwtUtil.extractToken(authorization);
        Long userId = jwtUtil.getUserIdFromToken(token);

        memoryService.deleteMemory(userId, memoryId);
        return ApiResponse.success(null, "추억이 삭제되었습니다.");
    }
}
