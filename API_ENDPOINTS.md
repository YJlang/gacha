# 🔗 API 엔드포인트 매핑 가이드

프론트엔드와 백엔드 간의 API 엔드포인트 매핑 문서입니다.

---

## 📌 Base URL 설정

### 프론트엔드 (React)
- **개발 환경**: `http://localhost:8080/api`
- **설정 파일**: `FE/.env`
- **환경 변수**: `REACT_APP_API_URL`

### 백엔드 (Spring Boot)
- **서버 포트**: `8080`
- **Base Path**: `/api`
- **설정 파일**: `src/main/resources/application.properties`

---

## 🔐 인증 (Authentication)

### 회원가입
- **Method**: `POST`
- **Frontend**: `/auth/signup`
- **Backend**: `/api/auth/signup`
- **Request Body**:
  ```json
  {
    "username": "string",
    "password": "string",
    "email": "string"
  }
  ```
- **Response**:
  ```json
  {
    "success": true,
    "message": "회원가입이 완료되었습니다.",
    "data": {
      "userId": 1,
      "username": "user123",
      "email": "user@example.com",
      "token": "eyJhbGciOiJIUzUxMiJ9..."
    }
  }
  ```

### 로그인
- **Method**: `POST`
- **Frontend**: `/auth/login`
- **Backend**: `/api/auth/login`
- **Request Body**:
  ```json
  {
    "username": "string",
    "password": "string"
  }
  ```
- **Response**:
  ```json
  {
    "success": true,
    "message": "로그인 성공",
    "data": {
      "userId": 1,
      "username": "user123",
      "email": "user@example.com",
      "token": "eyJhbGciOiJIUzUxMiJ9..."
    }
  }
  ```

---

## 🎰 가챠 (Gacha)

### 가챠 상태 확인
- **Method**: `GET`
- **Frontend**: `/gacha/status`
- **Backend**: `/api/gacha/status`
- **Headers**: `Authorization: Bearer {token}`
- **Response**:
  ```json
  {
    "success": true,
    "data": {
      "canDraw": true,
      "remainingCount": 1,
      "lastDrawTime": null,
      "todayDrawCount": 0
    }
  }
  ```

### 가챠 뽑기
- **Method**: `POST`
- **Frontend**: `/gacha/draw`
- **Backend**: `/api/gacha/draw`
- **Headers**: `Authorization: Bearer {token}`
- **Request Body** (선택사항):
  ```json
  {
    "region": "경기도",
    "programType": "농작물경작체험"
  }
  ```
- **Response**:
  ```json
  {
    "success": true,
    "message": "가챠 뽑기 성공",
    "data": {
      "villageId": 1,
      "villageName": "행복마을",
      "sidoName": "경기도",
      "sigunguName": "가평군",
      "address": "경기도 가평군 ...",
      "phoneNumber": "031-123-4567",
      "latitude": 37.831,
      "longitude": 127.509,
      "programName": "농작물경작체험",
      "programContent": "감자 캐기, 고구마 심기 등",
      "isNew": true,
      "drawnAt": "2025-11-21T14:30:00"
    }
  }
  ```

---

## 🗺️ 여행지 (Villages)

### 여행지 목록 조회
- **Method**: `GET`
- **Frontend**: `/villages?page=0&size=20&region=경기도&programType=농작물경작체험`
- **Backend**: `/api/villages?page=0&size=20&region=경기도&programType=농작물경작체험`
- **Query Parameters**:
  - `page`: 페이지 번호 (0부터 시작)
  - `size`: 페이지 크기 (기본값: 20)
  - `region`: 지역 필터 (선택사항)
  - `programType`: 프로그램 유형 필터 (선택사항)
- **Response**:
  ```json
  {
    "success": true,
    "data": {
      "content": [
        {
          "villageId": 1,
          "villageName": "행복마을",
          "sidoName": "경기도",
          "sigunguName": "가평군",
          "address": "경기도 가평군 ...",
          "phoneNumber": "031-123-4567",
          "latitude": 37.831,
          "longitude": 127.509,
          "programName": "농작물경작체험",
          "programContent": "감자 캐기, 고구마 심기 등"
        }
      ],
      "totalPages": 5,
      "totalElements": 100,
      "currentPage": 0,
      "size": 20
    }
  }
  ```

### 여행지 상세 조회
- **Method**: `GET`
- **Frontend**: `/villages/{villageId}`
- **Backend**: `/api/villages/{villageId}`
- **Response**:
  ```json
  {
    "success": true,
    "data": {
      "villageId": 1,
      "villageName": "행복마을",
      "sidoName": "경기도",
      "sigunguName": "가평군",
      "address": "경기도 가평군 ...",
      "phoneNumber": "031-123-4567",
      "latitude": 37.831,
      "longitude": 127.509,
      "programName": "농작물경작체험",
      "programContent": "감자 캐기, 고구마 심기 등"
    }
  }
  ```

---

## 📚 컬렉션 (Collections)

