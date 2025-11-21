package com.example.gacha.domain.village;

import com.example.gacha.exception.BusinessException;
import com.example.gacha.exception.ErrorCode;
import com.example.gacha.util.ImageUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * CSV 파일에서 마을(여행지) 데이터를 읽어오는 컴포넌트
 * 캐싱을 통해 성능 최적화
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class VillageCsvReader {

    private final ImageUtil imageUtil;

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

            // 인코딩을 명시적으로 지정하여 Reader 생성
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, encoding);

            // CSV 파서 설정 - Reader를 직접 전달
            CSVParser parser = CSVFormat.DEFAULT
                    .builder()
                    .setHeader()
                    .setSkipHeaderRecord(false)
                    .setIgnoreHeaderCase(true)
                    .setTrim(true)
                    .setIgnoreSurroundingSpaces(true)
                    .build()
                    .parse(inputStreamReader);

            long id = 1;
            for (CSVRecord record : parser) {
                try {
                    Long villageId = id++;

                    // 주소는 도로명주소 우선, 없으면 지번주소
                    String address = getValue(record, "소재지도로명주소");
                    if (address == null || address.isEmpty()) {
                        address = getValue(record, "소재지지번주소");
                    }

                    VillageDto village = VillageDto.builder()
                            .villageId(villageId)
                            .villageName(getValue(record, "체험마을명"))
                            .sidoName(getValue(record, "시도명"))
                            .sigunguName(getValue(record, "시군구명"))
                            .address(address)
                            .phoneNumber(getValue(record, "대표전화번호"))
                            .latitude(parseDouble(getValue(record, "위도")))
                            .longitude(parseDouble(getValue(record, "경도")))
                            .programName(getValue(record, "체험프로그램명"))
                            .programContent(getValue(record, "체험프로그램구분"))
                            .imageUrl(imageUtil.generatePicsumImageUrl(villageId))
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
