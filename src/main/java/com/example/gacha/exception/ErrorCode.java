package com.example.gacha.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 비즈니스 예외 에러 코드
 */
@Getter
@AllArgsConstructor
public enum ErrorCode {
    // 인증 관련
    USERNAME_ALREADY_EXISTS("이미 존재하는 아이디입니다.", "USERNAME_ALREADY_EXISTS"),
    EMAIL_ALREADY_EXISTS("이미 존재하는 이메일입니다.", "EMAIL_ALREADY_EXISTS"),
    INVALID_CREDENTIALS("아이디 또는 비밀번호가 올바르지 않습니다.", "INVALID_CREDENTIALS"),
    INVALID_TOKEN("유효하지 않은 토큰입니다.", "INVALID_TOKEN"),
    UNAUTHORIZED("인증이 필요합니다.", "UNAUTHORIZED"),

    // 사용자 관련
    USER_NOT_FOUND("사용자를 찾을 수 없습니다.", "USER_NOT_FOUND"),

    // 여행지 관련
    VILLAGE_NOT_FOUND("여행지를 찾을 수 없습니다.", "VILLAGE_NOT_FOUND"),
    NO_VILLAGES_AVAILABLE("조건에 맞는 여행지가 없습니다.", "NO_VILLAGES_AVAILABLE"),
    CSV_READ_ERROR("CSV 파일을 읽는 중 오류가 발생했습니다.", "CSV_READ_ERROR"),

    // 가챠 관련
    DAILY_LIMIT_EXCEEDED("오늘 가챠 횟수를 모두 사용했습니다. 내일 다시 시도해주세요.", "DAILY_LIMIT_EXCEEDED"),

    // 컬렉션 관련
    ALREADY_COLLECTED("이미 컬렉션에 추가된 여행지입니다.", "ALREADY_COLLECTED"),
    COLLECTION_NOT_FOUND("컬렉션을 찾을 수 없습니다.", "COLLECTION_NOT_FOUND"),
    FORBIDDEN("권한이 없습니다.", "FORBIDDEN"),

    // 추억 관련
    MEMORY_NOT_FOUND("추억을 찾을 수 없습니다.", "MEMORY_NOT_FOUND"),

    // 공통
    BAD_REQUEST("잘못된 요청입니다.", "BAD_REQUEST"),
    INTERNAL_SERVER_ERROR("서버 오류가 발생했습니다.", "INTERNAL_SERVER_ERROR");

    private final String message;
    private final String code;
}
