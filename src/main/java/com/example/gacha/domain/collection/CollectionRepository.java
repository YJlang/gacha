package com.example.gacha.domain.collection;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CollectionRepository extends JpaRepository<Collection, Long> {

    /**
     * 사용자의 컬렉션 목록 조회 (최신순)
     */
    List<Collection> findByUserIdOrderByCollectedAtDesc(Long userId);

    /**
     * 사용자가 이미 해당 여행지를 컬렉션에 추가했는지 확인
     */
    boolean existsByUserIdAndVillageId(Long userId, Long villageId);

    /**
     * 컬렉션 ID와 사용자 ID로 조회 (권한 확인용)
     */
    Optional<Collection> findByCollectionIdAndUserId(Long collectionId, Long userId);

    /**
     * 사용자의 총 컬렉션 개수
     */
    long countByUserId(Long userId);

    /**
     * 컬렉션 삭제 (권한 확인 후)
     */
    void deleteByCollectionIdAndUserId(Long collectionId, Long userId);
}
