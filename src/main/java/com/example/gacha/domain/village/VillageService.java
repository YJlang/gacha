package com.example.gacha.domain.village;

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
import java.util.Random;

/**
 * 여행지(마을) 서비스
 * CSV 파일 기반 데이터 조회 및 랜덤 선택
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class VillageService {

    private final VillageCsvReader csvReader;
    private final Random random = new Random();

    /**
     * 모든 마을 조회 (페이지네이션)
     *
     * @param region      시도명 필터 (선택)
     * @param programType 프로그램 유형 필터 (선택)
     * @param pageable    페이지 정보
     * @return 페이지네이션된 마을 리스트
     */
    public Page<VillageDto> getAllVillages(String region, String programType, Pageable pageable) {
        List<VillageDto> filteredVillages = csvReader.getFilteredVillages(region, programType);

        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), filteredVillages.size());

        if (start > filteredVillages.size()) {
            return new PageImpl<>(List.of(), pageable, filteredVillages.size());
        }

        List<VillageDto> pageContent = filteredVillages.subList(start, end);

        return new PageImpl<>(pageContent, pageable, filteredVillages.size());
    }

    /**
     * ID로 마을 조회
     *
     * @param villageId 마을 ID
     * @return 마을 데이터
     * @throws BusinessException 마을을 찾을 수 없는 경우
     */
    public VillageDto getVillageById(Long villageId) {
        return csvReader.findById(villageId)
                .orElseThrow(() -> new BusinessException(ErrorCode.VILLAGE_NOT_FOUND));
    }

    /**
     * 랜덤 마을 선택 (가챠용)
     *
     * @param region      시도명 필터 (선택)
     * @param programType 프로그램 유형 필터 (선택)
     * @return 랜덤으로 선택된 마을
     * @throws BusinessException 조건에 맞는 마을이 없는 경우
     */
    public VillageDto getRandomVillage(String region, String programType) {
        List<VillageDto> villages = csvReader.getFilteredVillages(region, programType);

        if (villages.isEmpty()) {
            log.warn("No villages available for region: {}, programType: {}", region, programType);
            throw new BusinessException(ErrorCode.NO_VILLAGES_AVAILABLE);
        }

        int randomIndex = random.nextInt(villages.size());
        VillageDto selectedVillage = villages.get(randomIndex);

        log.info("Random village selected: {} (ID: {})", selectedVillage.getVillageName(), selectedVillage.getVillageId());

        return selectedVillage;
    }

    /**
     * 전체 마을 개수 조회
     *
     * @return 전체 마을 개수
     */
    public long getTotalCount() {
        return csvReader.readAllVillages().size();
    }

    /**
     * 필터링된 마을 개수 조회
     *
     * @param region      시도명 필터
     * @param programType 프로그램 유형 필터
     * @return 필터링된 마을 개수
     */
    public long getFilteredCount(String region, String programType) {
        return csvReader.getFilteredVillages(region, programType).size();
    }
}
