package com.example.gacha.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.data.domain.Page;

import java.util.List;

/**
 * 페이지네이션 응답 DTO
 * Spring의 Page 객체를 감싸서 currentPage 필드를 제공합니다.
 */
@Getter
@AllArgsConstructor
public class PageResponse<T> {
    private List<T> content;
    private int totalPages;
    private long totalElements;
    private int currentPage; // Spring의 number 대신 currentPage 사용
    private int size;

    /**
     * Spring의 Page 객체를 PageResponse로 변환
     */
    public static <T> PageResponse<T> from(Page<T> page) {
        return new PageResponse<>(
                page.getContent(),
                page.getTotalPages(),
                page.getTotalElements(),
                page.getNumber(), // number -> currentPage로 매핑
                page.getSize());
    }
}
