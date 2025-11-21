package com.example.gacha.domain.memory;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MemoryRepository extends JpaRepository<Memory, Long> {

    /**
     * 사용자의 추억 목록 조회 (최신순)
     */
    List<Memory> findByUserIdOrderByCreatedAtDesc(Long userId);

    /**
     * 추억 ID와 사용자 ID로 조회 (권한 확인용)
     */
    Optional<Memory> findByMemoryIdAndUserId(Long memoryId, Long userId);

    /**
     * 사용자의 총 추억 개수
     */
    long countByUserId(Long userId);
}
