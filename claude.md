# Spring Boot 백엔드 개발 가이드

## 프로젝트 개요

### 서비스 소개
**가챠 여행 서비스 (Gacha Travel Service)**는 공공데이터를 활용하여 왜소된 지역이나 특정 지역의 여행 상품성을 높이기 위한 서비스입니다. 사용자는 가챠 시스템을 통해 랜덤으로 여행지를 추천받고, 이를 컬렉션으로 모으며, 실제 방문 후 추억을 기록할 수 있습니다.

### 핵심 가치
- **지역 활성화**: 공공데이터를 활용하여 덜 알려진 지역의 여행지를 발굴하고 홍보
- **재미있는 발견**: 가챠 시스템을 통해 우연한 여행지 발견의 즐거움 제공
- **추억 기록**: 여행 경험을 기록하고 공유할 수 있는 플랫폼 제공

### 주요 기능
1. **가챠 시스템**: 하루 1회 랜덤 여행지 추천 (지역/프로그램 유형 필터링 가능)
2. **컬렉션**: 뽑은 여행지를 저장하고 관리
3. **추억 기록**: 실제 방문 후 여행 추억을 작성하고 관리
4. **마이페이지**: 내 컬렉션과 추억 통계 확인

### 타겟 사용자
- 새로운 여행지를 찾고 싶은 사람
- 덜 알려진 지역을 탐험하고 싶은 사람
- 여행 추억을 기록하고 싶은 사람
- 가챠 시스템의 재미를 즐기고 싶은 사람

### 기술 스택
- **Backend**: Spring Boot 3.x, Java 17+
- **Database**: MySQL 8.0
- **ORM**: JPA/Hibernate
- **Build Tool**: Maven 또는 Gradle
- **기타**: Lombok, JWT, Apache Commons CSV

### 개발 원칙
- **빠른 MVP 개발 우선**: 해커톤 환경에 맞춰 빠른 프로토타입 개발
- **보안 최소화**: MVP 단계에서는 보안보다 기능 구현 우선
- **공공데이터 활용**: CSV 파일을 동적으로 읽어서 최신 데이터 제공
- **사용자 경험 중심**: 직관적이고 재미있는 UX 제공

---

## 개발 환경 설정

### 필수 요구사항
- JDK 17 이상
- MySQL 8.0 이상
- Spring Boot 3.x
- Maven 또는 Gradle

### 의존성 (pom.xml 또는 build.gradle)
```xml
<dependencies>
    <!-- Spring Boot Starter Web -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-web</artifactId>
    </dependency>
    
    <!-- Spring Boot Starter Data JPA -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-data-jpa</artifactId>
    </dependency>
    
    <!-- MySQL Driver -->
    <dependency>
        <groupId>com.mysql</groupId>
        <artifactId>mysql-connector-j</artifactId>
        <scope>runtime</scope>
    </dependency>
    
    <!-- Lombok -->
    <dependency>
        <groupId>org.projectlombok</groupId>
        <artifactId>lombok</artifactId>
        <optional>true</optional>
    </dependency>
    
    <!-- Validation -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-validation</artifactId>
    </dependency>
    
    <!-- JWT (간단한 토큰 인증용) -->
    <dependency>
        <groupId>io.jsonwebtoken</groupId>
        <artifactId>jjwt-api</artifactId>
        <version>0.11.5</version>
    </dependency>
    <dependency>
        <groupId>io.jsonwebtoken</groupId>
        <artifactId>jjwt-impl</artifactId>
        <version>0.11.5</version>
        <scope>runtime</scope>
    </dependency>
    <dependency>
        <groupId>io.jsonwebtoken</groupId>
        <artifactId>jjwt-jackson</artifactId>
        <version>0.11.5</version>
        <scope>runtime</scope>
    </dependency>
    
    <!-- CSV 파싱을 위한 Apache Commons CSV -->
    <dependency>
        <groupId>org.apache.commons</groupId>
        <artifactId>commons-csv</artifactId>
        <version>1.10.0</version>
    </dependency>
    
    <!-- 캐싱을 위한 Spring Cache -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-cache</artifactId>
    </dependency>
</dependencies>
```

### application.yml 설정
```yaml
spring:
  application:
    name: gacha-travel-service
  
  datasource:
    url: jdbc:mysql://localhost:3306/gacha_travel?useSSL=false&serverTimezone=Asia/Seoul&characterEncoding=UTF-8
    username: root
    password: your_password
    driver-class-name: com.mysql.cj.jdbc.Driver
  
  jpa:
    hibernate:
      ddl-auto: update
    show-sql: true
    properties:
      hibernate:
        format_sql: true
        dialect: org.hibernate.dialect.MySQL8Dialect
  
  jackson:
    serialization:
      write-dates-as-timestamps: false
    time-zone: Asia/Seoul
  
  cache:
    type: simple
    cache-names: villages

server:
  port: 8080

jwt:
  secret: your-secret-key-change-in-production
  expiration: 86400000 # 24시간 (밀리초)

csv:
  file:
    path: classpath:data/전국농어촌체험휴양마을표준데이터.csv
    encoding: UTF-8
    cache:
      enabled: true
      ttl: 3600 # 1시간 (초)
```

---

## 서비스 플로우 및 사용자 시나리오

### 주요 사용자 플로우

#### 1. 신규 사용자 온보딩
```
회원가입 → 로그인 → 온보딩 화면 → 메인 화면
```

#### 2. 가챠 뽑기 플로우
```
메인 화면 → 가챠 뽑기 버튼 클릭 → 랜덤 여행지 표시 → 
상세 정보 확인 → 컬렉션에 저장 (선택) → 추억 작성 (선택)
```

#### 3. 컬렉션 관리 플로우
```
마이페이지 → 내 컬렉션 조회 → 여행지 상세 보기 → 
추억 작성 또는 컬렉션에서 제거
```

#### 4. 추억 작성 플로우
```
여행지 상세 → 추억 작성 → 내용 입력 → 저장 → 
나의 추억 목록에서 확인
```

### 비즈니스 로직

#### 가챠 시스템
- **하루 1회 제한**: 사용자는 하루에 한 번만 가챠를 뽑을 수 있음
- **랜덤 추천**: CSV 파일의 모든 여행지 중 랜덤으로 선택
- **필터링 옵션**: 지역(시도명) 또는 프로그램 유형으로 필터링 가능
- **이력 관리**: 가챠 이력을 저장하여 제한 확인 및 통계 활용

#### 컬렉션 시스템
- **중복 방지**: 같은 여행지는 한 번만 컬렉션에 추가 가능
- **통계 제공**: 지역별 컬렉션 통계 제공
- **관리 기능**: 컬렉션에서 제거 가능

#### 추억 시스템
- **자유 작성**: 사용자가 자유롭게 여행 추억 작성
- **날짜 기록**: 방문 날짜 기록 가능
- **수정/삭제**: 작성한 추억 수정 및 삭제 가능

### 데이터 흐름

#### CSV 데이터 처리
```
CSV 파일 (resources/data/) → VillageCsvReader → 
캐싱 (메모리) → VillageService → API 응답
```

#### 사용자 데이터 처리
```
사용자 요청 → Controller → Service → Repository → 
MySQL Database → 응답
```

---

## 프로젝트 구조

