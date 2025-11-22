package com.example.gacha.service;

import com.example.gacha.domain.gacha.GachaHistory;
import com.example.gacha.domain.gacha.GachaHistoryRepository;
import com.example.gacha.domain.village.VillageDto;
import com.example.gacha.domain.village.VillageService;
import com.example.gacha.dto.request.GachaDrawRequest;
import com.example.gacha.dto.response.GachaStatusResponse;
import com.example.gacha.dto.response.VillageResponse;
import com.example.gacha.exception.BusinessException;
import com.example.gacha.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;

/**
 * 가챠 서비스
 * 핵심 기능: 하루 1회 제한, 랜덤 여행지 뽑기
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class GachaService {

        private static final int DAILY_GACHA_LIMIT = 1; // 하루 가챠 제한 횟수 (1회)

        private final VillageService villageService;
        private final GachaHistoryRepository gachaHistoryRepository;

        /**
         * 가챠 뽑기 (랜덤 여행지 추천)
         *
         * @param userId  사용자 ID
         * @param request 가챠 요청 (필터링 옵션)
         * @return 랜덤으로 선택된 여행지
         * @throws BusinessException 하루 제한 초과 시
         */
        @Transactional
        public VillageResponse drawGacha(Long userId, GachaDrawRequest request) {
                log.info("Gacha draw attempt - userId: {}, region: {}, programType: {}",
                                userId, request.getRegion(), request.getProgramType());

                // 오늘 가챠 횟수 확인 (하루 1회 제한)
                LocalDateTime todayStart = getTodayStart();
                long todayCount = gachaHistoryRepository.countByUserIdAndDrawnAtAfter(userId, todayStart);

                if (todayCount >= DAILY_GACHA_LIMIT) {
                        log.warn("Daily gacha limit exceeded - userId: {}, todayCount: {}/{}", userId, todayCount,
                                        DAILY_GACHA_LIMIT);
                        throw new BusinessException(ErrorCode.DAILY_LIMIT_EXCEEDED);
                }

                // CSV 파일에서 랜덤 여행지 선택 (필터링 적용)
                VillageDto selectedVillage = villageService.getRandomVillage(
                                request.getRegion(),
                                request.getProgramType());

                // 가챠 이력 저장
                LocalDateTime drawnAt = LocalDateTime.now(ZoneId.of("Asia/Seoul"));
                GachaHistory history = GachaHistory.builder()
                                .userId(userId)
                                .villageId(selectedVillage.getVillageId())
                                .drawnAt(drawnAt)
                                .build();

                gachaHistoryRepository.save(history);

                log.info("Gacha draw success - userId: {}, villageId: {}, villageName: {}",
                                userId, selectedVillage.getVillageId(), selectedVillage.getVillageName());

                // 해당 여행지를 처음 뽑았는지 확인
                boolean isNew = !hasDrawnBefore(userId, selectedVillage.getVillageId());

                return VillageResponse.forGacha(selectedVillage, isNew, drawnAt);
        }

        /**
         * 오늘 가챠 가능 여부 확인
         *
         * @param userId 사용자 ID
         * @return 가챠 상태 정보
         */
        public GachaStatusResponse getGachaStatus(Long userId) {
                log.info("Get gacha status - userId: {}", userId);

                LocalDateTime todayStart = getTodayStart();

                // 오늘 가챠 이력 조회
                List<GachaHistory> todayHistory = gachaHistoryRepository
                                .findByUserIdAndDrawnAtAfterOrderByDrawnAtDesc(userId, todayStart);

                long todayDrawCount = todayHistory.size();
                boolean canDraw = todayDrawCount < DAILY_GACHA_LIMIT; // 하루 1회 제한
                int remainingCount = Math.max(0, DAILY_GACHA_LIMIT - (int) todayDrawCount);

                // 마지막 가챠 시간
                LocalDateTime lastDrawTime = todayHistory.isEmpty() ? null : todayHistory.get(0).getDrawnAt();

                return GachaStatusResponse.builder()
                                .canDraw(canDraw)
                                .remainingCount(remainingCount)
                                .lastDrawTime(lastDrawTime)
                                .todayDrawCount(todayDrawCount)
                                .build();
        }

        /**
         * 사용자가 특정 여행지를 이전에 뽑았는지 확인
         *
         * @param userId    사용자 ID
         * @param villageId 여행지 ID
         * @return 뽑았으면 true, 처음이면 false
         */
        private boolean hasDrawnBefore(Long userId, Long villageId) {
                List<GachaHistory> allHistory = gachaHistoryRepository.findByUserIdOrderByDrawnAtDesc(userId);

                // 오늘 뽑은 것 제외하고 이전에 뽑았는지 확인
                LocalDateTime todayStart = getTodayStart();
                return allHistory.stream()
                                .filter(h -> h.getDrawnAt().isBefore(todayStart)) // 오늘 이전
                                .anyMatch(h -> h.getVillageId().equals(villageId));
        }

        /**
         * 오늘 00:00:00 시간 반환 (Asia/Seoul 타임존 기준)
         *
         * @return 오늘 시작 시간 (00:00:00)
         */
        private LocalDateTime getTodayStart() {
                return LocalDateTime.now(ZoneId.of("Asia/Seoul"))
                                .withHour(0)
                                .withMinute(0)
                                .withSecond(0)
                                .withNano(0);
        }
}
