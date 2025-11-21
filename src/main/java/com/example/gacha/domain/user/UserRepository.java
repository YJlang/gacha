package com.example.gacha.domain.user;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * 사용자 Repository
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * 아이디로 사용자 조회
     *
     * @param username 사용자 아이디
     * @return 사용자 (Optional)
     */
    Optional<User> findByUsername(String username);

    /**
     * 아이디 존재 여부 확인
     *
     * @param username 사용자 아이디
     * @return 존재 여부
     */
    boolean existsByUsername(String username);

    /**
     * 이메일 존재 여부 확인
     *
     * @param email 이메일 주소
     * @return 존재 여부
     */
    boolean existsByEmail(String email);

    /**
     * 이메일로 사용자 조회
     *
     * @param email 이메일 주소
     * @return 사용자 (Optional)
     */
    Optional<User> findByEmail(String email);
}