```
src/main/java/com/gacha/travel/
├── GachaTravelApplication.java
├── config/
│   ├── JwtConfig.java
│   ├── WebConfig.java
│   └── CacheConfig.java
├── domain/
│   ├── user/
│   │   ├── User.java
│   │   ├── UserRepository.java
│   │   └── UserService.java
│   ├── village/
│   │   ├── VillageDto.java (CSV에서 읽은 데이터를 담는 DTO)
│   │   ├── VillageCsvReader.java (CSV 파일 읽기)
│   │   └── VillageService.java (CSV 데이터 조회 서비스)
│   ├── collection/
│   │   ├── Collection.java
│   │   ├── CollectionRepository.java
│   │   └── CollectionService.java
│   ├── memory/
│   │   ├── Memory.java
│   │   ├── MemoryRepository.java
│   │   └── MemoryService.java
│   └── gacha/
│       ├── GachaHistory.java
│       ├── GachaHistoryRepository.java
│       └── GachaService.java
├── dto/
│   ├── request/
│   │   ├── SignupRequest.java
│   │   ├── LoginRequest.java
│   │   ├── GachaDrawRequest.java
│   │   ├── CollectionRequest.java
│   │   └── MemoryRequest.java
│   └── response/
│       ├── ApiResponse.java
│       ├── AuthResponse.java
│       ├── VillageResponse.java
│       ├── CollectionResponse.java
│       └── MemoryResponse.java
├── controller/
│   ├── AuthController.java
│   ├── GachaController.java
│   ├── VillageController.java
│   ├── CollectionController.java
│   ├── MemoryController.java
│   └── UserController.java
├── util/
│   ├── JwtUtil.java
│   └── PasswordUtil.java
└── exception/
    ├── GlobalExceptionHandler.java
    ├── BusinessException.java
    └── ErrorCode.java

src/main/resources/
├── application.yml
└── data/
    └── 전국농어촌체험휴양마을표준데이터.csv
```

---

## 개발 규칙

### 1. Entity 클래스 작성 규칙
- `@Entity` 어노테이션 사용
- `@Table(name = "table_name")` 명시
- Lombok `@Getter`, `@Setter`, `@NoArgsConstructor`, `@AllArgsConstructor` 사용
- `@Builder` 패턴 적극 활용
- `@CreatedDate`, `@LastModifiedDate` 사용 (BaseEntity 상속 권장)
- `@Id`, `@GeneratedValue(strategy = GenerationType.IDENTITY)` 사용

**예시:**
```java
@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userId;
    
    @Column(nullable = false, unique = true, length = 50)
    private String username;
    
    @Column(nullable = false)
    private String password;
    
    @Column(nullable = false, unique = true)
    private String email;
    
    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;
}
```

### 2. Repository 작성 규칙
- `JpaRepository<Entity, ID>` 상속
- 메서드 네이밍 규칙 준수 (findBy, existsBy 등)
- `@Query` 어노테이션으로 복잡한 쿼리 작성
- 커스텀 쿼리는 가능한 한 메서드 네이밍으로 해결

**예시:**
```java
@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
    
    @Query("SELECT u FROM User u WHERE u.email = :email")
    Optional<User> findByEmail(@Param("email") String email);
}
```

### 3. DTO 작성 규칙
- Request DTO는 `@Valid` 어노테이션과 함께 사용
- `@NotNull`, `@NotBlank`, `@Email`, `@Size` 등 Validation 어노테이션 활용
- Lombok `@Getter`, `@Setter`, `@NoArgsConstructor`, `@AllArgsConstructor` 사용
- `@Builder` 패턴 사용

**예시:**
```java
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SignupRequest {
    @NotBlank(message = "아이디는 필수입니다.")
    @Size(min = 3, max = 50, message = "아이디는 3자 이상 50자 이하여야 합니다.")
    private String username;
    
    @NotBlank(message = "비밀번호는 필수입니다.")
    @Size(min = 6, message = "비밀번호는 6자 이상이어야 합니다.")
    private String password;
    
    @NotBlank(message = "이메일은 필수입니다.")
    @Email(message = "올바른 이메일 형식이 아닙니다.")
    private String email;
}
```

### 4. Service 작성 규칙
- `@Service` 어노테이션 사용
- 비즈니스 로직은 Service에 구현
- Repository는 Service에서만 사용
- 트랜잭션은 `@Transactional` 어노테이션 사용
- 예외는 `BusinessException`으로 처리

**예시:**
```java
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {
    private final UserRepository userRepository;
    private final PasswordUtil passwordUtil;
    
    @Transactional
    public UserResponse signup(SignupRequest request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new BusinessException(ErrorCode.USERNAME_ALREADY_EXISTS);
        }
        
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new BusinessException(ErrorCode.EMAIL_ALREADY_EXISTS);
        }
        
        String encodedPassword = passwordUtil.encode(request.getPassword());
        
        User user = User.builder()
                .username(request.getUsername())
                .password(encodedPassword)
                .email(request.getEmail())
                .build();
        
        User savedUser = userRepository.save(user);
        return UserResponse.from(savedUser);
    }
}
```

### 5. Controller 작성 규칙
- `@RestController` 어노테이션 사용
- `@RequestMapping("/api/...")`로 API 경로 명시
- `@Valid` 어노테이션으로 Request DTO 검증
- 모든 응답은 `ApiResponse<T>`로 래핑
- HTTP 상태 코드 명시 (`@ResponseStatus` 또는 ResponseEntity 사용)

**예시:**
```java
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;
    
    @PostMapping("/signup")
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<AuthResponse> signup(@Valid @RequestBody SignupRequest request) {
        AuthResponse response = authService.signup(request);
        return ApiResponse.success(response, "회원가입이 완료되었습니다.");
    }
    
    @PostMapping("/login")
    public ApiResponse<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        AuthResponse response = authService.login(request);
        return ApiResponse.success(response, "로그인 성공");
    }
}
```

### 6. 공통 응답 형식
- 모든 API 응답은 `ApiResponse<T>`로 통일
- 성공/실패 여부는 `success` 필드로 구분
- 데이터는 `data` 필드에 포함
- 메시지는 `message` 필드에 포함

**예시:**
```java
@Getter
@Builder
public class ApiResponse<T> {
    private boolean success;
    private String message;
    private T data;
    
    public static <T> ApiResponse<T> success(T data) {
        return ApiResponse.<T>builder()
                .success(true)
                .data(data)
                .build();
    }
    
    public static <T> ApiResponse<T> success(T data, String message) {
        return ApiResponse.<T>builder()
                .success(true)
                .message(message)
                .data(data)
                .build();
    }
    
    public static <T> ApiResponse<T> error(String message, String error) {
        return ApiResponse.<T>builder()
                .success(false)
                .message(message)
                .error(error)
                .build();
    }
}
```

### 7. 예외 처리 규칙
- 커스텀 예외는 `BusinessException` 사용
- `@ControllerAdvice`로 전역 예외 처리
- 에러 코드는 `ErrorCode` enum으로 관리

