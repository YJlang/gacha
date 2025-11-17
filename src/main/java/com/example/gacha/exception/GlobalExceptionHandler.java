package com.example.gacha.exception;

import com.example.gacha.dto.response.ApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

/**
 * 전역 예외 처리 핸들러
 */
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    /**
     * BusinessException 처리
     */
    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ApiResponse<?>> handleBusinessException(BusinessException e) {
        log.error("BusinessException: {} - {}", e.getErrorCode().getCode(), e.getMessage());

        ApiResponse<?> response = ApiResponse.error(
                e.getErrorCode().getMessage(),
                e.getErrorCode().getCode()
        );

        // 에러 코드에 따라 HTTP 상태 코드 설정
        HttpStatus status = getHttpStatus(e.getErrorCode());

        return ResponseEntity.status(status).body(response);
    }

    /**
     * Validation 예외 처리 (@Valid 검증 실패)
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<?>> handleValidationException(MethodArgumentNotValidException e) {
        log.error("Validation Exception: {}", e.getMessage());

        // 첫 번째 에러 메시지 사용
        String message = e.getBindingResult().getAllErrors().get(0).getDefaultMessage();

        ApiResponse<?> response = ApiResponse.error(message, "VALIDATION_ERROR");

        return ResponseEntity.badRequest().body(response);
    }

    /**
     * IllegalArgumentException 처리
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiResponse<?>> handleIllegalArgumentException(IllegalArgumentException e) {
        log.error("IllegalArgumentException: {}", e.getMessage());

        ApiResponse<?> response = ApiResponse.error(
                e.getMessage(),
                "BAD_REQUEST"
        );

        return ResponseEntity.badRequest().body(response);
    }

    /**
     * 기타 모든 예외 처리
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<?>> handleException(Exception e) {
        log.error("Unexpected Exception: {}", e.getMessage(), e);

        ApiResponse<?> response = ApiResponse.error(
                "서버 오류가 발생했습니다.",
                "INTERNAL_SERVER_ERROR"
        );

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }

    /**
     * ErrorCode에 따른 HTTP 상태 코드 매핑
     */
    private HttpStatus getHttpStatus(ErrorCode errorCode) {
        return switch (errorCode) {
            case UNAUTHORIZED, INVALID_TOKEN, INVALID_CREDENTIALS -> HttpStatus.UNAUTHORIZED;
            case FORBIDDEN -> HttpStatus.FORBIDDEN;
            case USER_NOT_FOUND, VILLAGE_NOT_FOUND, COLLECTION_NOT_FOUND, MEMORY_NOT_FOUND -> HttpStatus.NOT_FOUND;
            case DAILY_LIMIT_EXCEEDED -> HttpStatus.TOO_MANY_REQUESTS;
            case INTERNAL_SERVER_ERROR -> HttpStatus.INTERNAL_SERVER_ERROR;
            default -> HttpStatus.BAD_REQUEST;
        };
    }
}
