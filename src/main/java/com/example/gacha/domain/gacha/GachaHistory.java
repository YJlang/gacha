package com.example.gacha.domain.gacha;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

/**
 * 가챠 이력 엔티티
 * 하루 1회 제한 확인 및 통계를 위한 테이블
 */
@Entity
@Table(name = "gacha_history",
        indexes = {
                @Index(name = "idx_user_drawn_at", columnList = "user_id, drawn_at"),
                @Index(name = "idx_user_id", columnList = "user_id")
        })
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GachaHistory {

    /**
     * 가챠 ID (Primary Key)
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "gacha_id")
    private Long gachaId;

    /**
     * 사용자 ID (Foreign Key)
     */
    @Column(name = "user_id", nullable = false)
    private Long userId;

    /**
     * 여행지 ID (CSV 파일의 villageId 참조, 외래키 제약 없음)
     */
    @Column(name = "village_id", nullable = false)
    private Long villageId;

    /**
     * 가챠 뽑기 일시
     */
    @CreationTimestamp
    @Column(name = "drawn_at", nullable = false)
    private LocalDateTime drawnAt;
}
