package com.example.gacha.domain.village;

import com.example.gacha.exception.BusinessException;
import com.example.gacha.exception.ErrorCode;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * CSV 파일에서 마을(여행지) 데이터를 읽어오는 컴포넌트
 * 캐싱을 통해 성능 최적화
 */
@Component
@Slf4j
public class VillageCsvReader {

    @Value("${csv.file.path}")
    private String csvFilePath;

    @Value("${csv.file.encoding:UTF-8}")
    private String encoding;

    /**
     * CSV 파일에서 모든 마을 데이터를 읽어옴
     * 캐싱을 사용하여 성능 최적화 (@Cacheable)
     *
     * @return 모든 마을 데이터 리스트
     */
    @Cacheable(value = "villages", key = "'all'")
    public List<VillageDto> readAllVillages() {
        log.info("Reading CSV file: {}", csvFilePath);

        List<VillageDto> villages = new ArrayList<>();

        try {
            Resource resource = new ClassPathResource(csvFilePath.replace("classpath:", ""));
            InputStream inputStream = resource.getInputStream();

            // BOM 제거를 위한 처리
            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(inputStream, encoding)
            );

            // 첫 번째 줄 읽기 (BOM 제거)
            String firstLine = reader.readLine();
            if (firstLine != null && firstLine.startsWith("\uFEFF")) {
                firstLine = firstLine.substring(1);
            }

            // 나머지 줄 읽기
            String remainingLines = reader.lines().collect(Collectors.joining("\n"));

            // CSV 파서 설정
            CSVParser parser = CSVFormat.DEFAULT
                    .withHeader() // 첫 줄을 헤더로 사용
                    .withIgnoreHeaderCase()
                    .withTrim()
                    .parse(new StringReader(firstLine + "\n" + remainingLines));

            long id = 1;
            for (CSVRecord record : parser) {
                try {
                    VillageDto village = VillageDto.builder()
                            .villageId(id++)
                            .villageName(getValue(record, "마을명"))
                            .sidoName(getValue(record, "시도명"))
                            .sigunguName(getValue(record, "시군구명"))
                            .address(getValue(record, "주소"))
                            .phoneNumber(getValue(record, "전화번호"))
                            .latitude(parseDouble(getValue(record, "위도")))
                            .longitude(parseDouble(getValue(record, "경도")))
                            .programName(getValue(record, "체험프로그램명"))
                            .programContent(getValue(record, "체험프로그램내용"))
                            .build();

                    villages.add(village);
                } catch (Exception e) {
                    log.warn("Failed to parse CSV record at line {}: {}", id, e.getMessage());
                }
            }

            log.info("Successfully loaded {} villages from CSV", villages.size());

        } catch (IOException e) {
            log.error("Failed to read CSV file: {}", e.getMessage(), e);
            throw new BusinessException(ErrorCode.CSV_READ_ERROR);
        }

        return villages;
    }

    /**
     * 필터링된 마을 데이터 조회
     *
     * @param region      시도명 필터 (선택사항)
     * @param programType 프로그램 유형 필터 (선택사항)
     * @return 필터링된 마을 리스트
     */
    public List<VillageDto> getFilteredVillages(String region, String programType) {
        List<VillageDto> allVillages = readAllVillages();

        return allVillages.stream()
                .filter(v -> region == null || region.isEmpty() ||
                        (v.getSidoName() != null && v.getSidoName().contains(region)))
                .filter(v -> programType == null || programType.isEmpty() ||
                        (v.getProgramName() != null && v.getProgramName().contains(programType)))
                .collect(Collectors.toList());
    }

    /**
     * ID로 마을 조회
     *
     * @param villageId 마을 ID
     * @return 마을 데이터 (Optional)
     */
    public Optional<VillageDto> findById(Long villageId) {
        return readAllVillages().stream()
                .filter(v -> v.getVillageId().equals(villageId))
                .findFirst();
    }

    /**
     * CSV 레코드에서 값 추출
     */
    private String getValue(CSVRecord record, String columnName) {
        try {
            String value = record.get(columnName);
            return value != null && !value.trim().isEmpty() ? value.trim() : null;
        } catch (IllegalArgumentException e) {
            // 컬럼이 없는 경우 null 반환
            return null;
        }
    }

    /**
     * 문자열을 Double로 파싱
     */
    private Double parseDouble(String value) {
        if (value == null || value.trim().isEmpty()) {
            return null;
        }
        try {
            return Double.parseDouble(value.trim());
        } catch (NumberFormatException e) {
            return null;
        }
    }
}