**예시:**
```java
@Getter
@AllArgsConstructor
public enum ErrorCode {
    USERNAME_ALREADY_EXISTS("이미 존재하는 아이디입니다.", "USERNAME_ALREADY_EXISTS"),
    EMAIL_ALREADY_EXISTS("이미 존재하는 이메일입니다.", "EMAIL_ALREADY_EXISTS"),
    INVALID_CREDENTIALS("아이디 또는 비밀번호가 올바르지 않습니다.", "INVALID_CREDENTIALS"),
    VILLAGE_NOT_FOUND("여행지를 찾을 수 없습니다.", "VILLAGE_NOT_FOUND"),
    ALREADY_COLLECTED("이미 컬렉션에 추가된 여행지입니다.", "ALREADY_COLLECTED"),
    DAILY_LIMIT_EXCEEDED("오늘 가챠 횟수를 모두 사용했습니다.", "DAILY_LIMIT_EXCEEDED"),
    NO_VILLAGES_AVAILABLE("조건에 맞는 여행지가 없습니다.", "NO_VILLAGES_AVAILABLE"),
    CSV_READ_ERROR("CSV 파일을 읽는 중 오류가 발생했습니다.", "CSV_READ_ERROR");
    
    private final String message;
    private final String code;
}

@Getter
public class BusinessException extends RuntimeException {
    private final ErrorCode errorCode;
    
    public BusinessException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }
}

@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {
    
    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ApiResponse<?>> handleBusinessException(BusinessException e) {
        log.error("BusinessException: {}", e.getMessage());
        ApiResponse<?> response = ApiResponse.error(
            e.getErrorCode().getMessage(),
            e.getErrorCode().getCode()
        );
        return ResponseEntity.badRequest().body(response);
    }
    
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<?>> handleValidationException(MethodArgumentNotValidException e) {
        String message = e.getBindingResult().getAllErrors().get(0).getDefaultMessage();
        ApiResponse<?> response = ApiResponse.error(message, "VALIDATION_ERROR");
        return ResponseEntity.badRequest().body(response);
    }
}
```

### 8. JWT 인증 (간단한 구현)
- Spring Security 사용하지 않음
- 간단한 JWT 토큰 기반 인증
- `@RequestHeader("Authorization")`로 토큰 받기
- 토큰 검증은 Interceptor 또는 Filter에서 처리 (선택사항)

**예시:**
```java
@Component
@RequiredArgsConstructor
public class JwtUtil {
    @Value("${jwt.secret}")
    private String secret;
    
    @Value("${jwt.expiration}")
    private Long expiration;
    
    public String generateToken(Long userId, String username) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + expiration);
        
        return Jwts.builder()
                .setSubject(String.valueOf(userId))
                .claim("username", username)
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(SignatureAlgorithm.HS512, secret)
                .compact();
    }
    
    public Long getUserIdFromToken(String token) {
        Claims claims = Jwts.parser()
                .setSigningKey(secret)
                .parseClaimsJws(token)
                .getBody();
        
        return Long.parseLong(claims.getSubject());
    }
    
    public boolean validateToken(String token) {
        try {
            Jwts.parser().setSigningKey(secret).parseClaimsJws(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }
}
```

### 9. 비밀번호 암호화
- Spring Security의 BCrypt 사용하지 않음
- 간단한 해시 함수 사용 (예: SHA-256) 또는 간단한 BCrypt 직접 구현

**예시:**
```java
@Component
public class PasswordUtil {
    
    public String encode(String rawPassword) {
        // 간단한 해시 (실제로는 BCrypt 권장하지만 MVP이므로 간단하게)
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hash = md.digest(rawPassword.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(hash);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Password encoding failed", e);
        }
    }
    
    public boolean matches(String rawPassword, String encodedPassword) {
        return encode(rawPassword).equals(encodedPassword);
    }
}
```

### 10. 가챠 로직 구현
- 하루 1회 제한 (또는 여러 번 가능하도록 설정)
- CSV 파일에서 랜덤으로 Village 선택
- 필터링 옵션 지원 (지역, 프로그램 유형)
- 가챠 이력 저장

**예시:**
```java
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class GachaService {
    private final VillageService villageService;
    private final GachaHistoryRepository gachaHistoryRepository;
    
    @Transactional
    public VillageResponse drawGacha(Long userId, GachaDrawRequest request) {
        // 오늘 가챠 횟수 확인
        LocalDateTime todayStart = LocalDateTime.now().withHour(0).withMinute(0).withSecond(0);
        long todayCount = gachaHistoryRepository.countByUserIdAndDrawnAtAfter(userId, todayStart);
        
        if (todayCount >= 1) { // 하루 1회 제한
            throw new BusinessException(ErrorCode.DAILY_LIMIT_EXCEEDED);
        }
        
        // CSV 파일에서 랜덤 여행지 선택
        VillageDto selectedVillage = villageService.getRandomVillage(
            request.getRegion(), 
            request.getProgramType()
        );
        
        // 가챠 이력 저장
        GachaHistory history = GachaHistory.builder()
                .userId(userId)
                .villageId(selectedVillage.getVillageId())
                .drawnAt(LocalDateTime.now())
                .build();
        gachaHistoryRepository.save(history);
        
        return VillageResponse.from(selectedVillage);
    }
}
```

---

## 데이터베이스 스키마

### users 테이블
```sql
CREATE TABLE users (
    user_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    email VARCHAR(100) NOT NULL UNIQUE,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP
);
```

### villages 테이블 (사용하지 않음)
**참고**: Village 데이터는 CSV 파일에서 동적으로 읽어오므로 데이터베이스 테이블이 필요하지 않습니다.
단, Collection과 Memory에서 villageId를 참조하므로, villageId는 CSV 파일의 인덱스 기반 ID를 사용합니다.

### collections 테이블
```sql
CREATE TABLE collections (
    collection_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    village_id BIGINT NOT NULL, -- CSV 파일의 villageId 참조
    collected_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE,
    UNIQUE KEY unique_user_village (user_id, village_id)
);
-- 주의: village_id는 CSV 파일의 ID를 참조하므로 외래키 제약조건 없음
```

### memories 테이블
```sql
CREATE TABLE memories (
    memory_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    village_id BIGINT NOT NULL, -- CSV 파일의 villageId 참조
    content TEXT NOT NULL,
    visit_date DATE,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE
);
-- 주의: village_id는 CSV 파일의 ID를 참조하므로 외래키 제약조건 없음
```

### gacha_history 테이블
```sql
CREATE TABLE gacha_history (
    gacha_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    village_id BIGINT NOT NULL, -- CSV 파일의 villageId 참조
    drawn_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE
);
-- 주의: village_id는 CSV 파일의 ID를 참조하므로 외래키 제약조건 없음
```

---

## CSV 파일 동적 읽기

### CSV 파일 위치
- **파일 경로**: `src/main/resources/data/전국농어촌체험휴양마을표준데이터.csv`
- CSV 파일을 프로젝트의 `resources/data` 폴더에 저장
- 빌드 시 JAR 파일에 포함되어 배포됨

### CSV 파일 구조
- 인코딩: UTF-8 (BOM 제거 필요할 수 있음)
- 컬럼 순서 (예상):
  1. 마을명
  2. 시도명
  3. 시군구명
  4. 체험프로그램명
  5. 체험프로그램내용
  6. 주소
  7. 전화번호
  8. 위도
  9. 경도
  10. 기타...

### VillageDto 클래스
```java
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VillageDto {
    private Long villageId; // CSV 인덱스 기반 ID
    private String villageName; // 마을명
    private String sidoName; // 시도명
    private String sigunguName; // 시군구명
    private String address; // 주소
    private String phoneNumber; // 전화번호
    private Double latitude; // 위도
    private Double longitude; // 경도
    private String programName; // 체험프로그램명
    private String programContent; // 체험프로그램내용
}
```

