# 가챠 여행 서비스 API 명세서

## 기본 정보
- **Base URL**: `http://localhost:8080/api`
- **Content-Type**: `application/json`
- **인증 방식**: JWT 토큰 (Authorization 헤더에 Bearer 토큰 포함)

---

## 1. 인증 API

### 1.1 회원가입
**POST** `/auth/signup`

**Request Body:**
```json
{
  "username": "string (필수, 최소 3자)",
  "password": "string (필수, 최소 6자)",
  "email": "string (필수, 이메일 형식)"
}
```

**Response 201 Created:**
```json
{
  "success": true,
  "message": "회원가입이 완료되었습니다.",
  "data": {
    "userId": 1,
    "username": "traveler123",
    "email": "traveler@example.com"
  }
}
```

**Response 400 Bad Request:**
```json
{
  "success": false,
  "message": "이미 존재하는 아이디입니다.",
  "error": "USERNAME_ALREADY_EXISTS"
}
```

---

### 1.2 로그인
**POST** `/auth/login`

**Request Body:**
```json
{
  "username": "string (필수)",
  "password": "string (필수)"
}
```

**Response 200 OK:**
```json
{
  "success": true,
  "message": "로그인 성공",
  "data": {
    "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
    "user": {
      "userId": 1,
      "username": "traveler123",
      "email": "traveler@example.com"
    }
  }
}
```

**Response 401 Unauthorized:**
```json
{
  "success": false,
  "message": "아이디 또는 비밀번호가 올바르지 않습니다.",
  "error": "INVALID_CREDENTIALS"
}
```

---

## 2. 가챠 API

### 2.1 랜덤 여행지 뽑기 (가챠)
**POST** `/gacha/draw`

**Headers:**
```
Authorization: Bearer {token}
```

**Request Body (선택사항):**
```json
{
  "region": "string (선택, 예: '전라북도', '경상남도')",
  "programType": "string (선택, 예: '체험프로그램', '숙박')"
}
```

**Response 200 OK:**
```json
{
  "success": true,
  "message": "가챠 뽑기 성공",
  "data": {
    "villageId": 123,
    "villageName": "산청마을",
    "sidoName": "경상남도",
    "sigunguName": "산청군",
    "address": "경상남도 산청군 산청읍 산청리 123",
    "phoneNumber": "055-123-4567",
    "latitude": 35.415,
    "longitude": 127.875,
    "programName": "체험프로그램명",
    "programContent": "체험프로그램 상세 내용...",
    "isNew": true,
    "drawnAt": "2025-01-15T10:30:00"
  }
}
```

**Response 429 Too Many Requests:**
```json
{
  "success": false,
  "message": "오늘 가챠 횟수를 모두 사용했습니다. 내일 다시 시도해주세요.",
  "error": "DAILY_LIMIT_EXCEEDED"
}
```

---

### 2.2 오늘 가챠 가능 여부 확인
**GET** `/gacha/status`

**Headers:**
```
Authorization: Bearer {token}
```

**Response 200 OK:**
```json
{
  "success": true,
  "data": {
    "canDraw": true,
    "remainingCount": 1,
    "lastDrawTime": "2025-01-15T09:00:00"
  }
}
```

---

## 3. 여행지 API

### 3.1 여행지 상세 조회
**GET** `/villages/{villageId}`

**Headers:**
```
Authorization: Bearer {token}
```

**Response 200 OK:**
```json
{
  "success": true,
  "data": {
    "villageId": 123,
    "villageName": "산청마을",
    "sidoName": "경상남도",
    "sigunguName": "산청군",
    "address": "경상남도 산청군 산청읍 산청리 123",
    "phoneNumber": "055-123-4567",
    "latitude": 35.415,
    "longitude": 127.875,
    "programName": "체험프로그램명",
    "programContent": "체험프로그램 상세 내용...",
    "isCollected": false,
    "collectedAt": null
  }
}
```

**Response 404 Not Found:**
```json
{
  "success": false,
  "message": "여행지를 찾을 수 없습니다.",
  "error": "VILLAGE_NOT_FOUND"
}
```

---

### 3.2 여행지 목록 조회 (필터링)
**GET** `/villages?region={region}&programType={type}&page={page}&size={size}`

**Query Parameters:**
- `region` (선택): 시도명
- `programType` (선택): 프로그램 유형
- `page` (선택, 기본값: 0): 페이지 번호
- `size` (선택, 기본값: 20): 페이지 크기

**Headers:**
```
Authorization: Bearer {token}
```

**Response 200 OK:**
```json
{
  "success": true,
  "data": {
    "content": [
      {
        "villageId": 123,
        "villageName": "산청마을",
        "sidoName": "경상남도",
        "sigunguName": "산청군",
        "address": "경상남도 산청군 산청읍 산청리 123",
        "isCollected": false
      }
    ],
    "totalElements": 100,
    "totalPages": 5,
    "currentPage": 0,
    "size": 20
  }
}
```

