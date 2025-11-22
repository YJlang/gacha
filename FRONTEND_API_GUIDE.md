# 📸 추억 이미지 업로드 API - 프론트엔드 개발자용 가이드

## 🔄 변경 사항 요약

기존 추억(Memory) API가 **JSON** 형식에서 **multipart/form-data** 형식으로 변경되었습니다.
이제 추억을 작성하거나 수정할 때 이미지 파일을 함께 업로드할 수 있습니다.

---

## 📋 API 엔드포인트

### 1. 추억 작성 (이미지 포함)

**Endpoint**: `POST /api/memories`

**Headers**:
```
Authorization: Bearer {token}
Content-Type: multipart/form-data
```

**Request (FormData)**:
```javascript
const formData = new FormData();
formData.append('villageId', 1);
formData.append('content', '정말 즐거운 여행이었습니다!');
formData.append('visitDate', '2025-11-20');  // 선택사항
formData.append('image', fileObject);        // 선택사항

// Axios 예시
const response = await client.post('/memories', formData, {
  headers: {
    'Content-Type': 'multipart/form-data'
  }
});
```

**Request Fields**:
| 필드 | 타입 | 필수 | 설명 |
|------|------|------|------|
| `villageId` | number | ✅ | 여행지 ID |
| `content` | string | ✅ | 추억 내용 (1-1000자) |
| `visitDate` | string | ❌ | 방문 날짜 (YYYY-MM-DD) |
| `image` | File | ❌ | 이미지 파일 (최대 5MB) |

**허용 이미지 형식**: jpg, jpeg, png, gif, webp

**Response**:
```json
{
  "success": true,
  "message": "추억이 작성되었습니다.",
  "data": {
    "memoryId": 1,
    "villageId": 1,
    "villageName": "행복마을",
    "sidoName": "경기도",
    "sigunguName": "가평군",
    "address": "경기도 가평군...",
    "content": "정말 즐거운 여행이었습니다!",
    "visitDate": "2025-11-20",
    "imageUrl": "http://localhost:8080/uploads/memories/a1b2c3d4-5e6f-7g8h-9i0j-kl1mn2op3qr4_photo.jpg",
    "createdAt": "2025-11-22T11:30:00",
    "updatedAt": "2025-11-22T11:30:00"
  }
}
```

---

### 2. 추억 수정 (이미지 변경 가능)

**Endpoint**: `PUT /api/memories/{memoryId}`

**Headers**:
```
Authorization: Bearer {token}
Content-Type: multipart/form-data
```

**Request (FormData)**:
```javascript
const formData = new FormData();
formData.append('content', '수정된 내용입니다.');
formData.append('visitDate', '2025-11-20');
formData.append('image', newFileObject);  // 선택사항: 새 이미지로 교체

const response = await client.put(`/memories/${memoryId}`, formData, {
  headers: {
    'Content-Type': 'multipart/form-data'
  }
});
```

**Request Fields**:
| 필드 | 타입 | 필수 | 설명 |
|------|------|------|------|
| `content` | string | ✅ | 추억 내용 (1-1000자) |
| `visitDate` | string | ❌ | 방문 날짜 (YYYY-MM-DD) |
| `image` | File | ❌ | 새 이미지 (기존 이미지 자동 삭제) |

> **⚠️ 중요**: 새 이미지를 업로드하면 기존 이미지는 서버에서 자동으로 삭제됩니다.

**Response**: 추억 작성과 동일한 형식

---

### 3. 추억 목록 조회

**Endpoint**: `GET /api/memories?page=0&size=20`

**Headers**:
```
Authorization: Bearer {token}
```

**Response**:
```json
{
  "success": true,
  "data": {
    "content": [
      {
        "memoryId": 1,
        "villageId": 1,
        "villageName": "행복마을",
        "sidoName": "경기도",
        "content": "정말 즐거운 여행이었습니다!",
        "visitDate": "2025-11-20",
        "imageUrl": "http://localhost:8080/uploads/memories/a1b2c3d4_photo.jpg",
        "createdAt": "2025-11-22T11:30:00",
        "updatedAt": "2025-11-22T11:30:00"
      }
    ],
    "totalPages": 1,
    "totalElements": 5,
    "currentPage": 0,
    "size": 20
  }
}
```

