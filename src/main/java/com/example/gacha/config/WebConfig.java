package com.example.gacha.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.lang.NonNull;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Web 설정 (CORS 등)
 * 프론트엔드(React)와 백엔드(Spring Boot) 간의 통신을 위한 CORS 설정
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Value("${file.upload.dir:/opt/gacha/uploads/memories}")
    private String uploadDir;

    /**
     * CORS 설정
     * - React 개발 서버(localhost:3000)에서 Spring Boot API(localhost:8080)로의 요청 허용
     * - EC2 환경에서는 EC2 Public IP도 허용 (환경에 따라 동적으로 추가 가능)
     */
    @Override
    public void addCorsMappings(@NonNull CorsRegistry registry) {
        registry.addMapping("/**") // 모든 경로에 대해 CORS 허용
                .allowedOriginPatterns("*") // 모든 origin 허용 (프로덕션에서는 특정 도메인으로 제한 권장)
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH") // 허용할 HTTP 메서드
                .allowedHeaders("*") // 모든 헤더 허용
                .allowCredentials(true) // 쿠키/인증 정보 허용
                .exposedHeaders("Authorization") // Authorization 헤더 노출
                .maxAge(3600); // Pre-flight 요청 캐시 시간 (1시간)
    }

    /**
     * 정적 리소스 핸들러 설정
     * - 업로드된 이미지를 정적 리소스로 제공
     * - EC2 환경에서는 절대 경로 사용 (예: /opt/gacha/uploads/memories)
     */
    @Override
    public void addResourceHandlers(@NonNull ResourceHandlerRegistry registry) {
        // 업로드 디렉토리의 상위 디렉토리를 사용
        String uploadLocation = uploadDir.endsWith("/")
                ? "file:" + uploadDir.substring(0, uploadDir.lastIndexOf('/', uploadDir.length() - 2) + 1)
                : "file:" + uploadDir.substring(0, uploadDir.lastIndexOf('/') + 1);

        registry.addResourceHandler("/uploads/**")
                .addResourceLocations(uploadLocation);
    }
}
