package com.example.gacha.config;

import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.concurrent.ConcurrentMapCache;
import org.springframework.cache.support.SimpleCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Arrays;

/**
 * 캐시 설정
 * CSV 파일에서 읽은 Village 데이터를 캐싱
 */
@Configuration
@EnableCaching
public class CacheConfig {

    /**
     * Simple Cache Manager 설정
     * villages 캐시를 사용하여 CSV 데이터 캐싱
     */
    @Bean
    public CacheManager cacheManager() {
        SimpleCacheManager cacheManager = new SimpleCacheManager();
        cacheManager.setCaches(Arrays.asList(
                new ConcurrentMapCache("villages")
        ));
        return cacheManager;
    }
}