### 컬렉션 추가
- **Method**: `POST`
- **Frontend**: `/collections`
- **Backend**: `/api/collections`
- **Headers**: `Authorization: Bearer {token}`
- **Request Body**:
  ```json
  {
    "villageId": 1
  }
  ```
- **Response**:
  ```json
  {
    "success": true,
    "message": "컬렉션에 추가되었습니다.",
    "data": {
      "collectionId": 1,
      "villageId": 1,
      "villageName": "행복마을",
      "sidoName": "경기도",
      "collectedAt": "2025-11-21T14:30:00"
    }
  }
  ```

### 내 컬렉션 조회
- **Method**: `GET`
- **Frontend**: `/collections?page=0&size=20`
- **Backend**: `/api/collections?page=0&size=20`
- **Headers**: `Authorization: Bearer {token}`
- **Response**:
  ```json
  {
    "success": true,
    "data": {
      "content": [
        {
          "collectionId": 1,
          "villageId": 1,
          "villageName": "행복마을",
          "sidoName": "경기도",
          "collectedAt": "2025-11-21T14:30:00"
        }
      ],
      "totalPages": 1,
      "totalElements": 5,
      "currentPage": 0,
      "size": 20
    }
  }
  ```

### 컬렉션 통계
- **Method**: `GET`
- **Frontend**: `/collections/stats`
- **Backend**: `/api/collections/stats`
- **Headers**: `Authorization: Bearer {token}`
- **Response**:
  ```json
  {
    "success": true,
    "data": {
      "totalCount": 15,
      "regionStats": {
        "경기도": 5,
        "강원도": 3,
        "충청남도": 7
      }
    }
  }
  ```

### 컬렉션 삭제
- **Method**: `DELETE`
- **Frontend**: `/collections/{collectionId}`
- **Backend**: `/api/collections/{collectionId}`
- **Headers**: `Authorization: Bearer {token}`
- **Response**:
  ```json
  {
    "success": true,
    "message": "컬렉션에서 삭제되었습니다."
  }
  ```

---

## 📝 추억 (Memories)

### 추억 작성
- **Method**: `POST`
- **Frontend**: `/memories`
- **Backend**: `/api/memories`
- **Headers**: `Authorization: Bearer {token}`
- **Request Body**:
  ```json
  {
    "villageId": 1,
    "content": "정말 즐거운 여행이었습니다!",
    "visitDate": "2025-11-20"
  }
  ```
- **Response**:
  ```json
  {
    "success": true,
    "message": "추억이 작성되었습니다.",
    "data": {
      "memoryId": 1,
      "villageId": 1,
      "villageName": "행복마을",
      "content": "정말 즐거운 여행이었습니다!",
      "visitDate": "2025-11-20",
      "createdAt": "2025-11-21T14:30:00"
    }
  }
  ```

### 내 추억 목록 조회
- **Method**: `GET`
- **Frontend**: `/memories?page=0&size=20`
- **Backend**: `/api/memories?page=0&size=20`
- **Headers**: `Authorization: Bearer {token}`
- **Response**:
  ```json
  {
    "success": true,
    "data": {
      "content": [
        {
          "memoryId": 1,
          "villageId": 1,
          "villageName": "행복마을",
          "content": "정말 즐거운 여행이었습니다!",
          "visitDate": "2025-11-20",
          "createdAt": "2025-11-21T14:30:00",
          "updatedAt": "2025-11-21T14:30:00"
        }
      ],
      "totalPages": 1,
      "totalElements": 3,
      "currentPage": 0,
      "size": 20
    }
  }
  ```

### 추억 상세 조회
- **Method**: `GET`
- **Frontend**: `/memories/{memoryId}`
- **Backend**: `/api/memories/{memoryId}`
- **Headers**: `Authorization: Bearer {token}`
- **Response**:
  ```json
  {
    "success": true,
    "data": {
      "memoryId": 1,
      "villageId": 1,
      "villageName": "행복마을",
      "content": "정말 즐거운 여행이었습니다!",
      "visitDate": "2025-11-20",
      "createdAt": "2025-11-21T14:30:00",
      "updatedAt": "2025-11-21T14:30:00"
    }
  }
  ```

### 추억 수정
- **Method**: `PUT`
- **Frontend**: `/memories/{memoryId}`
- **Backend**: `/api/memories/{memoryId}`
- **Headers**: `Authorization: Bearer {token}`
- **Request Body**:
  ```json
  {
    "content": "수정된 내용입니다.",
    "visitDate": "2025-11-20"
  }
  ```
- **Response**:
  ```json
  {
    "success": true,
    "message": "추억이 수정되었습니다.",
    "data": {
      "memoryId": 1,
      "villageId": 1,
      "villageName": "행복마을",
      "content": "수정된 내용입니다.",
      "visitDate": "2025-11-20",
      "createdAt": "2025-11-21T14:30:00",
      "updatedAt": "2025-11-21T15:00:00"
    }
  }
  ```