---

## 4. 컬렉션 API

### 4.1 내 컬렉션 조회
**GET** `/collections`

**Headers:**
```
Authorization: Bearer {token}
```

**Query Parameters:**
- `page` (선택, 기본값: 0): 페이지 번호
- `size` (선택, 기본값: 20): 페이지 크기

**Response 200 OK:**
```json
{
  "success": true,
  "data": {
    "content": [
      {
        "collectionId": 1,
        "villageId": 123,
        "villageName": "산청마을",
        "sidoName": "경상남도",
        "sigunguName": "산청군",
        "address": "경상남도 산청군 산청읍 산청리 123",
        "collectedAt": "2025-01-15T10:30:00"
      }
    ],
    "totalElements": 15,
    "totalPages": 1,
    "currentPage": 0,
    "size": 20
  }
}
```

---

### 4.2 여행지 컬렉션에 추가
**POST** `/collections`

**Headers:**
```
Authorization: Bearer {token}
```

**Request Body:**
```json
{
  "villageId": 123
}
```

**Response 201 Created:**
```json
{
  "success": true,
  "message": "컬렉션에 추가되었습니다.",
  "data": {
    "collectionId": 1,
    "villageId": 123,
    "villageName": "산청마을",
    "collectedAt": "2025-01-15T10:30:00"
  }
}
```

**Response 400 Bad Request:**
```json
{
  "success": false,
  "message": "이미 컬렉션에 추가된 여행지입니다.",
  "error": "ALREADY_COLLECTED"
}
```

---

### 4.3 컬렉션에서 제거
**DELETE** `/collections/{collectionId}`

**Headers:**
```
Authorization: Bearer {token}
```

**Response 200 OK:**
```json
{
  "success": true,
  "message": "컬렉션에서 제거되었습니다."
}
```

---

### 4.4 컬렉션 통계
**GET** `/collections/stats`

**Headers:**
```
Authorization: Bearer {token}
```

**Response 200 OK:**
```json
{
  "success": true,
  "data": {
    "totalCount": 15,
    "regionStats": {
      "경상남도": 5,
      "전라북도": 3,
      "경기도": 7
    }
  }
}
```

---

## 5. 추억 API

### 5.1 내 추억 목록 조회
**GET** `/memories`

**Headers:**
```
Authorization: Bearer {token}
```

**Query Parameters:**
- `page` (선택, 기본값: 0): 페이지 번호
- `size` (선택, 기본값: 20): 페이지 크기

**Response 200 OK:**
```json
{
  "success": true,
  "data": {
    "content": [
      {
        "memoryId": 1,
        "villageId": 123,
        "villageName": "산청마을",
        "sidoName": "경상남도",
        "sigunguName": "산청군",
        "address": "경상남도 산청군 산청읍 산청리 123",
        "content": "정말 좋은 추억이었어요!",
        "visitDate": "2025-01-15",
        "createdAt": "2025-01-15T14:30:00"
      }
    ],
    "totalElements": 5,
    "totalPages": 1,
    "currentPage": 0,
    "size": 20
  }
}
```

---

### 5.2 추억 작성
**POST** `/memories`

**Headers:**
```
Authorization: Bearer {token}
```

**Request Body:**
```json
{
  "villageId": 123,
  "content": "string (필수, 최대 1000자)",
  "visitDate": "string (선택, 형식: YYYY-MM-DD)"
}
```

**Response 201 Created:**
```json
{
  "success": true,
  "message": "추억이 저장되었습니다.",
  "data": {
    "memoryId": 1,
    "villageId": 123,
    "villageName": "산청마을",
    "content": "정말 좋은 추억이었어요!",
    "visitDate": "2025-01-15",
    "createdAt": "2025-01-15T14:30:00"
  }
}
```

---

### 5.3 추억 수정
**PUT** `/memories/{memoryId}`

**Headers:**
```
Authorization: Bearer {token}
```

**Request Body:**
```json
{
  "content": "string (필수, 최대 1000자)",
  "visitDate": "string (선택, 형식: YYYY-MM-DD)"
}
```

**Response 200 OK:**
```json
{
  "success": true,
  "message": "추억이 수정되었습니다.",
  "data": {
    "memoryId": 1,
    "content": "수정된 내용",
    "visitDate": "2025-01-15",
    "updatedAt": "2025-01-15T15:00:00"
  }
}
```

---

### 5.4 추억 삭제
**DELETE** `/memories/{memoryId}`

**Headers:**
```
Authorization: Bearer {token}
```

**Response 200 OK:**
```json
{
  "success": true,
  "message": "추억이 삭제되었습니다."
}
```

---

### 5.5 추억 상세 조회
**GET** `/memories/{memoryId}`