### CSV Reader 구현
```java
@Component
@Slf4j
public class VillageCsvReader {
    
    @Value("${csv.file.path}")
    private String csvFilePath;
    
    @Value("${csv.file.encoding:UTF-8}")
    private String encoding;
    
    /**
     * CSV 파일에서 모든 마을 데이터를 읽어옴
     * 캐싱을 사용하여 성능 최적화
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
            
            // CSV 파서 설정
            CSVParser parser = CSVFormat.DEFAULT
                .withHeader() // 첫 줄을 헤더로 사용
                .withIgnoreHeaderCase()
                .withTrim()
                .parse(new StringReader(firstLine + "\n" + reader.lines().collect(Collectors.joining("\n"))));
            
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
                    log.warn("Failed to parse CSV record: {}", e.getMessage());
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
     */
    public List<VillageDto> getFilteredVillages(String region, String programType) {
        List<VillageDto> allVillages = readAllVillages();
        
        return allVillages.stream()
            .filter(v -> region == null || v.getSidoName().contains(region))
            .filter(v -> programType == null || 
                (v.getProgramName() != null && v.getProgramName().contains(programType)))
            .collect(Collectors.toList());
    }
    
    /**
     * ID로 마을 조회
     */
    public Optional<VillageDto> findById(Long villageId) {
        return readAllVillages().stream()
            .filter(v -> v.getVillageId().equals(villageId))
            .findFirst();
    }
    
    private String getValue(CSVRecord record, String columnName) {
        try {
            String value = record.get(columnName);
            return value != null && !value.trim().isEmpty() ? value.trim() : null;
        } catch (IllegalArgumentException e) {
            // 컬럼이 없는 경우 null 반환
            return null;
        }
    }
    
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
```

### VillageService 수정
```java
@Service
@RequiredArgsConstructor
@Slf4j
public class VillageService {
    private final VillageCsvReader csvReader;
    
    /**
     * 모든 마을 조회 (페이지네이션)
     */
    public Page<VillageDto> getAllVillages(String region, String programType, Pageable pageable) {
        List<VillageDto> filteredVillages = csvReader.getFilteredVillages(region, programType);
        
        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), filteredVillages.size());
        List<VillageDto> pageContent = filteredVillages.subList(start, end);
        
        return new PageImpl<>(pageContent, pageable, filteredVillages.size());
    }
    
    /**
     * ID로 마을 조회
     */
    public VillageDto getVillageById(Long villageId) {
        return csvReader.findById(villageId)
            .orElseThrow(() -> new BusinessException(ErrorCode.VILLAGE_NOT_FOUND));
    }
    
    /**
     * 랜덤 마을 선택 (가챠용)
     */
    public VillageDto getRandomVillage(String region, String programType) {
        List<VillageDto> villages = csvReader.getFilteredVillages(region, programType);
        
        if (villages.isEmpty()) {
            throw new BusinessException(ErrorCode.NO_VILLAGES_AVAILABLE);
        }
        
        Random random = new Random();
        return villages.get(random.nextInt(villages.size()));
    }
}
```

### CacheConfig 설정
```java
@Configuration
@EnableCaching
public class CacheConfig {
    
    @Bean
    public CacheManager cacheManager() {
        SimpleCacheManager cacheManager = new SimpleCacheManager();
        cacheManager.setCaches(Arrays.asList(
            new ConcurrentMapCache("villages")
        ));
        return cacheManager;
    }
}
```

### 캐시 무효화 (선택사항)
CSV 파일이 업데이트된 경우 캐시를 무효화하려면:
```java
@Service
@RequiredArgsConstructor
public class VillageService {
    private final VillageCsvReader csvReader;
    private final CacheManager cacheManager;
    
    @CacheEvict(value = "villages", allEntries = true)
    public void refreshCache() {
        log.info("Village cache cleared");
    }
}
```

### CSV 파일 업로드 (선택사항)
런타임에 CSV 파일을 업데이트하고 싶다면:
```java
@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {
    private final VillageCsvReader csvReader;
    private final CacheManager cacheManager;
    
    @PostMapping("/csv/upload")
    public ApiResponse<String> uploadCsv(@RequestParam("file") MultipartFile file) {
        // 파일을 resources/data 폴더에 저장
        // 캐시 무효화
        cacheManager.getCache("villages").clear();
        return ApiResponse.success("CSV 파일이 업데이트되었습니다.");
    }
}
```

### 주의사항
1. **CSV 파일 인코딩**: UTF-8로 저장되어 있는지 확인 (BOM 제거 필요할 수 있음)
2. **컬럼명 확인**: 실제 CSV 파일의 헤더 컬럼명을 확인하여 `getValue()` 메서드 수정
3. **성능**: 캐싱을 사용하여 매번 파일을 읽지 않도록 최적화
4. **메모리**: CSV 파일이 매우 큰 경우 스트리밍 방식 고려
5. **에러 처리**: CSV 파싱 중 오류가 발생해도 다른 레코드는 계속 처리

---

## 개발 체크리스트

### 필수 구현 항목
- [ ] User 엔티티 및 Repository
- [ ] VillageDto 및 VillageCsvReader (CSV 파일 읽기)
- [ ] VillageService (CSV 데이터 조회 서비스)
- [ ] Collection 엔티티 및 Repository
- [ ] Memory 엔티티 및 Repository
- [ ] GachaHistory 엔티티 및 Repository
- [ ] 회원가입 API
- [ ] 로그인 API
- [ ] 가챠 뽑기 API
- [ ] 여행지 조회 API
- [ ] 컬렉션 추가/조회/삭제 API
- [ ] 추억 작성/조회/수정/삭제 API
- [ ] 마이페이지 API
- [ ] 전역 예외 처리
- [ ] CSV 파일을 resources/data 폴더에 저장
- [ ] 캐싱 설정

### 선택 구현 항목
- [ ] JWT 토큰 검증 Interceptor
- [ ] 페이지네이션
- [ ] 검색 기능
- [ ] 통계 API

---

## 주의사항

1. **보안 최소화**: MVP 개발이므로 보안은 최소화 (비밀번호 단순 해시, JWT 간단 구현)
2. **빠른 개발**: 복잡한 로직보다는 빠른 구현 우선
3. **일관성**: 모든 API는 동일한 응답 형식 사용
4. **에러 처리**: 모든 예외는 적절한 에러 메시지와 함께 처리
5. **로깅**: 중요한 로직에는 로그 추가 (`@Slf4j` 사용)

---

## 테스트

### Postman 테스트
1. 환경 변수 설정 (`baseUrl`, `token`)
2. 회원가입 → 로그인 → 토큰 저장
3. 각 API 순차적으로 테스트

### 로컬 테스트
```bash
# 애플리케이션 실행
./mvnw spring-boot:run

# 또는
java -jar target/gacha-travel-service-0.0.1-SNAPSHOT.jar
```

---

## 추가 참고사항

### 기술적 주의사항
- 모든 날짜/시간은 `LocalDateTime`, `LocalDate` 사용
- 시간대는 `Asia/Seoul` 사용
- 데이터베이스 인덱스는 필요시 추가 (username, email 등)
- N+1 문제 주의 (JPA 쿼리 최적화)
- **CSV 파일은 `src/main/resources/data/` 폴더에 저장**
- **CSV 파일은 캐싱되어 성능 최적화됨**
- **CSV 파일의 컬럼명은 실제 파일의 헤더와 일치해야 함**
- **villageId는 CSV 파일의 인덱스 기반 ID 사용 (1부터 시작)**

### 기획적 고려사항

#### 사용자 경험 (UX)
- **가챠의 재미**: 랜덤 추천으로 새로운 여행지 발견의 즐거움 제공
- **수집의 재미**: 컬렉션을 모으는 게임화 요소
- **추억의 가치**: 여행 경험을 기록하고 되돌아볼 수 있는 기능

#### 데이터 활용
- **공공데이터 활용**: 전국농어촌체험휴양마을 표준데이터를 활용하여 실제 여행지 정보 제공
- **지역 균형**: 덜 알려진 지역의 여행지도 동등한 확률로 추천
- **최신 정보**: CSV 파일을 동적으로 읽어 최신 데이터 반영 가능