### 추억 삭제
- **Method**: `DELETE`
- **Frontend**: `/memories/{memoryId}`
- **Backend**: `/api/memories/{memoryId}`
- **Headers**: `Authorization: Bearer {token}`
- **Response**:
  ```json
  {
    "success": true,
    "message": "추억이 삭제되었습니다."
  }
  ```

---

## 👤 사용자 (Users)

### 내 정보 조회
- **Method**: `GET`
- **Frontend**: `/users/me` ✅ (수정됨: `/user/me` → `/users/me`)
- **Backend**: `/api/users/me`
- **Headers**: `Authorization: Bearer {token}`
- **Response**:
  ```json
  {
    "success": true,
    "data": {
      "userId": 1,
      "username": "user123",
      "email": "user@example.com",
      "createdAt": "2025-11-21T10:00:00",
      "collectionCount": 15,
      "memoryCount": 8
    }
  }
  ```

### 내 정보 수정 (이메일)
- **Method**: `PUT`
- **Frontend**: `/users/me` ✅ (수정됨: `/user/me` → `/users/me`)
- **Backend**: `/api/users/me`
- **Headers**: `Authorization: Bearer {token}`
- **Request Body**:
  ```json
  {
    "email": "newemail@example.com"
  }
  ```
- **Response**:
  ```json
  {
    "success": true,
    "message": "이메일이 수정되었습니다.",
    "data": {
      "userId": 1,
      "username": "user123",
      "email": "newemail@example.com",
      "createdAt": "2025-11-21T10:00:00",
      "collectionCount": 15,
      "memoryCount": 8
    }
  }
  ```

---

## 🔧 CORS 설정

### 백엔드 (Spring Boot)
- **파일**: `src/main/java/com/example/gacha/config/WebConfig.java`
- **허용 오리진**:
  - `http://localhost:3000`
  - `http://127.0.0.1:3000`
- **허용 메서드**: `GET`, `POST`, `PUT`, `DELETE`, `OPTIONS`
- **허용 헤더**: 모두 허용
- **Credentials**: 허용

---

## 📦 환경 변수 설정

### 프론트엔드 (.env)
```bash
# 실제 API 사용
REACT_APP_API_URL=http://localhost:8080/api
REACT_APP_USE_MOCK=false
```

### 프론트엔드 (.env.mock)
```bash
# Mock API 사용 (백엔드 없이 개발)
REACT_APP_API_URL=http://localhost:8080/api
REACT_APP_USE_MOCK=true
```

### 백엔드 (application.properties)
```properties
server.port=8080

# CORS 설정은 WebConfig.java에서 관리
```

---

## ✅ 수정된 항목

1. **MyPage.jsx** ([MyPage.jsx:17](FE/src/pages/MyPage.jsx#L17))
   - ❌ 이전: `client.get('/user/me')`
   - ✅ 수정: `client.get('/users/me')`

2. **MyPage.jsx** ([MyPage.jsx:36](FE/src/pages/MyPage.jsx#L36))
   - ❌ 이전: `client.put('/user/me', ...)`
   - ✅ 수정: `client.put('/users/me', ...)`

3. **MyPage.jsx** ([MyPage.jsx:19](FE/src/pages/MyPage.jsx#L19))
   - ❌ 이전: `res.date.success` (오타)
   - ✅ 수정: `res.data.success`

4. **CORS 설정 추가**
   - ✅ 새로 생성: `src/main/java/com/example/gacha/config/WebConfig.java`

5. **환경 변수 파일 생성**
   - ✅ 새로 생성: `FE/.env`
   - ✅ 새로 생성: `FE/.env.example`
   - ✅ 새로 생성: `FE/.env.development`
   - ✅ 새로 생성: `FE/.env.mock`

---

## 🚀 테스트 방법

### 1. 백엔드 실행
```bash
cd c:\gacha
./gradlew bootRun
# 또는
gradle bootRun
```

### 2. 프론트엔드 실행 (실제 API 사용)
```bash
cd FE
npm install
npm start
```

### 3. 프론트엔드 실행 (Mock API 사용)
```bash
cd FE
npm install
# .env 파일에서 REACT_APP_USE_MOCK=true 설정
# 또는
REACT_APP_USE_MOCK=true npm start
```

---

## 📝 주의사항

1. **JWT 토큰**
   - 모든 인증 필요 API는 `Authorization: Bearer {token}` 헤더 필요
   - 토큰은 로그인/회원가입 시 응답으로 받음
   - 프론트엔드는 `localStorage`에 `jwt_token` 키로 저장

2. **페이지네이션**
   - 페이지 번호는 0부터 시작
   - 기본 페이지 크기: 20

3. **에러 응답**
   ```json
   {
     "success": false,
     "message": "에러 메시지",
     "error": "ERROR_CODE"
   }
   ```

4. **날짜 형식**
   - ISO 8601 형식 사용: `2025-11-21T14:30:00`
   - 타임존: `Asia/Seoul`

---

## 🔗 관련 문서

- [백엔드 개발 가이드](claude.md)
- [API 명세서](API_SPECIFICATION.md)
- [Postman 테스트 가이드](postman.md)
