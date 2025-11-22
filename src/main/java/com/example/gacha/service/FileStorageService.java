package com.example.gacha.service;

import com.example.gacha.exception.BusinessException;
import com.example.gacha.exception.ErrorCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

/**
 * 파일 저장 및 삭제를 담당하는 서비스
 */
@Service
@Slf4j
public class FileStorageService {

    private final Path fileStorageLocation;
    private final String baseUrl;

    // 허용되는 이미지 확장자
    private static final List<String> ALLOWED_EXTENSIONS = Arrays.asList("jpg", "jpeg", "png", "gif", "webp");

    public FileStorageService(
            @Value("${file.upload.dir}") String uploadDir,
            @Value("${file.upload.base-url}") String baseUrl) {
        this.fileStorageLocation = Paths.get(uploadDir).toAbsolutePath().normalize();
        this.baseUrl = baseUrl;

        try {
            Files.createDirectories(this.fileStorageLocation);
            log.info("File storage directory created: {}", this.fileStorageLocation);
        } catch (IOException ex) {
            throw new RuntimeException("파일 저장 디렉토리를 생성할 수 없습니다.", ex);
        }
    }

    /**
     * 추억 이미지 저장
     * 
     * @param file 업로드할 파일
     * @return 저장된 파일의 URL
     */
    public String storeMemoryImage(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            return null;
        }

        // 1. 파일명 추출 및 검증
        String originalFilename = StringUtils.cleanPath(file.getOriginalFilename());
        if (originalFilename.contains("..")) {
            throw new BusinessException(ErrorCode.BAD_REQUEST);
        }

        // 2. 확장자 검증
        String extension = getFileExtension(originalFilename);
        if (!ALLOWED_EXTENSIONS.contains(extension.toLowerCase())) {
            throw new BusinessException(ErrorCode.BAD_REQUEST);
        }

        try {
            // 3. 고유한 파일명 생성 (UUID + 원본 파일명)
            String newFilename = UUID.randomUUID().toString() + "_" + originalFilename;
            Path targetLocation = this.fileStorageLocation.resolve(newFilename);

            // 4. 파일 저장
            Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);
            log.info("Stored file: {}", targetLocation);

            // 5. URL 반환
            return baseUrl + "/" + newFilename;
        } catch (IOException ex) {
            log.error("Failed to store file {}", originalFilename, ex);
            throw new BusinessException(ErrorCode.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * 추억 이미지 삭제
     * 
     * @param imageUrl 삭제할 이미지의 URL
     */
    public void deleteMemoryImage(String imageUrl) {
        if (imageUrl == null || imageUrl.isEmpty()) {
            return;
        }

        try {
            // URL에서 파일명 추출
            String filename = imageUrl.substring(imageUrl.lastIndexOf("/") + 1);
            Path filePath = this.fileStorageLocation.resolve(filename).normalize();

            // 파일 삭제
            Files.deleteIfExists(filePath);
            log.info("Deleted file: {}", filePath);
        } catch (IOException ex) {
            log.error("Failed to delete file from URL: {}", imageUrl, ex);
            // 파일 삭제 실패는 치명적이지 않으므로 예외를 던지지 않음
        }
    }

    /**
     * 파일 확장자 추출
     */
    private String getFileExtension(String filename) {
        int lastIndexOf = filename.lastIndexOf(".");
        if (lastIndexOf == -1) {
            return "";
        }
        return filename.substring(lastIndexOf + 1);
    }
}
