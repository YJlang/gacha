# 가챠 여행 서비스 API 테스트 가이드 (Postman)

## 📋 목차
1. [환경 설정](#환경-설정)
2. [인증 API](#1-인증-api)
3. [가챠 API](#2-가챠-api)
4. [컬렉션 API](#3-컬렉션-api)
5. [추억 API](#4-추억-api)
6. [여행지 API](#5-여행지-api)
7. [마이페이지 API](#6-마이페이지-api)
8. [테스트 시나리오](#테스트-시나리오)

---

## 환경 설정

### Postman Environment Variables
```json
{
  "baseUrl": "http://localhost:8080",
  "token": ""
}
```

**사용법:**
1. Postman에서 Environment 생성
2. `baseUrl` 변수 추가: `http://localhost:8080`
3. `token` 변수 추가: 로그인 후 자동으로 설정됨

---

## 1. 인증 API

### 1.1 회원가입
**POST** `{{baseUrl}}/api/auth/signup`

#### Request
```json
{
  "username": "testuser",
  "password": "password123",
  "email": "testuser@example.com"
}
```

#### Response (201 Created)
```json
{
  "success": true,
  "message": "회원가입이 완료되었습니다.",
  "data": {
    "token": "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiIxIiwidXNlcm5hbWUiOiJ0ZXN0dXNlciIsImlhdCI6MTcwMDAwMDAwMCwiZXhwIjoxNzAwMDg2NDAwfQ...",
    "userId": 1,
    "username": "testuser",
    "email": "testuser@example.com"
  },
  "error": null
}
```

#### Postman Tests Script (자동 토큰 저장)
```javascript
if (pm.response.code === 201) {
    var jsonData = pm.response.json();
    pm.environment.set("token", jsonData.data.token);
}
```

---

### 1.2 로그인
**POST** `{{baseUrl}}/api/auth/login`

#### Request
```json
{
  "username": "testuser",
  "password": "password123"
}
```

#### Response (200 OK)
```json
{
  "success": true,
  "message": "로그인 성공",
  "data": {
    "token": "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiIxIiwidXNlcm5hbWUiOiJ0ZXN0dXNlciIsImlhdCI6MTcwMDAwMDAwMCwiZXhwIjoxNzAwMDg2NDAwfQ...",
    "userId": 1,
    "username": "testuser",
    "email": "testuser@example.com"
  },
  "error": null
}
```

#### Postman Tests Script (자동 토큰 저장)
```javascript
if (pm.response.code === 200) {
    var jsonData = pm.response.json();
    pm.environment.set("token", jsonData.data.token);
}
```

#### 에러 응답 예시 (400 Bad Request)
```json
{
  "success": false,
  "message": "아이디 또는 비밀번호가 올바르지 않습니다.",
  "data": null,
  "error": "INVALID_CREDENTIALS"
}
```

---

## 2. 가챠 API

### 2.1 가챠 뽑기
**POST** `{{baseUrl}}/api/gacha/draw`

#### Headers
```
Authorization: Bearer {{token}}
```

#### Request Body (필터 없음)
```json
{
  "region": null,
  "programType": null
}
```

#### Request Body (지역 필터)
```json
{
  "region": "경상남도",
  "programType": null
}
```

#### Request Body (프로그램 유형 필터)
```json
{
  "region": null,
  "programType": "체험"
}
```

#### Response (201 Created)
```json
{
  "success": true,
  "message": "가챠 뽑기 성공!",
  "data": {
    "villageId": 42,
    "villageName": "청학동마을",
    "sidoName": "경상남도",
    "sigunguName": "하동군",
    "address": "경상남도 하동군 청학로 123",
    "phoneNumber": "055-123-4567",
    "latitude": 35.123456,
    "longitude": 127.654321,
    "programName": "전통문화체험",
    "programContent": "다도, 서예, 전통놀이 등",
    "isNew": true,
    "drawnAt": "2025-11-18T14:30:00",
    "isCollected": null,
    "collectedAt": null
  },
  "error": null
}
```

#### 에러 응답 - 일일 제한 초과 (400 Bad Request)
```json
{
  "success": false,
  "message": "오늘 가챠 횟수를 모두 사용했습니다. 내일 다시 시도해주세요.",
  "data": null,
  "error": "DAILY_LIMIT_EXCEEDED"
}
```

#### 에러 응답 - 조건 맞는 여행지 없음 (400 Bad Request)
```json
{
  "success": false,
  "message": "조건에 맞는 여행지가 없습니다.",
  "data": null,
  "error": "NO_VILLAGES_AVAILABLE"
}
```

---

### 2.2 가챠 상태 확인
**GET** `{{baseUrl}}/api/gacha/status`

#### Headers
```
Authorization: Bearer {{token}}
```

#### Response (200 OK) - 뽑기 가능
```json
{
  "success": true,
  "message": "가챠 상태 조회 성공",
  "data": {
    "canDraw": true,
    "remainingCount": 1,
    "lastDrawTime": null,
    "todayDrawCount": 0
  },
  "error": null
}
```

#### Response (200 OK) - 뽑기 불가능
```json
{
  "success": true,
  "message": "가챠 상태 조회 성공",
  "data": {
    "canDraw": false,
    "remainingCount": 0,
    "lastDrawTime": "2025-11-18T14:30:00",
    "todayDrawCount": 1
  },
  "error": null
}
```

---

## 3. 컬렉션 API

### 3.1 컬렉션에 추가
**POST** `{{baseUrl}}/api/collections`

#### Headers
```
Authorization: Bearer {{token}}
```

#### Request
```json
{
  "villageId": 42
}
```

#### Response (201 Created)
```json
{
  "success": true,
  "message": "컬렉션에 추가되었습니다.",
  "data": {
    "collectionId": 1,
    "villageId": 42,
    "villageName": "청학동마을",
    "sidoName": "경상남도",
    "sigunguName": "하동군",
    "address": "경상남도 하동군 청학로 123",
    "phoneNumber": "055-123-4567",
    "latitude": 35.123456,
    "longitude": 127.654321,
    "programName": "전통문화체험",
    "programContent": "다도, 서예, 전통놀이 등",
    "collectedAt": "2025-11-18T14:35:00"
  },
  "error": null
}
```

#### 에러 응답 - 이미 컬렉션에 존재 (400 Bad Request)
```json
{
  "success": false,
  "message": "이미 컬렉션에 추가된 여행지입니다.",
  "data": null,
  "error": "ALREADY_COLLECTED"
}
```

#### 에러 응답 - 여행지 없음 (400 Bad Request)
```json
{
  "success": false,
  "message": "여행지를 찾을 수 없습니다.",
  "data": null,
  "error": "VILLAGE_NOT_FOUND"
}
```

---

### 3.2 내 컬렉션 조회
**GET** `{{baseUrl}}/api/collections?page=0&size=20`

#### Headers
```
Authorization: Bearer {{token}}
```

#### Query Parameters
- `page`: 페이지 번호 (기본값: 0)
- `size`: 페이지 크기 (기본값: 20)

#### Response (200 OK)
```json
{
  "success": true,
  "message": "내 컬렉션 조회 성공",
  "data": {
    "content": [
      {
        "collectionId": 1,
        "villageId": 42,
        "villageName": "청학동마을",
        "sidoName": "경상남도",
        "sigunguName": "하동군",
        "address": "경상남도 하동군 청학로 123",
        "phoneNumber": "055-123-4567",
        "latitude": 35.123456,
        "longitude": 127.654321,
        "programName": "전통문화체험",
        "programContent": "다도, 서예, 전통놀이 등",
        "collectedAt": "2025-11-18T14:35:00"
      },
      {
        "collectionId": 2,
        "villageId": 78,
        "villageName": "남해바다마을",
        "sidoName": "경상남도",
        "sigunguName": "남해군",
        "address": "경상남도 남해군 바다로 456",
        "phoneNumber": "055-234-5678",
        "latitude": 34.567890,
        "longitude": 128.123456,
        "programName": "해양체험",
        "programContent": "낚시, 스노쿨링, 조개잡이 등",
        "collectedAt": "2025-11-17T10:20:00"
      }
    ],
    "pageable": {
      "pageNumber": 0,
      "pageSize": 20,
      "sort": {
        "sorted": false,
        "empty": true,
        "unsorted": true
      },
      "offset": 0,
      "paged": true,
      "unpaged": false
    },
    "totalElements": 2,
    "totalPages": 1,
    "last": true,
    "size": 20,
    "number": 0,
    "sort": {
      "sorted": false,
      "empty": true,
      "unsorted": true
    },
    "numberOfElements": 2,
    "first": true,
    "empty": false
  },
  "error": null
}
```

---

### 3.3 컬렉션에서 제거
**DELETE** `{{baseUrl}}/api/collections/1`

#### Headers
```
Authorization: Bearer {{token}}
```

#### Response (200 OK)
```json
{
  "success": true,
  "message": "컬렉션에서 제거되었습니다.",
  "data": null,
  "error": null
}
```

#### 에러 응답 - 컬렉션 없음 (400 Bad Request)
```json
{
  "success": false,
  "message": "컬렉션을 찾을 수 없습니다.",
  "data": null,
  "error": "COLLECTION_NOT_FOUND"
}
```

---

### 3.4 컬렉션 통계 조회
**GET** `{{baseUrl}}/api/collections/stats`

#### Headers
```
Authorization: Bearer {{token}}
```

#### Response (200 OK)
```json
{
  "success": true,
  "message": "컬렉션 통계 조회 성공",
  "data": {
    "totalCount": 5,
    "regionStats": {
      "경상남도": 3,
      "전라북도": 1,
      "강원도": 1
    }
  },
  "error": null
}
```

---

## 4. 추억 API

### 4.1 추억 작성
**POST** `{{baseUrl}}/api/memories`

#### Headers
```
Authorization: Bearer {{token}}
```

#### Request
```json
{
  "villageId": 42,
  "content": "청학동마을에서 다도 체험을 했습니다. 정말 평화로운 시간이었어요. 전통 한옥에서 차를 마시며 자연을 느낄 수 있었습니다.",
  "visitDate": "2025-11-15"
}
```

#### Request (방문 날짜 없이)
```json
{
  "villageId": 42,
  "content": "청학동마을 방문 계획 중입니다. 다음 주말에 가볼 예정!",
  "visitDate": null
}
```

#### Response (201 Created)
```json
{
  "success": true,
  "message": "추억이 작성되었습니다.",
  "data": {
    "memoryId": 1,
    "villageId": 42,
    "villageName": "청학동마을",
    "sidoName": "경상남도",
    "sigunguName": "하동군",
    "address": "경상남도 하동군 청학로 123",
    "phoneNumber": "055-123-4567",
    "latitude": 35.123456,
    "longitude": 127.654321,
    "programName": "전통문화체험",
    "programContent": "다도, 서예, 전통놀이 등",
    "content": "청학동마을에서 다도 체험을 했습니다. 정말 평화로운 시간이었어요. 전통 한옥에서 차를 마시며 자연을 느낄 수 있었습니다.",
    "visitDate": "2025-11-15",
    "createdAt": "2025-11-18T15:00:00",
    "updatedAt": "2025-11-18T15:00:00"
  },
  "error": null
}
```

#### 에러 응답 - villageId 누락 (400 Bad Request)
```json
{
  "success": false,
  "message": "잘못된 요청입니다.",
  "data": null,
  "error": "BAD_REQUEST"
}
```

#### 에러 응답 - content 누락 (400 Bad Request)
```json
{
  "success": false,
  "message": "추억 내용은 필수입니다.",
  "data": null,
  "error": "VALIDATION_ERROR"
}
```

---

### 4.2 내 추억 목록 조회
**GET** `{{baseUrl}}/api/memories?page=0&size=20`

#### Headers
```
Authorization: Bearer {{token}}
```

#### Query Parameters
- `page`: 페이지 번호 (기본값: 0)
- `size`: 페이지 크기 (기본값: 20)

#### Response (200 OK)
```json
{
  "success": true,
  "message": "내 추억 목록 조회 성공",
  "data": {
    "content": [
      {
        "memoryId": 1,
        "villageId": 42,
        "villageName": "청학동마을",
        "sidoName": "경상남도",
        "sigunguName": "하동군",
        "address": "경상남도 하동군 청학로 123",
        "phoneNumber": "055-123-4567",
        "latitude": 35.123456,
        "longitude": 127.654321,
        "programName": "전통문화체험",
        "programContent": "다도, 서예, 전통놀이 등",
        "content": "청학동마을에서 다도 체험을 했습니다. 정말 평화로운 시간이었어요.",
        "visitDate": "2025-11-15",
        "createdAt": "2025-11-18T15:00:00",
        "updatedAt": "2025-11-18T15:00:00"
      }
    ],
    "totalElements": 1,
    "totalPages": 1,
    "size": 20,
    "number": 0,
    "first": true,
    "last": true,
    "empty": false
  },
  "error": null
}
```

---

### 4.3 추억 상세 조회
**GET** `{{baseUrl}}/api/memories/1`

#### Headers
```
Authorization: Bearer {{token}}
```

#### Response (200 OK)
```json
{
  "success": true,
  "message": "추억 조회 성공",
  "data": {
    "memoryId": 1,
    "villageId": 42,
    "villageName": "청학동마을",
    "sidoName": "경상남도",
    "sigunguName": "하동군",
    "address": "경상남도 하동군 청학로 123",
    "phoneNumber": "055-123-4567",
    "latitude": 35.123456,
    "longitude": 127.654321,
    "programName": "전통문화체험",
    "programContent": "다도, 서예, 전통놀이 등",
    "content": "청학동마을에서 다도 체험을 했습니다. 정말 평화로운 시간이었어요.",
    "visitDate": "2025-11-15",
    "createdAt": "2025-11-18T15:00:00",
    "updatedAt": "2025-11-18T15:00:00"
  },
  "error": null
}
```

#### 에러 응답 - 추억 없음 또는 권한 없음 (400 Bad Request)
```json
{
  "success": false,
  "message": "추억을 찾을 수 없습니다.",
  "data": null,
  "error": "MEMORY_NOT_FOUND"
}
```

---

### 4.4 추억 수정
**PUT** `{{baseUrl}}/api/memories/1`

#### Headers
```
Authorization: Bearer {{token}}
```

#### Request
```json
{
  "content": "청학동마을에서 다도와 서예 체험을 모두 했습니다. 정말 평화로운 시간이었어요. 다음에 또 오고 싶습니다!",
  "visitDate": "2025-11-15"
}
```

#### Response (200 OK)
```json
{
  "success": true,
  "message": "추억이 수정되었습니다.",
  "data": {
    "memoryId": 1,
    "villageId": 42,
    "villageName": "청학동마을",
    "sidoName": "경상남도",
    "sigunguName": "하동군",
    "address": "경상남도 하동군 청학로 123",
    "phoneNumber": "055-123-4567",
    "latitude": 35.123456,
    "longitude": 127.654321,
    "programName": "전통문화체험",
    "programContent": "다도, 서예, 전통놀이 등",
    "content": "청학동마을에서 다도와 서예 체험을 모두 했습니다. 정말 평화로운 시간이었어요. 다음에 또 오고 싶습니다!",
    "visitDate": "2025-11-15",
    "createdAt": "2025-11-18T15:00:00",
    "updatedAt": "2025-11-18T15:30:00"
  },
  "error": null
}
```

---

### 4.5 추억 삭제
**DELETE** `{{baseUrl}}/api/memories/1`

#### Headers
```
Authorization: Bearer {{token}}
```

#### Response (200 OK)
```json
{
  "success": true,
  "message": "추억이 삭제되었습니다.",
  "data": null,
  "error": null
}
```

---

## 5. 여행지 API

### 5.1 여행지 상세 조회
**GET** `{{baseUrl}}/api/villages/42`

#### Headers (선택사항 - 컬렉션 여부 확인용)
```
Authorization: Bearer {{token}}
```

#### Response (200 OK) - 인증 없이
```json
{
  "success": true,
  "message": "여행지 조회 성공",
  "data": {
    "villageId": 42,
    "villageName": "청학동마을",
    "sidoName": "경상남도",
    "sigunguName": "하동군",
    "address": "경상남도 하동군 청학로 123",
    "phoneNumber": "055-123-4567",
    "latitude": 35.123456,
    "longitude": 127.654321,
    "programName": "전통문화체험",
    "programContent": "다도, 서예, 전통놀이 등",
    "isNew": null,
    "drawnAt": null,
    "isCollected": false,
    "collectedAt": null
  },
  "error": null
}
```

#### Response (200 OK) - 인증 포함 (컬렉션에 이미 추가된 경우)
```json
{
  "success": true,
  "message": "여행지 조회 성공",
  "data": {
    "villageId": 42,
    "villageName": "청학동마을",
    "sidoName": "경상남도",
    "sigunguName": "하동군",
    "address": "경상남도 하동군 청학로 123",
    "phoneNumber": "055-123-4567",
    "latitude": 35.123456,
    "longitude": 127.654321,
    "programName": "전통문화체험",
    "programContent": "다도, 서예, 전통놀이 등",
    "isNew": null,
    "drawnAt": null,
    "isCollected": true,
    "collectedAt": null
  },
  "error": null
}
```

#### 에러 응답 - 여행지 없음 (400 Bad Request)
```json
{
  "success": false,
  "message": "여행지를 찾을 수 없습니다.",
  "data": null,
  "error": "VILLAGE_NOT_FOUND"
}
```

---

### 5.2 여행지 목록 조회
**GET** `{{baseUrl}}/api/villages?page=0&size=20&region=경상남도&programType=체험`

#### Query Parameters
- `page`: 페이지 번호 (기본값: 0)
- `size`: 페이지 크기 (기본값: 20)
- `region`: 지역 필터 (선택사항, 예: "경상남도")
- `programType`: 프로그램 유형 필터 (선택사항, 예: "체험")

#### Response (200 OK)
```json
{
  "success": true,
  "message": "여행지 목록 조회 성공",
  "data": {
    "content": [
      {
        "villageId": 42,
        "villageName": "청학동마을",
        "sidoName": "경상남도",
        "sigunguName": "하동군",
        "address": "경상남도 하동군 청학로 123",
        "phoneNumber": "055-123-4567",
        "latitude": 35.123456,
        "longitude": 127.654321,
        "programName": "전통문화체험",
        "programContent": "다도, 서예, 전통놀이 등"
      },
      {
        "villageId": 78,
        "villageName": "남해바다마을",
        "sidoName": "경상남도",
        "sigunguName": "남해군",
        "address": "경상남도 남해군 바다로 456",
        "phoneNumber": "055-234-5678",
        "latitude": 34.567890,
        "longitude": 128.123456,
        "programName": "해양체험",
        "programContent": "낚시, 스노쿨링, 조개잡이 등"
      }
    ],
    "totalElements": 2,
    "totalPages": 1,
    "size": 20,
    "number": 0,
    "first": true,
    "last": true,
    "empty": false
  },
  "error": null
}
```

---

## 6. 마이페이지 API

### 6.1 내 정보 조회
**GET** `{{baseUrl}}/api/users/me`

#### Headers
```
Authorization: Bearer {{token}}
```

#### Response (200 OK)
```json
{
  "success": true,
  "message": "내 정보 조회 성공",
  "data": {
    "userId": 1,
    "username": "testuser",
    "email": "testuser@example.com",
    "createdAt": "2025-11-18T10:00:00",
    "collectionCount": 5,
    "memoryCount": 3
  },
  "error": null
}
```

---

### 6.2 내 정보 수정
**PUT** `{{baseUrl}}/api/users/me`

#### Headers
```
Authorization: Bearer {{token}}
```

#### Request
```json
{
  "email": "newemail@example.com"
}
```

#### Response (200 OK)
```json
{
  "success": true,
  "message": "내 정보가 수정되었습니다.",
  "data": {
    "userId": 1,
    "username": "testuser",
    "email": "newemail@example.com",
    "createdAt": "2025-11-18T10:00:00",
    "collectionCount": 5,
    "memoryCount": 3
  },
  "error": null
}
```

#### 에러 응답 - 이메일 중복 (400 Bad Request)
```json
{
  "success": false,
  "message": "이미 존재하는 이메일입니다.",
  "data": null,
  "error": "EMAIL_ALREADY_EXISTS"
}
```

---

## 테스트 시나리오

### 시나리오 1: 신규 사용자 전체 플로우

1. **회원가입**
   ```
   POST /api/auth/signup
   → 토큰 자동 저장
   ```

2. **가챠 상태 확인**
   ```
   GET /api/gacha/status
   → canDraw: true 확인
   ```

3. **가챠 뽑기**
   ```
   POST /api/gacha/draw
   → villageId 메모
   ```

4. **컬렉션에 추가**
   ```
   POST /api/collections
   → villageId 사용
   ```

5. **가챠 상태 재확인**
   ```
   GET /api/gacha/status
   → canDraw: false 확인
   ```

6. **내 컬렉션 조회**
   ```
   GET /api/collections
   → 1개 확인
   ```

7. **여행지 상세 조회**
   ```
   GET /api/villages/{villageId}
   → isCollected: true 확인
   ```

8. **추억 작성**
   ```
   POST /api/memories
   → 방문 계획 작성
   ```

9. **마이페이지 확인**
   ```
   GET /api/users/me
   → collectionCount: 1, memoryCount: 1 확인
   ```

10. **컬렉션 통계 확인**
    ```
    GET /api/collections/stats
    → 지역별 통계 확인
    ```

---

### 시나리오 2: 추억 관리 플로우

1. **로그인**
   ```
   POST /api/auth/login
   ```

2. **내 추억 목록 조회**
   ```
   GET /api/memories
   ```

3. **추억 상세 조회**
   ```
   GET /api/memories/{memoryId}
   ```

4. **추억 수정**
   ```
   PUT /api/memories/{memoryId}
   → 방문 후기 업데이트
   ```

5. **추억 삭제**
   ```
   DELETE /api/memories/{memoryId}
   ```

---

### 시나리오 3: 여행지 탐색 플로우

1. **여행지 목록 조회 (필터 없음)**
   ```
   GET /api/villages?page=0&size=10
   ```

2. **여행지 목록 조회 (지역 필터)**
   ```
   GET /api/villages?region=경상남도
   ```

3. **여행지 목록 조회 (프로그램 필터)**
   ```
   GET /api/villages?programType=체험
   ```

4. **여행지 상세 조회**
   ```
   GET /api/villages/{villageId}
   ```

5. **컬렉션에 추가**
   ```
   POST /api/collections
   ```

---

### 시나리오 4: 에러 핸들링 테스트

1. **중복 회원가입 시도**
   ```
   POST /api/auth/signup
   → USERNAME_ALREADY_EXISTS 에러 확인
   ```

2. **잘못된 로그인**
   ```
   POST /api/auth/login
   → INVALID_CREDENTIALS 에러 확인
   ```

3. **일일 가챠 제한 초과**
   ```
   POST /api/gacha/draw (2회)
   → DAILY_LIMIT_EXCEEDED 에러 확인
   ```

4. **중복 컬렉션 추가**
   ```
   POST /api/collections (동일 villageId 2회)
   → ALREADY_COLLECTED 에러 확인
   ```

5. **존재하지 않는 여행지 조회**
   ```
   GET /api/villages/999999
   → VILLAGE_NOT_FOUND 에러 확인
   ```

6. **권한 없는 추억 수정 시도**
   ```
   PUT /api/memories/{다른_사용자의_memoryId}
   → MEMORY_NOT_FOUND 에러 확인
   ```

---

## 📝 추가 팁

### 1. Authorization Header 자동 설정
Collection 레벨에서 Authorization 설정:
```
Type: Bearer Token
Token: {{token}}
```

### 2. Pre-request Script (전역 설정)
```javascript
// 토큰이 없으면 경고
if (!pm.environment.get("token")) {
    console.warn("⚠️ 토큰이 없습니다. 먼저 로그인하세요.");
}
```

### 3. Test Script 템플릿
```javascript
// 성공 응답 확인
pm.test("Status code is 2xx", function () {
    pm.response.to.have.status(200) ||
    pm.response.to.have.status(201);
});

// success 필드 확인
pm.test("Response has success field", function () {
    var jsonData = pm.response.json();
    pm.expect(jsonData.success).to.be.true;
});

// 응답 시간 확인
pm.test("Response time is less than 1000ms", function () {
    pm.expect(pm.response.responseTime).to.be.below(1000);
});
```

---

## 🎯 우선순위 테스트 순서

1. **필수 테스트** (먼저 실행)
   - 회원가입
   - 로그인
   - 가챠 뽑기
   - 컬렉션 추가
   - 추억 작성

2. **기능 테스트**
   - 여행지 목록 조회
   - 마이페이지
   - 통계 조회

3. **에러 테스트**
   - 중복 처리
   - 권한 확인
   - 유효성 검증

**테스트를 시작하기 전에 서버를 실행하세요:**
```bash
cd c:/gacha-ex/BE/gacha
./gradlew bootRun
```

**애플리케이션 실행 확인:**
- URL: http://localhost:8080
- 서버가 정상 실행되면 Postman 테스트 시작!

---

**Happy Testing! 🚀**
