package com.example.gacha.domain.user;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

/**
 * 사용자 엔티티
 */
@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {

    /**
     * 사용자 ID (Primary Key)
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long userId;

    /**
     * 사용자 아이디 (로그인용, 최소 3자)
     */
    @Column(nullable = false, unique = true, length = 50)
    private String username;

    /**
     * 암호화된 비밀번호 (최소 6자)
     */
    @Column(nullable = false, length = 255)
    private String password;

    /**
     * 이메일 주소
     */
    @Column(nullable = false, unique = true, length = 100)
    private String email;

    /**
     * 계정 생성 일시
     */
    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;
}
