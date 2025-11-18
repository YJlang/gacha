package com.example.gacha.service;

import com.example.gacha.domain.collection.CollectionRepository;
import com.example.gacha.domain.memory.MemoryRepository;
import com.example.gacha.domain.user.User;
import com.example.gacha.domain.user.UserRepository;
import com.example.gacha.dto.request.UserUpdateRequest;
import com.example.gacha.dto.response.UserResponse;
import com.example.gacha.exception.BusinessException;
import com.example.gacha.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 사용자(마이페이지) 관련 서비스
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class UserService {

    private final UserRepository userRepository;
    private final CollectionRepository collectionRepository;
    private final MemoryRepository memoryRepository;

    /**
     * 내 정보 조회 (통계 포함)
     */
    public UserResponse getMyInfo(Long userId) {
        // 1. 사용자 조회
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        // 2. 통계 계산
        long collectionCount = collectionRepository.countByUserId(userId);
        long memoryCount = memoryRepository.countByUserId(userId);

        // 3. 응답 생성
        return UserResponse.withStats(user, collectionCount, memoryCount);
    }

    /**
     * 내 정보 수정
     */
    @Transactional
    public UserResponse updateMyInfo(Long userId, UserUpdateRequest request) {
        // 1. 사용자 조회
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        // 2. 이메일 수정 (변경된 경우에만)
        if (request.getEmail() != null && !request.getEmail().equals(user.getEmail())) {
            // 이메일 중복 확인
            if (userRepository.existsByEmail(request.getEmail())) {
                throw new BusinessException(ErrorCode.EMAIL_ALREADY_EXISTS);
            }
            user.setEmail(request.getEmail());
        }

        // 3. 저장
        User updatedUser = userRepository.save(user);
        log.info("User {} updated profile", userId);

        // 4. 통계 포함하여 응답
        long collectionCount = collectionRepository.countByUserId(userId);
        long memoryCount = memoryRepository.countByUserId(userId);

        return UserResponse.withStats(updatedUser, collectionCount, memoryCount);
    }
}