**Headers:**
```
Authorization: Bearer {token}
```

**Response 200 OK:**
```json
{
  "success": true,
  "data": {
    "memoryId": 1,
    "villageId": 123,
    "villageName": "산청마을",
    "sidoName": "경상남도",
    "sigunguName": "산청군",
    "address": "경상남도 산청군 산청읍 산청리 123",
    "content": "정말 좋은 추억이었어요!",
    "visitDate": "2025-01-15",
    "createdAt": "2025-01-15T14:30:00",
    "updatedAt": "2025-01-15T14:30:00"
  }
}
```

---

## 6. 마이페이지 API

### 6.1 내 정보 조회
**GET** `/users/me`

**Headers:**
```
Authorization: Bearer {token}
```

**Response 200 OK:**
```json
{
  "success": true,
  "data": {
    "userId": 1,
    "username": "traveler123",
    "email": "traveler@example.com",
    "collectionCount": 15,
    "memoryCount": 5,
    "createdAt": "2025-01-01T00:00:00"
  }
}
```

---

### 6.2 내 정보 수정
**PUT** `/users/me`

**Headers:**
```
Authorization: Bearer {token}
```

**Request Body:**
```json
{
  "email": "string (선택, 이메일 형식)"
}
```

**Response 200 OK:**
```json
{
  "success": true,
  "message": "정보가 수정되었습니다.",
  "data": {
    "userId": 1,
    "username": "traveler123",
    "email": "newemail@example.com"
  }
}
```

---

## 7. 공통 에러 응답

### 400 Bad Request
```json
{
  "success": false,
  "message": "잘못된 요청입니다.",
  "error": "BAD_REQUEST"
}
```

### 401 Unauthorized
```json
{
  "success": false,
  "message": "인증이 필요합니다.",
  "error": "UNAUTHORIZED"
}
```

### 403 Forbidden
```json
{
  "success": false,
  "message": "권한이 없습니다.",
  "error": "FORBIDDEN"
}
```

### 404 Not Found
```json
{
  "success": false,
  "message": "리소스를 찾을 수 없습니다.",
  "error": "NOT_FOUND"
}
```

### 500 Internal Server Error
```json
{
  "success": false,
  "message": "서버 오류가 발생했습니다.",
  "error": "INTERNAL_SERVER_ERROR"
}
```

---

## 8. 응답 형식 규칙

### 성공 응답
- 모든 성공 응답은 `success: true` 포함
- 데이터는 `data` 필드에 포함
- 메시지는 `message` 필드에 포함 (선택사항)

### 실패 응답
- 모든 실패 응답은 `success: false` 포함
- 에러 메시지는 `message` 필드에 포함
- 에러 코드는 `error` 필드에 포함

### 페이지네이션
- 페이지네이션이 필요한 경우 Spring의 `Page` 객체 사용
- `content`, `totalElements`, `totalPages`, `currentPage`, `size` 필드 포함

---

## 9. Postman 테스트 가이드

### 환경 변수 설정
1. Postman 환경 생성
2. 다음 변수 추가:
   - `baseUrl`: `http://localhost:8080/api`
   - `token`: (로그인 후 자동 설정)

### 테스트 순서
1. **회원가입** (`POST /auth/signup`)
2. **로그인** (`POST /auth/login`) → `token` 변수에 저장
3. **가챠 뽑기** (`POST /gacha/draw`) - Authorization 헤더에 `Bearer {{token}}` 추가
4. **컬렉션 추가** (`POST /collections`)
5. **추억 작성** (`POST /memories`)
6. **마이페이지 조회** (`GET /users/me`)

### Authorization 헤더 설정
- Type: Bearer Token
- Token: `{{token}}`

---

## 10. 데이터 모델

### Village (여행지)
- `villageId`: Long (PK)
- `villageName`: String
- `sidoName`: String
- `sigunguName`: String
- `address`: String
- `phoneNumber`: String
- `latitude`: Double
- `longitude`: Double
- `programName`: String
- `programContent`: String

### User (사용자)
- `userId`: Long (PK)
- `username`: String (Unique)
- `password`: String (암호화)
- `email`: String
- `createdAt`: LocalDateTime

### Collection (컬렉션)
- `collectionId`: Long (PK)
- `userId`: Long (FK)
- `villageId`: Long (FK)
- `collectedAt`: LocalDateTime

### Memory (추억)
- `memoryId`: Long (PK)
- `userId`: Long (FK)
- `villageId`: Long (FK)
- `content`: String
- `visitDate`: LocalDate
- `createdAt`: LocalDateTime
- `updatedAt`: LocalDateTime

### GachaHistory (가챠 이력)
- `gachaId`: Long (PK)
- `userId`: Long (FK)
- `villageId`: Long (FK)
- `drawnAt`: LocalDateTime

