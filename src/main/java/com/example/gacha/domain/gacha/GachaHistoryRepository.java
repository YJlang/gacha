package com.example.gacha.domain.gacha;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * 가챠 이력 Repository
 */
@Repository
public interface GachaHistoryRepository extends JpaRepository<GachaHistory, Long> {

    /**
     * 특정 시간 이후 사용자의 가챠 횟수 조회 (하루 1회 제한 확인용)
     *
     * @param userId    사용자 ID
     * @param drawnAtAfter 기준 시간 (예: 오늘 00:00:00)
     * @return 가챠 횟수
     */
    long countByUserIdAndDrawnAtAfter(Long userId, LocalDateTime drawnAtAfter);

    /**
     * 특정 시간 이후 사용자의 가챠 이력 조회
     *
     * @param userId    사용자 ID
     * @param drawnAtAfter 기준 시간
     * @return 가챠 이력 리스트
     */
    List<GachaHistory> findByUserIdAndDrawnAtAfterOrderByDrawnAtDesc(Long userId, LocalDateTime drawnAtAfter);

    /**
     * 특정 시간 이후 사용자의 가장 최근 가챠 이력 조회
     *
     * @param userId    사용자 ID
     * @param drawnAtAfter 기준 시간
     * @return 가장 최근 가챠 이력 (Optional)
     */
    Optional<GachaHistory> findFirstByUserIdAndDrawnAtAfterOrderByDrawnAtDesc(Long userId, LocalDateTime drawnAtAfter);

    /**
     * 사용자의 모든 가챠 이력 조회
     *
     * @param userId 사용자 ID
     * @return 가챠 이력 리스트
     */
    List<GachaHistory> findByUserIdOrderByDrawnAtDesc(Long userId);

    /**
     * 사용자의 총 가챠 횟수 조회
     *
     * @param userId 사용자 ID
     * @return 총 가챠 횟수
     */
    long countByUserId(Long userId);
}
