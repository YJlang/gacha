# 데이터베이스 설정 가이드

## 🎯 지원하는 데이터베이스

1. **H2 Database** (기본) - 테스트 및 빠른 개발용
2. **Local MySQL** - 로컬 개발용
3. **AWS RDS MySQL** - 프로덕션용

---

## 🚀 빠른 시작

### 1. H2 Database (기본 설정)

**장점**: 설치 불필요, 빠른 시작, 인메모리

**실행 방법**:
```bash
# 기본 실행 (H2 자동 사용)
./gradlew bootRun

# 또는 명시적으로 H2 프로파일 지정
./gradlew bootRun --args='--spring.profiles.active=h2'
```

**H2 Console 접속**:
- URL: http://localhost:8080/h2-console
- JDBC URL: `jdbc:h2:mem:gacha_travel`
- Username: `sa`
- Password: (빈칸)

**주의**: 애플리케이션 재시작 시 데이터 초기화됨

---

### 2. Local MySQL

**사전 요구사항**: MySQL 8.0 이상 설치

**MySQL 데이터베이스 생성**:
```sql
-- MySQL 접속
mysql -u root -p

-- 데이터베이스 생성
CREATE DATABASE gacha_travel CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

-- 사용자 생성 (선택사항)
CREATE USER 'gacha_user'@'localhost' IDENTIFIED BY 'your_password';
GRANT ALL PRIVILEGES ON gacha_travel.* TO 'gacha_user'@'localhost';
FLUSH PRIVILEGES;
```

**설정 파일 수정** (필요시):
```properties
# src/main/resources/application-local.properties
spring.datasource.username=root
spring.datasource.password=your_password  # 비밀번호 변경
```

**실행 방법**:
```bash
# Local MySQL 프로파일로 실행
./gradlew bootRun --args='--spring.profiles.active=local'
```

---

### 3. AWS RDS MySQL

**사전 요구사항**: AWS RDS MySQL 인스턴스 생성 완료

**설정 파일 수정**:
```properties
# src/main/resources/application-rds.properties
spring.datasource.url=jdbc:mysql://your-rds-endpoint:3306/gacha_travel?...
spring.datasource.username=your_username
spring.datasource.password=your_password
```

**실행 방법**:
```bash
# RDS 프로파일로 실행
./gradlew bootRun --args='--spring.profiles.active=rds'
```

---

## 📋 프로파일별 설정 요약

| 프로파일 | 데이터베이스 | 설정 파일 | 용도 |
|---------|------------|----------|------|
| `h2` | H2 (인메모리) | application.properties (기본) | 테스트, 빠른 개발 |
| `local` | Local MySQL | application-local.properties | 로컬 개발 |
| `rds` | AWS RDS MySQL | application-rds.properties | 프로덕션 |

---

## 🔧 IDE에서 프로파일 설정

### IntelliJ IDEA
1. Run → Edit Configurations
2. Active profiles에 `h2`, `local`, 또는 `rds` 입력
3. Apply → OK

### VS Code (Spring Boot Extension)
1. `.vscode/launch.json` 수정:
```json
{
  "configurations": [
    {
      "type": "java",
      "name": "Spring Boot (H2)",
      "request": "launch",
      "mainClass": "com.example.gacha.GachaApplication",
      "args": "--spring.profiles.active=h2"
    },
    {
      "type": "java",
      "name": "Spring Boot (Local MySQL)",
      "request": "launch",
      "mainClass": "com.example.gacha.GachaApplication",
      "args": "--spring.profiles.active=local"
    }
  ]
}
```

---

## 🧪 테스트 시나리오

### H2 Database로 빠른 테스트
```bash
# 1. 백엔드 실행 (H2)
./gradlew bootRun

# 2. 회원가입 테스트
curl -X POST http://localhost:8080/api/auth/signup \
  -H "Content-Type: application/json" \
  -d '{"username":"testuser","password":"test1234","email":"test@example.com"}'

# 3. H2 Console에서 데이터 확인
# http://localhost:8080/h2-console
```

### Local MySQL로 영구 데이터 테스트
```bash
# 1. MySQL 데이터베이스 생성
mysql -u root -p -e "CREATE DATABASE gacha_travel;"

# 2. 백엔드 실행 (Local MySQL)
./gradlew bootRun --args='--spring.profiles.active=local'

# 3. 데이터가 영구 저장됨
```

---

## 🛠️ 트러블슈팅

### 문제: H2 Console 접속 안 됨
**해결**:
```properties
# application.properties 확인
spring.h2.console.enabled=true
spring.h2.console.path=/h2-console
```

### 문제: Local MySQL 연결 실패
**해결**:
```bash
# MySQL 서비스 확인
# Windows
net start MySQL80

# macOS/Linux
sudo systemctl start mysql

# 데이터베이스 존재 확인
mysql -u root -p -e "SHOW DATABASES;"
```

### 문제: RDS 연결 타임아웃
**해결**:
1. Security Group 인바운드 규칙 확인 (3306 포트 허용)
2. VPC/서브넷 설정 확인
3. RDS 엔드포인트 정확한지 확인

---

## 📊 데이터베이스 스키마

애플리케이션 실행 시 자동으로 생성되는 테이블:
- `users` - 사용자 정보
- `collections` - 여행지 컬렉션
- `memories` - 여행 추억
- `gacha_history` - 가챠 뽑기 이력

---

## 💡 추천 개발 워크플로우

1. **초기 개발**: H2 사용 (빠른 반복)
2. **로컬 테스트**: Local MySQL 사용 (데이터 영속성 확인)
3. **배포 전 테스트**: RDS 사용 (프로덕션 환경 확인)

---

## 🔐 보안 주의사항

**중요**: 프로덕션 환경에서는 절대 기본 비밀번호 사용 금지!

```properties
# ❌ 나쁜 예
spring.datasource.password=root

# ✅ 좋은 예
spring.datasource.password=${DB_PASSWORD}  # 환경 변수 사용
```

---

## 📝 참고

- H2 Database 공식 문서: https://www.h2database.com
- MySQL 다운로드: https://dev.mysql.com/downloads/
- Spring Boot Profiles: https://docs.spring.io/spring-boot/reference/features/profiles.html