> **📝 참고**: `imageUrl`이 `null`인 경우 이미지가 없는 추억입니다.

---

### 4. 추억 삭제

**Endpoint**: `DELETE /api/memories/{memoryId}`

**Headers**:
```
Authorization: Bearer {token}
```

> **🗑️ 자동 삭제**: 추억을 삭제하면 연결된 이미지 파일도 서버에서 자동으로 삭제됩니다.

---

## 💻 프론트엔드 구현 예시

### 파일 선택 및 미리보기

```javascript
import { useState } from 'react';

function MemoryAdd() {
  const [selectedImage, setSelectedImage] = useState(null);
  const [previewUrl, setPreviewUrl] = useState(null);
  const [content, setContent] = useState('');
  const [visitDate, setVisitDate] = useState('');

  // 이미지 선택 핸들러
  const handleImageChange = (e) => {
    const file = e.target.files[0];
    if (file) {
      setSelectedImage(file);
      
      // 미리보기 URL 생성
      const reader = new FileReader();
      reader.onloadend = () => {
        setPreviewUrl(reader.result);
      };
      reader.readAsDataURL(file);
    }
  };

  // 추억 저장
  const handleSubmit = async () => {
    const formData = new FormData();
    formData.append('villageId', villageId);
    formData.append('content', content);
    if (visitDate) formData.append('visitDate', visitDate);
    if (selectedImage) formData.append('image', selectedImage);

    try {
      const res = await client.post('/memories', formData, {
        headers: { 'Content-Type': 'multipart/form-data' }
      });
      
      if (res.data.success) {
        alert('저장되었습니다!');
      }
    } catch (error) {
      console.error('저장 실패:', error);
    }
  };

  return (
    <div>
      {/* 이미지 선택 */}
      <input 
        type="file" 
        accept="image/*" 
        onChange={handleImageChange}
      />
      
      {/* 미리보기 */}
      {previewUrl && (
        <img src={previewUrl} alt="미리보기" />
      )}
      
      {/* 나머지 폼... */}
    </div>
  );
}
```

---

## 🚨 주의사항

### 1. Content-Type 헤더
- **이전**: `Content-Type: application/json`
- **현재**: `Content-Type: multipart/form-data`
- Axios는 FormData를 자동으로 감지하므로 명시적으로 설정하는 것이 좋습니다.

### 2. 데이터 형식
- **이전**: JSON 객체 `{ villageId: 1, content: "..." }`
- **현재**: FormData 객체

### 3. 파일 크기 제한
- 최대 파일 크기: **5MB**
- 최대 요청 크기: **10MB**
- 초과 시 서버에서 에러 반환

### 4. 이미지 URL
- 이미지 URL은 절대 경로로 제공됩니다
- 예: `http://localhost:8080/uploads/memories/{filename}`
- `<img src={imageUrl} />` 형태로 바로 사용 가능

### 5. 이미지 없는 추억
- `image` 필드는 선택사항입니다
- 이미지 없이 추억만 작성 가능
- 응답의 `imageUrl` 필드가 `null`입니다

---

## ✅ 체크리스트

프론트엔드에서 확인해야 할 사항:

- [ ] FormData 형식으로 요청 전송
- [ ] Content-Type 헤더 설정
- [ ] 파일 선택 input 구현
- [ ] 이미지 미리보기 기능
- [ ] imageUrl 필드 화면에 표시
- [ ] 이미지 없는 경우 처리 (null 체크)
- [ ] 파일 크기 검증 (5MB 초과 시 경고)
- [ ] 추억 수정 시 이미지 교체 기능

---

## 🔗 관련 문서

- [전체 API 문서](file:///c:/gacha/API_ENDPOINTS.md)
- [구현 Walkthrough](file:///C:/Users/wnsgk/.gemini/antigravity/brain/d125086f-3711-420d-9712-925f6da2bea1/walkthrough.md)
