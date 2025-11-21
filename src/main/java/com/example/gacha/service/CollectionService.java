package com.example.gacha.service;

import com.example.gacha.domain.collection.Collection;
import com.example.gacha.domain.collection.CollectionRepository;
import com.example.gacha.domain.village.VillageDto;
import com.example.gacha.domain.village.VillageService;
import com.example.gacha.dto.request.CollectionRequest;
import com.example.gacha.dto.response.CollectionResponse;
import com.example.gacha.dto.response.CollectionStatsResponse;
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
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class CollectionService {

    private final CollectionRepository collectionRepository;
    private final VillageService villageService;

    /**
     * 컬렉션에 여행지 추가
     */
    @Transactional
    public CollectionResponse addCollection(Long userId, CollectionRequest request) {
        Long villageId = request.getVillageId();

        // 1. 여행지 존재 여부 확인
        VillageDto village = villageService.getVillageById(villageId);

        // 2. 이미 컬렉션에 추가되어 있는지 확인
        if (collectionRepository.existsByUserIdAndVillageId(userId, villageId)) {
            throw new BusinessException(ErrorCode.ALREADY_COLLECTED);
        }

        // 3. 컬렉션 저장
        Collection collection = Collection.builder()
                .userId(userId)
                .villageId(villageId)
                .build();

        Collection savedCollection = collectionRepository.save(collection);
        log.info("User {} added village {} to collection", userId, villageId);

        // 4. 응답 생성
        return CollectionResponse.from(savedCollection, village);
    }

    /**
     * 내 컬렉션 조회 (페이지네이션)
     */
    public Page<CollectionResponse> getMyCollections(Long userId, Pageable pageable) {
        // 1. 사용자의 모든 컬렉션 조회
        List<Collection> collections = collectionRepository.findByUserIdOrderByCollectedAtDesc(userId);

        // 2. 컬렉션을 Response로 변환
        List<CollectionResponse> responses = collections.stream()
                .map(collection -> {
                    try {
                        VillageDto village = villageService.getVillageById(collection.getVillageId());
                        return CollectionResponse.from(collection, village);
                    } catch (BusinessException e) {
                        // 여행지가 CSV에서 삭제된 경우 null 반환 (필터링됨)
                        log.warn("Village {} not found for collection {}", collection.getVillageId(), collection.getCollectionId());
                        return null;
                    }
                })
                .filter(response -> response != null)
                .collect(Collectors.toList());

        // 3. 페이지네이션 처리
        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), responses.size());
        List<CollectionResponse> pageContent = responses.subList(start, end);

        return new PageImpl<>(pageContent, pageable, responses.size());
    }

    /**
     * 컬렉션 제거
     */
    @Transactional
    public void removeCollection(Long userId, Long collectionId) {
        // 1. 컬렉션 존재 및 권한 확인
        Collection collection = collectionRepository.findByCollectionIdAndUserId(collectionId, userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.COLLECTION_NOT_FOUND));

        // 2. 컬렉션 삭제
        collectionRepository.delete(collection);
        log.info("User {} removed collection {}", userId, collectionId);
    }

    /**
     * 컬렉션 통계 조회 (지역별 집계)
     */
    public CollectionStatsResponse getCollectionStats(Long userId) {
        log.info("Getting collection stats for user: {}", userId);

        // 1. 사용자의 모든 컬렉션 조회
        List<Collection> collections = collectionRepository.findByUserIdOrderByCollectedAtDesc(userId);
        log.info("Found {} collections for user {}", collections.size(), userId);

        // 2. 총 개수
        Long totalCount = (long) collections.size();

        // 3. 지역별 통계 계산
        Map<String, Long> regionStats;

        if (collections.isEmpty()) {
            // 컬렉션이 없으면 빈 Map 반환
            regionStats = new java.util.HashMap<>();
            log.info("No collections found, returning empty stats");
        } else {
            regionStats = collections.stream()
                    .map(collection -> {
                        try {
                            VillageDto village = villageService.getVillageById(collection.getVillageId());
                            return village.getSidoName();
                        } catch (BusinessException e) {
                            log.warn("Village {} not found for collection stats", collection.getVillageId());
                            return null;
                        }
                    })
                    .filter(sidoName -> sidoName != null)
                    .collect(Collectors.groupingBy(
                            sidoName -> sidoName,
                            Collectors.counting()
                    ));
            log.info("Region stats calculated: {}", regionStats);
        }

        return CollectionStatsResponse.builder()
                .totalCount(totalCount)
                .regionStats(regionStats)
                .build();
    }
}