#### 확장 가능성
- **소셜 기능**: 추억 공유, 다른 사용자의 컬렉션 보기
- **추천 시스템**: 사용자 선호도 기반 맞춤 추천
- **리뷰 시스템**: 여행지에 대한 리뷰 및 평점
- **통계 대시보드**: 지역별 인기 여행지, 방문 통계 등

#### 제약사항 (MVP)
- **보안 최소화**: 빠른 개발을 위해 보안 기능 최소화
- **기본 기능 우선**: 핵심 기능(가챠, 컬렉션, 추억)에 집중
- **단순한 인증**: 복잡한 OAuth 등은 제외하고 기본 로그인만 제공

---

## ERD 다이어그램

프로젝트의 데이터베이스 구조는 `ERD.dbml` 파일을 참고하세요.
[dbdiagram.io](https://dbdiagram.io)에서 시각적으로 확인할 수 있습니다.

---

## 🎯 프로젝트 구현 현황 (2025-11-18 기준)

### ✅ 완료된 Phase (Phase 1~6) - 전체 완료!

#### **Phase 1: 의존성 설정 및 공통 인프라** ✅
**완료 항목:**
- ✅ `build.gradle` 의존성 추가 (Web, JPA, Validation, Cache, JWT, CSV)
- ✅ `application.properties` 설정 (MySQL, JPA, JWT, CSV 경로)
- ✅ 패키지 구조 생성 (domain, dto, controller, service, repository, config, util, exception)
- ✅ `ApiResponse<T>` - 통일된 API 응답 형식
- ✅ `ErrorCode` enum - 에러 코드 관리
- ✅ `BusinessException` - 비즈니스 예외
- ✅ `GlobalExceptionHandler` - 전역 예외 처리 (@ControllerAdvice)
- ✅ `JwtUtil` - JWT 토큰 생성/검증 유틸리티
- ✅ `PasswordUtil` - SHA-256 비밀번호 암호화
- ✅ `CacheConfig` - Spring Cache 설정 (villages 캐시)

**파일 위치:**
- `src/main/java/com/example/gacha/dto/response/ApiResponse.java`
- `src/main/java/com/example/gacha/exception/ErrorCode.java`
- `src/main/java/com/example/gacha/exception/BusinessException.java`
- `src/main/java/com/example/gacha/exception/GlobalExceptionHandler.java`
- `src/main/java/com/example/gacha/util/JwtUtil.java`
- `src/main/java/com/example/gacha/util/PasswordUtil.java`
- `src/main/java/com/example/gacha/config/CacheConfig.java`

---

#### **Phase 2: CSV 데이터 처리** ✅
**완료 항목:**
- ✅ `VillageDto` - CSV 데이터를 담는 DTO (villageId, villageName, sidoName, sigunguName, address, phoneNumber, latitude, longitude, programName, programContent)
- ✅ `VillageCsvReader` - CSV 파일 읽기 및 캐싱 (`@Cacheable`)
  - BOM 제거 처리
  - 필터링 기능 (지역, 프로그램 유형)
  - EUC-KR 인코딩 설정
- ✅ `VillageService` - 비즈니스 로직
  - 전체/필터링 조회 (페이지네이션)
  - ID로 조회
  - 랜덤 선택 (가챠용 핵심 로직)
- ✅ CSV 파일 준비 (`src/main/resources/data/전국농어촌체험휴양마을표준데이터.csv`, 506KB, EUC-KR)

**파일 위치:**
- `src/main/java/com/example/gacha/domain/village/VillageDto.java`
- `src/main/java/com/example/gacha/domain/village/VillageCsvReader.java`
- `src/main/java/com/example/gacha/domain/village/VillageService.java`

**중요 설정:**
```properties
csv.file.path=classpath:data/전국농어촌체험휴양마을표준데이터.csv
csv.file.encoding=EUC-KR
```

---

#### **Phase 3: 인증 시스템** ✅
**완료 항목:**
- ✅ `User` Entity (userId, username, password, email, createdAt)
- ✅ `UserRepository` (findByUsername, existsByUsername, existsByEmail)
- ✅ `SignupRequest` - 회원가입 요청 DTO (Validation 포함)
- ✅ `LoginRequest` - 로그인 요청 DTO
- ✅ `AuthResponse` - 인증 응답 DTO (토큰 + 사용자 정보)
- ✅ `AuthService` - 회원가입, 로그인, 토큰 검증 비즈니스 로직
- ✅ `AuthController` - 인증 API 엔드포인트
  - ✅ `POST /api/auth/signup` - 회원가입
  - ✅ `POST /api/auth/login` - 로그인

**파일 위치:**
- `src/main/java/com/example/gacha/domain/user/User.java`
- `src/main/java/com/example/gacha/domain/user/UserRepository.java`
- `src/main/java/com/example/gacha/dto/request/SignupRequest.java`
- `src/main/java/com/example/gacha/dto/request/LoginRequest.java`
- `src/main/java/com/example/gacha/dto/response/AuthResponse.java`
- `src/main/java/com/example/gacha/service/AuthService.java`
- `src/main/java/com/example/gacha/controller/AuthController.java`

---

#### **Phase 4: 가챠 시스템** ✅ (프로젝트 핵심!)
**완료 항목:**
- ✅ `GachaHistory` Entity (gachaId, userId, villageId, drawnAt)
  - 인덱스: `idx_user_drawn_at`, `idx_user_id`
- ✅ `GachaHistoryRepository`
  - `countByUserIdAndDrawnAtAfter()` - 하루 1회 제한 확인용
  - `findByUserIdAndDrawnAtAfterOrderByDrawnAtDesc()` - 오늘 이력 조회
- ✅ `GachaDrawRequest` - 가챠 요청 DTO (region, programType 필터)
- ✅ `VillageResponse` - 여행지 응답 DTO (isNew, drawnAt 포함)
- ✅ `GachaStatusResponse` - 가챠 상태 응답 DTO (canDraw, remainingCount, lastDrawTime, todayDrawCount)
- ✅ `GachaService` - 가챠 핵심 비즈니스 로직
  - **drawGacha()**: 하루 1회 제한 확인, 랜덤 여행지 선택, 가챠 이력 저장, 처음 뽑은 여행지인지 확인
  - **getGachaStatus()**: 오늘 가챠 가능 여부 확인
- ✅ `GachaController` - 가챠 API 엔드포인트
  - ✅ `POST /api/gacha/draw` - 가챠 뽑기 (JWT 인증, 필터링 옵션)
  - ✅ `GET /api/gacha/status` - 가챠 상태 확인

**파일 위치:**
- `src/main/java/com/example/gacha/domain/gacha/GachaHistory.java`
- `src/main/java/com/example/gacha/domain/gacha/GachaHistoryRepository.java`
- `src/main/java/com/example/gacha/dto/request/GachaDrawRequest.java`
- `src/main/java/com/example/gacha/dto/response/VillageResponse.java`
- `src/main/java/com/example/gacha/dto/response/GachaStatusResponse.java`
- `src/main/java/com/example/gacha/service/GachaService.java`
- `src/main/java/com/example/gacha/controller/GachaController.java`

**핵심 로직:**
```java
// 하루 1회 제한 확인
LocalDateTime todayStart = LocalDateTime.now().withHour(0).withMinute(0).withSecond(0).withNano(0);
long todayCount = gachaHistoryRepository.countByUserIdAndDrawnAtAfter(userId, todayStart);
if (todayCount >= 1) {
    throw new BusinessException(ErrorCode.DAILY_LIMIT_EXCEEDED);
}
```

---

---

#### **Phase 5: 컬렉션 & 추억 시스템** ✅
**완료 항목:**

**Phase 5-1: 컬렉션 시스템 (7개 컴포넌트)**
- ✅ `Collection` Entity - userId, villageId, collectedAt, UNIQUE 제약
- ✅ `CollectionRepository` - findByUserIdOrderByCollectedAtDesc, existsByUserIdAndVillageId, findByCollectionIdAndUserId, countByUserId
- ✅ `CollectionRequest` - villageId 필수 검증
- ✅ `CollectionResponse` - Collection + VillageDto 조합
- ✅ `CollectionStatsResponse` - totalCount, regionStats (Map<String, Long>)
- ✅ `CollectionService` - addCollection, getMyCollections, removeCollection, getCollectionStats (빈 컬렉션 처리 포함)
- ✅ `CollectionController` - 4개 API 엔드포인트
  - ✅ `POST /api/collections` - 컬렉션 추가
  - ✅ `GET /api/collections` - 내 컬렉션 조회 (페이지네이션)
  - ✅ `DELETE /api/collections/{collectionId}` - 컬렉션 제거
  - ✅ `GET /api/collections/stats` - 통계 조회 (라우팅 순서 수정 완료)

**Phase 5-2: 추억 시스템 (6개 컴포넌트)**
- ✅ `Memory` Entity - userId, villageId, content (TEXT), visitDate, createdAt, updatedAt
- ✅ `MemoryRepository` - findByUserIdOrderByCreatedAtDesc, findByMemoryIdAndUserId, countByUserId
- ✅ `MemoryRequest` - content (1~1000자), villageId, visitDate (선택)
- ✅ `MemoryResponse` - Memory + VillageDto 조합
- ✅ `MemoryService` - createMemory, getMyMemories, getMemoryById, updateMemory, deleteMemory (권한 확인 포함)
- ✅ `MemoryController` - 5개 API 엔드포인트
  - ✅ `POST /api/memories` - 추억 작성
  - ✅ `GET /api/memories` - 내 추억 목록 (페이지네이션)
  - ✅ `GET /api/memories/{memoryId}` - 추억 상세 조회
  - ✅ `PUT /api/memories/{memoryId}` - 추억 수정
  - ✅ `DELETE /api/memories/{memoryId}` - 추억 삭제

**파일 위치:**
- `src/main/java/com/example/gacha/domain/collection/`
- `src/main/java/com/example/gacha/domain/memory/`
- `src/main/java/com/example/gacha/dto/request/CollectionRequest.java`
- `src/main/java/com/example/gacha/dto/request/MemoryRequest.java`
- `src/main/java/com/example/gacha/dto/response/CollectionResponse.java`
- `src/main/java/com/example/gacha/dto/response/CollectionStatsResponse.java`
- `src/main/java/com/example/gacha/dto/response/MemoryResponse.java`
- `src/main/java/com/example/gacha/service/CollectionService.java`
- `src/main/java/com/example/gacha/service/MemoryService.java`
- `src/main/java/com/example/gacha/controller/CollectionController.java`
- `src/main/java/com/example/gacha/controller/MemoryController.java`

---

#### **Phase 6: 여행지 & 마이페이지 API** ✅
**완료 항목:**

**Phase 6-1: 여행지 API (1개 컨트롤러, 2개 엔드포인트)**
- ✅ `VillageController` - 여행지 조회 API
  - ✅ `GET /api/villages/{villageId}` - 여행지 상세 조회 (isCollected 포함, JWT 선택)
  - ✅ `GET /api/villages` - 여행지 목록 조회 (페이지네이션, 필터링)
- ✅ `VillageResponse.from(VillageDto, boolean)` - isCollected 지원 오버로드 추가

**Phase 6-2: 마이페이지 API (4개 컴포넌트)**
- ✅ `UserResponse` - userId, username, email, createdAt, collectionCount, memoryCount
  - `from(User)` - 기본 팩토리 메서드
  - `withStats(User, Long, Long)` - 통계 포함 팩토리 메서드
- ✅ `UserUpdateRequest` - email (이메일 형식 검증)
- ✅ `UserService` - getMyInfo, updateMyInfo
- ✅ `UserController` - 2개 API 엔드포인트
  - ✅ `GET /api/users/me` - 내 정보 조회 (통계 포함)
  - ✅ `PUT /api/users/me` - 이메일 수정

**파일 위치:**
- `src/main/java/com/example/gacha/controller/VillageController.java`
- `src/main/java/com/example/gacha/controller/UserController.java`
- `src/main/java/com/example/gacha/dto/request/UserUpdateRequest.java`
- `src/main/java/com/example/gacha/dto/response/UserResponse.java`
- `src/main/java/com/example/gacha/service/UserService.java`

---

### 📋 API 구현 현황 - 전체 완료! (17개 / 17개)

#### ✅ 인증 API (2개)
1. ✅ `POST /api/auth/signup` - 회원가입
2. ✅ `POST /api/auth/login` - 로그인 (JWT 토큰 발급)

#### ✅ 가챠 API (2개) ⭐ 핵심 기능
3. ✅ `POST /api/gacha/draw` - 가챠 뽑기 (하루 1회 제한, 필터링)
4. ✅ `GET /api/gacha/status` - 가챠 상태 확인

#### ✅ 여행지 API (2개)
5. ✅ `GET /api/villages/{villageId}` - 여행지 상세 조회 (컬렉션 여부 포함)
6. ✅ `GET /api/villages` - 여행지 목록 조회 (페이지네이션, 필터링)

#### ✅ 컬렉션 API (4개)
7. ✅ `POST /api/collections` - 컬렉션에 추가
8. ✅ `GET /api/collections` - 내 컬렉션 조회 (페이지네이션)
9. ✅ `DELETE /api/collections/{collectionId}` - 컬렉션 제거
10. ✅ `GET /api/collections/stats` - 컬렉션 통계 (지역별 집계)

#### ✅ 추억 API (5개)
11. ✅ `POST /api/memories` - 추억 작성
12. ✅ `GET /api/memories` - 내 추억 목록 (페이지네이션)
13. ✅ `GET /api/memories/{memoryId}` - 추억 상세 조회
14. ✅ `PUT /api/memories/{memoryId}` - 추억 수정
15. ✅ `DELETE /api/memories/{memoryId}` - 추억 삭제

#### ✅ 마이페이지 API (2개)
16. ✅ `GET /api/users/me` - 내 정보 조회 (통계 포함)
17. ✅ `PUT /api/users/me` - 내 정보 수정 (이메일)

---

### 🚀 다음 단계: Phase 5 - 컬렉션 & 추억 구현

#### Phase 5-1: 컬렉션 시스템
**구현할 내용:**
1. **Collection Entity** 생성
   - `collectionId` (PK)
   - `userId` (FK)
   - `villageId` (CSV ID 참조, 외래키 제약 없음)
   - `collectedAt` (수집 일시)
   - **UNIQUE 제약**: `(user_id, village_id)` - 중복 방지

2. **CollectionRepository** 생성
   - `findByUserIdOrderByCollectedAtDesc()` - 사용자의 컬렉션 조회
   - `existsByUserIdAndVillageId()` - 중복 확인
   - `deleteByCollectionIdAndUserId()` - 권한 확인 후 삭제
   - `countByUserId()` - 사용자의 총 컬렉션 개수

3. **CollectionRequest** DTO
   - `villageId` (필수)

4. **CollectionResponse** DTO
   - `collectionId`, `villageId`, `villageName`, `sidoName`, `sigunguName`, `address`, `collectedAt`
   - VillageDto와 조합하여 응답

5. **CollectionStatsResponse** DTO
   - `totalCount` - 전체 컬렉션 개수
   - `regionStats` - Map<String, Long> 지역별 통계 (예: {"경상남도": 5, "전라북도": 3})

6. **CollectionService** 비즈니스 로직
   - `addCollection()` - 컬렉션 추가 (중복 체크)
   - `getMyCollections()` - 내 컬렉션 조회 (페이지네이션)
   - `removeCollection()` - 컬렉션 제거 (권한 확인)
   - `getCollectionStats()` - 지역별 통계 계산

7. **CollectionController** API
   - `POST /api/collections` - 컬렉션 추가
   - `GET /api/collections` - 내 컬렉션 조회
   - `DELETE /api/collections/{collectionId}` - 컬렉션 제거
   - `GET /api/collections/stats` - 통계 조회

**파일 생성 위치:**
```
src/main/java/com/example/gacha/
├── domain/collection/
│   ├── Collection.java (Entity)
│   ├── CollectionRepository.java
│   └── CollectionService.java
├── dto/request/
│   └── CollectionRequest.java
├── dto/response/
│   ├── CollectionResponse.java
│   └── CollectionStatsResponse.java
└── controller/
    └── CollectionController.java
```

**ERD 참고:**
```sql
CREATE TABLE collections (
    collection_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    village_id BIGINT NOT NULL,
    collected_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE,
    UNIQUE KEY unique_user_village (user_id, village_id)
);
```

---

#### Phase 5-2: 추억 시스템
**구현할 내용:**
1. **Memory Entity** 생성
   - `memoryId` (PK)
   - `userId` (FK)
   - `villageId` (CSV ID 참조)
   - `content` (TEXT, 최대 1000자)
   - `visitDate` (방문 날짜, 선택사항)
   - `createdAt`, `updatedAt`

2. **MemoryRepository** 생성
   - `findByUserIdOrderByCreatedAtDesc()` - 사용자의 추억 목록
   - `findByMemoryIdAndUserId()` - 권한 확인용
   - `countByUserId()` - 사용자의 총 추억 개수

3. **MemoryRequest** DTO (작성/수정용)
   - `villageId` (필수, 작성 시에만)
   - `content` (필수, 1~1000자)
   - `visitDate` (선택, YYYY-MM-DD 형식)

4. **MemoryResponse** DTO
   - `memoryId`, `villageId`, `villageName`, `sidoName`, `sigunguName`, `address`, `content`, `visitDate`, `createdAt`, `updatedAt`

5. **MemoryService** 비즈니스 로직
   - `createMemory()` - 추억 작성 (villageId 유효성 검증)
   - `getMyMemories()` - 내 추억 목록 (페이지네이션)
   - `getMemoryById()` - 추억 상세 조회 (권한 확인)
   - `updateMemory()` - 추억 수정 (권한 확인)
   - `deleteMemory()` - 추억 삭제 (권한 확인)

6. **MemoryController** API
   - `POST /api/memories` - 추억 작성
   - `GET /api/memories` - 내 추억 목록
   - `GET /api/memories/{memoryId}` - 추억 상세
   - `PUT /api/memories/{memoryId}` - 추억 수정
   - `DELETE /api/memories/{memoryId}` - 추억 삭제

**파일 생성 위치:**
```
src/main/java/com/example/gacha/
├── domain/memory/
│   ├── Memory.java (Entity)
│   ├── MemoryRepository.java
│   └── MemoryService.java
├── dto/request/
│   └── MemoryRequest.java
├── dto/response/
│   └── MemoryResponse.java
└── controller/
    └── MemoryController.java
```

**ERD 참고:**
```sql
CREATE TABLE memories (
    memory_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    village_id BIGINT NOT NULL,
    content TEXT NOT NULL,
    visit_date DATE,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE
);
```

---

### 🎯 Phase 6 - 마이페이지 & 여행지 API 구현

#### Phase 6-1: 여행지 API
**구현할 내용:**
1. **VillageController** 생성
   - `GET /api/villages/{villageId}` - 여행지 상세 조회
     - VillageService의 `getVillageById()` 활용
     - 컬렉션 여부 (isCollected) 포함
   - `GET /api/villages` - 여행지 목록 조회
     - 페이지네이션 (`page`, `size`)
     - 필터링 (`region`, `programType`)
     - VillageService의 `getAllVillages()` 활용

**파일 생성 위치:**
```
src/main/java/com/example/gacha/controller/
└── VillageController.java
```

---

#### Phase 6-2: 마이페이지 API
**구현할 내용:**
1. **UserResponse** DTO (마이페이지용)
   - `userId`, `username`, `email`, `createdAt`
   - `collectionCount` - 컬렉션 개수
   - `memoryCount` - 추억 개수

2. **UserUpdateRequest** DTO
   - `email` (선택, 이메일 형식 검증)

3. **UserService** 생성
   - `getMyInfo()` - 내 정보 조회 (통계 포함)
   - `updateMyInfo()` - 내 정보 수정

4. **UserController** 생성
   - `GET /api/users/me` - 내 정보 조회
   - `PUT /api/users/me` - 내 정보 수정

**파일 생성 위치:**
```
src/main/java/com/example/gacha/
├── dto/request/
│   └── UserUpdateRequest.java
├── dto/response/
│   └── UserResponse.java
├── service/
│   └── UserService.java
└── controller/
    └── UserController.java
```

---

### 🔧 구현 시 주의사항

#### 공통 규칙
1. **Entity 클래스**
   - `@Entity`, `@Table`, `@Getter`, `@Setter`, `@NoArgsConstructor`, `@AllArgsConstructor`, `@Builder` 사용
   - `@CreationTimestamp`, `@UpdateTimestamp` 활용
   - `@Id`, `@GeneratedValue(strategy = GenerationType.IDENTITY)`

2. **Repository**
   - `JpaRepository<Entity, ID>` 상속
   - 메서드 네이밍 규칙 준수 (findBy, existsBy, countBy 등)

3. **Service**
   - `@Service`, `@RequiredArgsConstructor`, `@Transactional(readOnly = true)`
   - 쓰기 메서드에만 `@Transactional` 추가
   - `BusinessException`으로 예외 처리

4. **Controller**
   - `@RestController`, `@RequestMapping("/api/...")`
   - JWT 인증: `@RequestHeader("Authorization") String authorization`
   - 모든 응답은 `ApiResponse<T>`로 래핑

#### 권한 확인
- 컬렉션 삭제: `collectionId` + `userId` 확인
- 추억 조회/수정/삭제: `memoryId` + `userId` 확인
- 권한 없으면 `ErrorCode.FORBIDDEN` 발생

#### villageId 검증
- 컬렉션 추가/추억 작성 시 `villageService.getVillageById(villageId)` 호출하여 존재 여부 확인
- 존재하지 않으면 `ErrorCode.VILLAGE_NOT_FOUND` 발생

---

### 📦 최종 프로젝트 구조 - 전체 완료!

```
src/main/java/com/example/gacha/
├── GachaApplication.java
├── config/
│   └── CacheConfig.java ✅
├── domain/
│   ├── user/
│   │   ├── User.java ✅
│   │   └── UserRepository.java ✅
│   ├── village/
│   │   ├── VillageDto.java ✅
│   │   ├── VillageCsvReader.java ✅
│   │   └── VillageService.java ✅
│   ├── gacha/
│   │   ├── GachaHistory.java ✅
│   │   └── GachaHistoryRepository.java ✅
│   ├── collection/
│   │   ├── Collection.java ✅
│   │   └── CollectionRepository.java ✅
│   └── memory/
│       ├── Memory.java ✅
│       └── MemoryRepository.java ✅
├── dto/
│   ├── request/
│   │   ├── SignupRequest.java ✅
│   │   ├── LoginRequest.java ✅
│   │   ├── GachaDrawRequest.java ✅
│   │   ├── CollectionRequest.java ✅
│   │   ├── MemoryRequest.java ✅
│   │   └── UserUpdateRequest.java ✅
│   └── response/
│       ├── ApiResponse.java ✅
│       ├── AuthResponse.java ✅
│       ├── VillageResponse.java ✅
│       ├── GachaStatusResponse.java ✅
│       ├── CollectionResponse.java ✅
│       ├── CollectionStatsResponse.java ✅
│       ├── MemoryResponse.java ✅
│       └── UserResponse.java ✅
├── service/
│   ├── AuthService.java ✅
│   ├── GachaService.java ✅
│   ├── CollectionService.java ✅
│   ├── MemoryService.java ✅
│   └── UserService.java ✅
├── controller/
│   ├── AuthController.java ✅
│   ├── GachaController.java ✅
│   ├── VillageController.java ✅
│   ├── CollectionController.java ✅
│   ├── MemoryController.java ✅
│   └── UserController.java ✅
├── util/
│   ├── JwtUtil.java ✅ (JJWT 0.11.5 최신 API)
│   └── PasswordUtil.java ✅
└── exception/
    ├── ErrorCode.java ✅
    ├── BusinessException.java ✅
    └── GlobalExceptionHandler.java ✅
```

---

### 🧪 테스트 시나리오 (Phase 6 완료 후)

#### 전체 플로우 테스트
1. **회원가입** → `POST /api/auth/signup`
2. **로그인** → `POST /api/auth/login` (토큰 획득)
3. **가챠 뽑기** → `POST /api/gacha/draw` (랜덤 여행지 획득)
4. **컬렉션 추가** → `POST /api/collections`
5. **가챠 상태 확인** → `GET /api/gacha/status` (canDraw: false)
6. **내 컬렉션 조회** → `GET /api/collections`
7. **여행지 상세 조회** → `GET /api/villages/{villageId}`
8. **추억 작성** → `POST /api/memories`
9. **내 추억 조회** → `GET /api/memories`
10. **마이페이지** → `GET /api/users/me` (통계 확인)
11. **컬렉션 통계** → `GET /api/collections/stats` (지역별 통계)
12. **추억 수정** → `PUT /api/memories/{memoryId}`
13. **추억 삭제** → `DELETE /api/memories/{memoryId}`
14. **컬렉션 제거** → `DELETE /api/collections/{collectionId}`

---

### 💡 개발 팁

#### JWT 토큰 사용법
```java
// Controller에서 토큰 추출 및 사용자 ID 획득
String token = jwtUtil.extractToken(authorization);
Long userId = jwtUtil.getUserIdFromToken(token);
```

#### 페이지네이션 처리
```java
// Controller에서 Pageable 받기
@GetMapping
public ApiResponse<Page<CollectionResponse>> getMyCollections(
    @RequestHeader("Authorization") String authorization,
    @RequestParam(defaultValue = "0") int page,
    @RequestParam(defaultValue = "20") int size
) {
    Pageable pageable = PageRequest.of(page, size);
    // ...
}
```

#### VillageDto와 Response DTO 조합
```java
// Service에서 VillageDto를 가져와서 Response DTO에 매핑
VillageDto village = villageService.getVillageById(villageId);
CollectionResponse response = CollectionResponse.builder()
    .collectionId(collection.getCollectionId())
    .villageId(village.getVillageId())
    .villageName(village.getVillageName())
    .sidoName(village.getSidoName())
    .collectedAt(collection.getCollectedAt())
    .build();
```

---

### 📊 진행률 요약 - 프로젝트 100% 완료!

| Phase | 진행률 | 완료 항목 | 상태 |
|-------|--------|----------|------|
| Phase 1 | 100% | 공통 인프라 10개 | ✅ 완료 |
| Phase 2 | 100% | CSV 처리 3개 | ✅ 완료 |
| Phase 3 | 100% | 인증 7개 | ✅ 완료 |
| Phase 4 | 100% | 가챠 7개 | ✅ 완료 |
| Phase 5 | 100% | 컬렉션 7개 + 추억 6개 | ✅ 완료 |
| Phase 6 | 100% | 여행지 2개 + 마이페이지 4개 | ✅ 완료 |
| **전체** | **100%** | **46개 컴포넌트** | ✅ 전체 완료 |

**API 완성도:** 17/17 (100%)

---

### 🎖️ 핵심 성과

1. ✅ **가챠 시스템 완성** - 프로젝트의 핵심 기능! (하루 1회 제한, 필터링)
2. ✅ **컬렉션 시스템** - 중복 방지, 지역별 통계, 페이지네이션
3. ✅ **추억 시스템** - CRUD 완성, 권한 관리, 방문 날짜 기록
4. ✅ **마이페이지** - 사용자 정보 + 통계 (컬렉션 수, 추억 수)
5. ✅ **CSV 기반 데이터** - 공공데이터 활용, 캐싱으로 성능 최적화
6. ✅ **JWT 인증** - JJWT 0.11.5 최신 API 적용, SecretKey 기반
7. ✅ **통일된 응답 형식** - `ApiResponse<T>`로 일관성 유지
8. ✅ **전역 예외 처리** - `@ControllerAdvice`로 깔끔한 에러 핸들링
9. ✅ **경로 라우팅 최적화** - 구체적 경로 우선 매칭

---

### 🔧 주요 기술적 해결 사항

#### 1. JWT 인증 개선 (JJWT 0.11.5 최신 API)
**문제**: 초기 구현에서 deprecated API 사용으로 Base64 디코딩 오류 발생
**해결**:
```java
// 기존 (deprecated)
.signWith(SignatureAlgorithm.HS512, secret)

// 개선 후
private SecretKey getSigningKey() {
    byte[] keyBytes = secret.getBytes(StandardCharsets.UTF_8);
    return Keys.hmacShaKeyFor(keyBytes);
}
.signWith(getSigningKey())
```

#### 2. Spring MVC 경로 매칭 순서 최적화
**문제**: `/api/collections/stats` 호출 시 정적 리소스 오류 발생
**원인**: `@GetMapping("/stats")`가 `@DeleteMapping("/{collectionId}")` 뒤에 선언됨
**해결**: 구체적인 경로를 경로 변수보다 먼저 선언
```java
@GetMapping("/stats")      // 1. 구체적 경로 우선
@GetMapping                 // 2. 기본 경로
@DeleteMapping("/{collectionId}")  // 3. 경로 변수 마지막
```

#### 3. 컬렉션 통계 빈 데이터 처리
**문제**: 컬렉션이 없을 때 Stream groupingBy에서 오류 가능성
**해결**: 빈 컬렉션 명시적 처리
```java
if (collections.isEmpty()) {
    regionStats = new HashMap<>();
    log.info("No collections found, returning empty stats");
} else {
    regionStats = collections.stream()
        .map(collection -> {...})
        .collect(Collectors.groupingBy(...));
}
```

---

**프로젝트 완료!**
전체 17개 API 엔드포인트가 정상 작동하며, 가챠 여행 서비스의 모든 핵심 기능이 구현되었습니다.
Postman 테스트를 통해 API를 확인하고, 프론트엔드와 연동하세요! 🎉

