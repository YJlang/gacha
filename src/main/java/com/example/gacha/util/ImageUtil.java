package com.example.gacha.util;

import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

/**
 * 이미지 URL 생성 유틸리티
 * Unsplash API를 사용하여 랜덤 이미지 URL 생성
 */
@Component
public class ImageUtil {

    private static final Random random = new Random();

    // 여행지 관련 키워드
    private static final List<String> KEYWORDS = Arrays.asList(
            "village", "countryside", "nature", "landscape",
            "mountains", "forest", "rural", "farm",
            "traditional-house", "korean-village", "temple",
            "river", "lake", "hills", "field"
    );

    /**
     * 여행지에 맞는 랜덤 이미지 URL 생성
     * Unsplash의 무료 이미지 API 사용
     *
     * @param villageId 여행지 ID (시드값으로 사용)
     * @param villageName 여행지 이름
     * @return 이미지 URL
     */
    public String generateImageUrl(Long villageId, String villageName) {
        // villageId를 시드로 사용하여 항상 같은 이미지 반환 (일관성)
        String keyword = KEYWORDS.get((int) (villageId % KEYWORDS.size()));

        // Unsplash Source API: 크기와 키워드로 랜덤 이미지 생성
        // 800x600 크기의 이미지
        return String.format("https://source.unsplash.com/800x600/?%s&sig=%d",
                keyword, villageId);
    }

    /**
     * 완전 랜덤 이미지 URL 생성 (매번 다른 이미지)
     *
     * @return 이미지 URL
     */
    public String generateRandomImageUrl() {
        String keyword = KEYWORDS.get(random.nextInt(KEYWORDS.size()));
        long randomSeed = System.currentTimeMillis() + random.nextInt(10000);

        return String.format("https://source.unsplash.com/800x600/?%s&sig=%d",
                keyword, randomSeed);
    }

    /**
     * Picsum Photos API를 사용한 이미지 생성
     * ID 기반 방식으로 더 안정적
     *
     * @param villageId 여행지 ID
     * @return 이미지 URL
     */
    public String generatePicsumImageUrl(Long villageId) {
        // Lorem Picsum: ID 기반으로 항상 같은 이미지 반환 (1000개 이미지 순환)
        long imageId = (villageId % 1000) + 1; // 1~1000 사이의 ID
        return String.format("https://picsum.photos/id/%d/800/600", imageId);
    }
}
