package com.example.gacha.config;

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

    /**
     * CORS 설정
     * - React 개발 서버(localhost:3000)에서 Spring Boot API(localhost:8080)로의 요청 허용
     */
    @Override
    public void addCorsMappings(@NonNull CorsRegistry registry) {
        registry.addMapping("/**") // 모든 경로에 대해 CORS 허용
                .allowedOrigins(
                        "http://localhost:3000", // React 개발 서버
                        "http://127.0.0.1:3000", // localhost의 다른 표현
                        "https://travelgacha.netlify.app", // Netlify 배포
                        "https://gachalikealion.duckdns.org", // DuckDNS 도메인
                        "http://gachalikealion.duckdns.org") // HTTP도 허용
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH") // 허용할 HTTP 메서드
                .allowedHeaders("*") // 모든 헤더 허용
                .allowCredentials(true) // 쿠키/인증 정보 허용
                .exposedHeaders("Authorization") // Authorization 헤더 노출
                .maxAge(3600); // Pre-flight 요청 캐시 시간 (1시간)
    }

    /**
     * 정적 리소스 핸들러 설정
     * - 업로드된 이미지를 정적 리소스로 제공
     */
    @Override
    public void addResourceHandlers(@NonNull ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/uploads/**")
                .addResourceLocations("file:uploads/");
    }
}
