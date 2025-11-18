package com.example.gacha.service;

import com.example.gacha.domain.memory.Memory;
import com.example.gacha.domain.memory.MemoryRepository;
import com.example.gacha.domain.village.VillageDto;
import com.example.gacha.domain.village.VillageService;
import com.example.gacha.dto.request.MemoryRequest;
import com.example.gacha.dto.response.MemoryResponse;
import com.example.gacha.exception.BusinessException;
import com.example.gacha.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class MemoryService {

    private final MemoryRepository memoryRepository;
    private final VillageService villageService;

    /**
     * 추억 작성
     */
    @Transactional
    public MemoryResponse createMemory(Long userId, MemoryRequest request) {
        // 1. villageId 필수 확인
        if (request.getVillageId() == null) {
            throw new BusinessException(ErrorCode.BAD_REQUEST);
        }

        // 2. 여행지 존재 여부 확인
        VillageDto village = villageService.getVillageById(request.getVillageId());

        // 3. 추억 저장
        Memory memory = Memory.builder()
                .userId(userId)
                .villageId(request.getVillageId())
                .content(request.getContent())
                .visitDate(request.getVisitDate())
                .build();

        Memory savedMemory = memoryRepository.save(memory);
        log.info("User {} created memory {} for village {}", userId, savedMemory.getMemoryId(), request.getVillageId());

        // 4. 응답 생성
        return MemoryResponse.from(savedMemory, village);
    }

    /**
     * 내 추억 목록 조회 (페이지네이션)
     */
    public Page<MemoryResponse> getMyMemories(Long userId, Pageable pageable) {
        // 1. 사용자의 모든 추억 조회
        List<Memory> memories = memoryRepository.findByUserIdOrderByCreatedAtDesc(userId);

        // 2. 추억을 Response로 변환
        List<MemoryResponse> responses = memories.stream()
                .map(memory -> {
                    try {
                        VillageDto village = villageService.getVillageById(memory.getVillageId());
                        return MemoryResponse.from(memory, village);
                    } catch (BusinessException e) {
                        // 여행지가 CSV에서 삭제된 경우 null 반환 (필터링됨)
                        log.warn("Village {} not found for memory {}", memory.getVillageId(), memory.getMemoryId());
                        return null;
                    }
                })
                .filter(response -> response != null)
                .collect(Collectors.toList());

        // 3. 페이지네이션 처리
        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), responses.size());
        List<MemoryResponse> pageContent = responses.subList(start, end);

        return new PageImpl<>(pageContent, pageable, responses.size());
    }

    /**
     * 추억 상세 조회
     */
    public MemoryResponse getMemoryById(Long userId, Long memoryId) {
        // 1. 추억 존재 및 권한 확인
        Memory memory = memoryRepository.findByMemoryIdAndUserId(memoryId, userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.MEMORY_NOT_FOUND));

        // 2. 여행지 정보 조회
        VillageDto village = villageService.getVillageById(memory.getVillageId());

        // 3. 응답 생성
        return MemoryResponse.from(memory, village);
    }

    /**
     * 추억 수정
     */
    @Transactional
    public MemoryResponse updateMemory(Long userId, Long memoryId, MemoryRequest request) {
        // 1. 추억 존재 및 권한 확인
        Memory memory = memoryRepository.findByMemoryIdAndUserId(memoryId, userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.MEMORY_NOT_FOUND));

        // 2. 추억 내용 업데이트
        memory.setContent(request.getContent());
        memory.setVisitDate(request.getVisitDate());

        Memory updatedMemory = memoryRepository.save(memory);
        log.info("User {} updated memory {}", userId, memoryId);

        // 3. 여행지 정보 조회
        VillageDto village = villageService.getVillageById(memory.getVillageId());

        // 4. 응답 생성
        return MemoryResponse.from(updatedMemory, village);
    }

    /**
     * 추억 삭제
     */
    @Transactional
    public void deleteMemory(Long userId, Long memoryId) {
        // 1. 추억 존재 및 권한 확인
        Memory memory = memoryRepository.findByMemoryIdAndUserId(memoryId, userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.MEMORY_NOT_FOUND));

        // 2. 추억 삭제
        memoryRepository.delete(memory);
        log.info("User {} deleted memory {}", userId, memoryId);
    }
}
