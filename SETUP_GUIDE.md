# 🚀 가챠 여행 서비스 - 설정 및 실행 가이드

## 📋 목차
1. [빠른 시작](#빠른-시작)
2. [백엔드 설정](#백엔드-설정)
3. [프론트엔드 설정](#프론트엔드-설정)
4. [데이터베이스 설정](#데이터베이스-설정)
5. [테스트](#테스트)
6. [트러블슈팅](#트러블슈팅)

---

## 🎯 빠른 시작

### 1. H2 데이터베이스로 빠른 테스트 (추천)

**백엔드 실행**:
```bash
# 1. 백엔드 디렉토리로 이동
cd c:\gacha

# 2. 빌드
./gradlew clean build -x test

# 3. 실행 (H2 사용)
./gradlew bootRun
```

**프론트엔드 실행**:
```bash
# 1. 프론트엔드 디렉토리로 이동
cd c:\gacha\FE

# 2. 의존성 설치 (최초 1회)
npm install

# 3. 실행
npm start
```

**브라우저 접속**:
- 프론트엔드: http://localhost:3000
- H2 Console: http://localhost:8080/h2-console
- API Swagger: http://localhost:8080/swagger-ui.html (향후 추가 예정)

---

## 🔧 백엔드 설정

### 필수 요구사항
- **JDK**: 21 이상
- **Gradle**: 8.x (포함됨)
- **데이터베이스**: H2 (기본) / MySQL 8.0 (선택)

### 데이터베이스 프로파일

#### 1. H2 Database (기본)
```bash
# 기본 실행 (H2 자동 사용)
./gradlew bootRun

# 또는 명시적으로 H2 프로파일 지정
./gradlew bootRun --args='--spring.profiles.active=h2'
```

**특징**:
- ✅ 설치 불필요
- ✅ 빠른 시작
- ✅ 개발/테스트에 최적
- ⚠️ 재시작 시 데이터 초기화

**H2 Console 접속**:
- URL: http://localhost:8080/h2-console
- JDBC URL: `jdbc:h2:mem:gacha_travel`
- Username: `sa`
- Password: (빈칸)

#### 2. Local MySQL
```bash
# MySQL 데이터베이스 생성
mysql -u root -p -e "CREATE DATABASE gacha_travel CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;"

# Local MySQL 프로파일로 실행
./gradlew bootRun --args='--spring.profiles.active=local'
```

**설정 파일**: `src/main/resources/application-local.properties`
```properties
spring.datasource.username=root
spring.datasource.password=your_password  # 수정 필요
```

#### 3. AWS RDS MySQL
```bash
./gradlew bootRun --args='--spring.profiles.active=rds'
```

**설정 파일**: `src/main/resources/application-rds.properties`

---

## 🎨 프론트엔드 설정

### 필수 요구사항
- **Node.js**: 18.x 이상
- **npm**: 9.x 이상

### 환경 변수 설정

#### 1. 실제 백엔드 사용 (기본)
```bash
# .env 파일 (이미 생성됨)
REACT_APP_API_URL=http://localhost:8080/api
REACT_APP_USE_MOCK=false
```

#### 2. Mock API 사용 (백엔드 없이 개발)
```bash
# .env.mock 파일로 변경
cp .env.mock .env

# 또는 직접 수정
REACT_APP_USE_MOCK=true
```

### 실행 방법

```bash
# 1. 의존성 설치 (최초 1회)
cd FE
npm install

# 2. 개발 서버 실행
npm start

# 3. 프로덕션 빌드
npm run build
```

### 주요 페이지
- `/` - 랜딩 페이지
- `/auth` - 로그인/회원가입
- `/app/gacha` - 가챠 뽑기
- `/app/village` - 마을 목록
- `/app/village/save` - 내 컬렉션
- `/app/memory` - 내 추억
- `/app/mypage` - 마이페이지

---

## 💾 데이터베이스 설정

자세한 내용은 [DATABASE_SETUP.md](DATABASE_SETUP.md) 참고

### 프로파일 비교

| 프로파일 | 데이터베이스 | 설치 | 데이터 영속성 | 용도 |
|---------|------------|------|-------------|------|
| `h2` | H2 (인메모리) | 불필요 | ❌ 재시작 시 삭제 | 개발/테스트 |
| `local` | Local MySQL | 필요 | ✅ 영구 저장 | 로컬 개발 |
| `rds` | AWS RDS | AWS 설정 | ✅ 영구 저장 | 프로덕션 |

---

## 🧪 테스트

### API 테스트 (curl)

#### 1. 회원가입
```bash
curl -X POST http://localhost:8080/api/auth/signup \
  -H "Content-Type: application/json" \
  -d '{
    "username": "testuser",
    "password": "test1234",
    "email": "test@example.com"
  }'
```

#### 2. 로그인
```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "username": "testuser",
    "password": "test1234"
  }'
```

#### 3. 가챠 상태 확인
```bash
TOKEN="your_jwt_token_here"
curl -X GET http://localhost:8080/api/gacha/status \
  -H "Authorization: Bearer $TOKEN"
```

#### 4. 가챠 뽑기
```bash
curl -X POST http://localhost:8080/api/gacha/draw \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{}'
```

#### 5. 마을 목록 조회
```bash
curl -X GET "http://localhost:8080/api/villages?page=0&size=20"
```

### 전체 통합 테스트 시나리오

```bash
# 1. 백엔드 실행 확인
curl http://localhost:8080/actuator/health

# 2. 회원가입
curl -X POST http://localhost:8080/api/auth/signup \
  -H "Content-Type: application/json" \
  -d '{"username":"user1","password":"pass1234","email":"user1@test.com"}'

# 3. 로그인 및 토큰 획득
TOKEN=$(curl -s -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"user1","password":"pass1234"}' \
  | jq -r '.data.token')

echo "Token: $TOKEN"

# 4. 가챠 뽑기
curl -X POST http://localhost:8080/api/gacha/draw \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{}' | jq

# 5. 내 정보 조회
curl -X GET http://localhost:8080/api/users/me \
  -H "Authorization: Bearer $TOKEN" | jq
```

---

## 🔍 트러블슈팅

### 백엔드 문제

#### 1. Port 8080 already in use
```bash
# Windows
netstat -ano | findstr :8080
taskkill /PID <PID> /F

# Linux/Mac
lsof -ti:8080 | xargs kill -9
```

#### 2. Gradle 빌드 실패
```bash
# Gradle 캐시 클리어
./gradlew clean --refresh-dependencies

# 또는 Gradle Wrapper 재다운로드
./gradlew wrapper --gradle-version 8.11.1
```

#### 3. H2 Console 접속 안 됨
```properties
# application.properties 확인
spring.h2.console.enabled=true
spring.h2.console.path=/h2-console
```

#### 4. CSV 파일 읽기 오류
```
에러: CSV 파일을 찾을 수 없습니다.
해결: src/main/resources/data/ 폴더에 CSV 파일 존재 확인
```

### 프론트엔드 문제

#### 1. CORS 에러
```
Access to XMLHttpRequest has been blocked by CORS policy
```
**해결**: 백엔드가 실행 중인지 확인 (WebConfig에 CORS 설정 완료됨)

#### 2. 환경 변수 로드 안 됨
```bash
# .env 파일 위치 확인
ls -la FE/.env

# React 앱 재시작 필수
npm start
```

#### 3. API 호출 실패
```javascript
// 개발자 도구 콘솔에서 확인
console.log(process.env.REACT_APP_API_URL)
// 출력: http://localhost:8080/api

// Mock 모드 확인
console.log(process.env.REACT_APP_USE_MOCK)
// 출력: false
```

#### 4. npm install 오류
```bash
# node_modules 삭제 후 재설치
rm -rf node_modules package-lock.json
npm install
```

---

## 📝 추가 문서

- [데이터베이스 설정 상세 가이드](DATABASE_SETUP.md)
- [API 명세서](API_SPECIFICATION.md)
- [API 엔드포인트 매핑](API_ENDPOINTS.md)
- [백엔드 개발 가이드](claude.md)
- [Postman 테스트 가이드](postman.md)

---

## 🎯 개발 워크플로우 추천

### 1. 신규 기능 개발
```
H2 Database → 빠른 프로토타입 개발 및 테스트
```

### 2. 로컬 통합 테스트
```
Local MySQL → 데이터 영속성 및 통합 테스트
```

### 3. 배포 전 검증
```
AWS RDS → 프로덕션 환경 시뮬레이션
```

---

## 🚀 프로덕션 배포

### 백엔드 (JAR 파일)
```bash
# 1. 빌드
./gradlew clean build

# 2. JAR 파일 확인
ls build/libs/gacha-*.jar

# 3. 실행 (프로파일 지정)
java -jar build/libs/gacha-0.0.1-SNAPSHOT.jar --spring.profiles.active=rds
```

### 프론트엔드 (정적 파일)
```bash
# 1. 프로덕션 빌드
cd FE
npm run build

# 2. build 폴더 배포
# build/ 폴더를 웹 서버(Nginx, Apache 등)에 배포
```

---

## 📞 지원

문제가 발생하면 다음을 확인하세요:
1. 백엔드 로그: `./gradlew bootRun` 출력 확인
2. 프론트엔드 콘솔: 브라우저 개발자 도구 (F12)
3. H2 Console: http://localhost:8080/h2-console
4. API 응답: curl 또는 Postman으로 직접 테스트

---

## ✅ 체크리스트

### 백엔드 실행 전
- [ ] JDK 21 설치 확인: `java -version`
- [ ] Gradle 확인: `./gradlew -version`
- [ ] Port 8080 사용 가능 확인

### 프론트엔드 실행 전
- [ ] Node.js 설치 확인: `node -v`
- [ ] npm 설치 확인: `npm -v`
- [ ] .env 파일 존재 확인
- [ ] Port 3000 사용 가능 확인

### 통합 테스트 전
- [ ] 백엔드 정상 실행: http://localhost:8080
- [ ] 프론트엔드 정상 실행: http://localhost:3000
- [ ] 브라우저 콘솔에 CORS 에러 없음
- [ ] 회원가입/로그인 테스트 성공

---

**Happy Coding! 🎉**
