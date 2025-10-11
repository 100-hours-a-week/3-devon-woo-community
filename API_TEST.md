# API 테스트 가이드

모든 API 엔드포인트와 요청 예제입니다. Postman에서 테스트할 수 있습니다.

**Base URL**: `http://localhost:8080`

---

## 📋 목차
- [1. 인증 (Auth)](#1-인증-auth)
- [2. 회원 (Member)](#2-회원-member)
- [3. 게시글 (Post)](#3-게시글-post)
- [4. 댓글 (Comment)](#4-댓글-comment)

---

## 1. 인증 (Auth)

### 1.1 회원가입
```
POST /api/v1/auth/signup
Content-Type: application/json
```

**Request Body**:
```json
{
  "email": "newuser@test.com",
  "password": "password123!",
  "nickname": "신규유저",
  "profileImageUrl": "https://example.com/profile.jpg"
}
```

**Expected Response** (201 Created):
```json
{
  "success": true,
  "message": "signup_success",
  "data": {
    "memberId": 4,
    "email": "newuser@test.com",
    "nickname": "신규유저"
  }
}
```

---

### 1.2 로그인
```
POST /api/v1/auth/login
Content-Type: application/json
```

**Request Body**:
```json
{
  "email": "user1@test.com",
  "password": "password123!"
}
```

**Expected Response** (200 OK):
```json
{
  "success": true,
  "message": "login_success",
  "data": {
    "memberId": 1,
    "email": "user1@test.com",
    "nickname": "테스트유저1",
    "accessToken": "eyJhbGc..."
  }
}
```

---

### 1.3 로그아웃
```
POST /api/v1/auth/logout
```

**Expected Response** (204 No Content):
```
(No content)
```

---

## 2. 회원 (Member)

### 2.1 회원 정보 수정
```
PATCH /api/v1/members/{id}
Content-Type: application/json
```

**Request Body**:
```json
{
  "nickname": "수정된닉네임",
  "profileImageUrl": "https://example.com/new-profile.jpg"
}
```

**Example**:
```
PATCH /api/v1/members/1
```

**Expected Response** (200 OK):
```json
{
  "success": true,
  "message": "member_update_success",
  "data": {
    "memberId": 1,
    "nickname": "수정된닉네임",
    "profileImageUrl": "https://example.com/new-profile.jpg"
  }
}
```

---

### 2.2 비밀번호 변경
```
POST /api/v1/members/{id}/password
Content-Type: application/json
```

**Request Body**:
```json
{
  "currentPassword": "password123!",
  "newPassword": "newPassword456!"
}
```

**Example**:
```
POST /api/v1/members/1/password
```

**Expected Response** (204 No Content):
```
(No content)
```

---

### 2.3 회원 탈퇴
```
DELETE /api/v1/members/{id}
```

**Example**:
```
DELETE /api/v1/members/1
```

**Expected Response** (204 No Content):
```
(No content)
```

---

## 3. 게시글 (Post)

### 3.1 게시글 생성
```
POST /api/v1/posts
Content-Type: application/json
```

**Request Body**:
```json
{
  "authorId": 1,
  "title": "새로운 게시글",
  "content": "게시글 내용입니다.",
  "image": "https://example.com/image.jpg"
}
```

**Expected Response** (201 Created):
```json
{
  "success": true,
  "message": "post_created",
  "data": {
    "postId": 3,
    "author": {
      "memberId": 1,
      "nickname": "테스트유저1",
      "profileImage": "https://example.com/profile1.jpg"
    },
    "title": "새로운 게시글",
    "content": "게시글 내용입니다.",
    "imageUrl": "https://example.com/image.jpg",
    "createdAt": "2025-01-15T10:30:00",
    "updatedAt": "2025-01-15T10:30:00",
    "views": 0,
    "likes": 0
  }
}
```

---

### 3.2 게시글 목록 조회
```
GET /api/v1/posts?page=1&size=20
```

**Query Parameters**:
- `page` (optional, default: 1): 페이지 번호
- `size` (optional, default: 20): 페이지 크기

**Expected Response** (200 OK):
```json
{
  "success": true,
  "message": "posts_retrieved",
  "data": {
    "posts": [
      {
        "postId": 1,
        "author": {
          "memberId": 1,
          "nickname": "테스트유저1"
        },
        "title": "첫 번째 게시글",
        "views": 5,
        "likes": 2,
        "commentCount": 3,
        "createdAt": "2025-01-15T10:00:00"
      }
    ],
    "page": 1,
    "size": 20
  }
}
```

---

### 3.3 게시글 단건 조회
```
GET /api/v1/posts/{postId}
```

**Example**:
```
GET /api/v1/posts/1
```

**Expected Response** (200 OK):
```json
{
  "success": true,
  "message": "post_retrieved",
  "data": {
    "postId": 1,
    "author": {
      "memberId": 1,
      "nickname": "테스트유저1",
      "profileImage": "https://example.com/profile1.jpg"
    },
    "title": "첫 번째 게시글",
    "content": "안녕하세요! 첫 번째 게시글입니다.",
    "imageUrl": null,
    "createdAt": "2025-01-15T10:00:00",
    "updatedAt": "2025-01-15T10:00:00",
    "views": 6,
    "likes": 2
  }
}
```

---

### 3.4 게시글 수정
```
PATCH /api/v1/posts/{postId}
Content-Type: application/json
```

**Request Body**:
```json
{
  "authorId": 1,
  "title": "수정된 게시글 제목",
  "content": "수정된 게시글 내용입니다.",
  "image": "https://example.com/updated-image.jpg"
}
```

**Example**:
```
PATCH /api/v1/posts/1
```

**Expected Response** (200 OK):
```json
{
  "success": true,
  "message": "post_updated",
  "data": {
    "postId": 1,
    "author": {
      "memberId": 1,
      "nickname": "테스트유저1",
      "profileImage": "https://example.com/profile1.jpg"
    },
    "title": "수정된 게시글 제목",
    "content": "수정된 게시글 내용입니다.",
    "imageUrl": "https://example.com/updated-image.jpg",
    "createdAt": "2025-01-15T10:00:00",
    "updatedAt": "2025-01-15T11:00:00",
    "views": 6,
    "likes": 2
  }
}
```

---

### 3.5 게시글 삭제
```
DELETE /api/v1/posts/{postId}
Content-Type: application/json
```

**Request Body**:
```json
{
  "authorId": 1,
  "title": "",
  "content": "",
  "image": ""
}
```

**Example**:
```
DELETE /api/v1/posts/1
```

**Expected Response** (204 No Content):
```
(No content)
```

---

### 3.6 게시글 좋아요
```
PUT /api/v1/posts/{postId}/like?memberId={memberId}
```

**Query Parameters**:
- `memberId` (required): 좋아요를 누르는 회원 ID

**Example**:
```
PUT /api/v1/posts/1/like?memberId=2
```

**Expected Response** (200 OK):
```json
{
  "success": true,
  "message": "post_liked",
  "data": {
    "postId": 1,
    "likeCount": 3
  }
}
```

**Error Response** (409 Conflict - 이미 좋아요를 누른 경우):
```json
{
  "success": false,
  "message": "already_liked",
  "data": null
}
```

---

### 3.7 게시글 좋아요 취소
```
DELETE /api/v1/posts/{postId}/like?memberId={memberId}
```

**Query Parameters**:
- `memberId` (required): 좋아요를 취소하는 회원 ID

**Example**:
```
DELETE /api/v1/posts/1/like?memberId=2
```

**Expected Response** (200 OK):
```json
{
  "success": true,
  "message": "post_unliked",
  "data": {
    "postId": 1,
    "likeCount": 2
  }
}
```

**Error Response** (404 Not Found - 좋아요를 누르지 않은 경우):
```json
{
  "success": false,
  "message": "like_not_found",
  "data": null
}
```

---

## 4. 댓글 (Comment)

### 4.1 댓글 생성
```
POST /api/v1/posts/{postId}/comments
Content-Type: application/json
```

**Request Body**:
```json
{
  "authorId": 2,
  "content": "좋은 게시글이네요!"
}
```

**Example**:
```
POST /api/v1/posts/1/comments
```

**Expected Response** (201 Created):
```json
{
  "success": true,
  "message": "comment_created",
  "data": {
    "commentId": 1,
    "postId": 1,
    "author": {
      "memberId": 2,
      "nickname": "테스트유저2",
      "profileImage": "https://example.com/profile2.jpg"
    },
    "content": "좋은 게시글이네요!",
    "createdAt": "2025-01-15T11:00:00",
    "updatedAt": "2025-01-15T11:00:00"
  }
}
```

---

### 4.2 게시글의 댓글 목록 조회
```
GET /api/v1/posts/{postId}/comments?page=0&size=10
```

**Query Parameters**:
- `page` (optional, default: 0): 페이지 번호
- `size` (optional, default: 10): 페이지 크기

**Example**:
```
GET /api/v1/posts/1/comments?page=0&size=10
```

**Expected Response** (200 OK):
```json
{
  "success": true,
  "message": "comment_list_fetched",
  "data": {
    "postId": 1,
    "comments": [
      {
        "commentId": 1,
        "postId": 1,
        "author": {
          "memberId": 2,
          "nickname": "테스트유저2",
          "profileImage": "https://example.com/profile2.jpg"
        },
        "content": "좋은 게시글이네요!",
        "createdAt": "2025-01-15T11:00:00",
        "updatedAt": "2025-01-15T11:00:00"
      }
    ],
    "page": 0,
    "size": 10
  }
}
```

---

### 4.3 댓글 단건 조회
```
GET /api/v1/comments/{commentId}
```

**Example**:
```
GET /api/v1/comments/1
```

**Expected Response** (200 OK):
```json
{
  "success": true,
  "message": "comment_fetched",
  "data": {
    "commentId": 1,
    "postId": 1,
    "author": {
      "memberId": 2,
      "nickname": "테스트유저2",
      "profileImage": "https://example.com/profile2.jpg"
    },
    "content": "좋은 게시글이네요!",
    "createdAt": "2025-01-15T11:00:00",
    "updatedAt": "2025-01-15T11:00:00"
  }
}
```

---

### 4.4 댓글 수정
```
PATCH /api/v1/comments/{commentId}
Content-Type: application/json
```

**Request Body**:
```json
{
  "authorId": 2,
  "content": "수정된 댓글 내용입니다."
}
```

**Example**:
```
PATCH /api/v1/comments/1
```

**Expected Response** (200 OK):
```json
{
  "success": true,
  "message": "comment_updated",
  "data": {
    "commentId": 1,
    "postId": 1,
    "author": {
      "memberId": 2,
      "nickname": "테스트유저2",
      "profileImage": "https://example.com/profile2.jpg"
    },
    "content": "수정된 댓글 내용입니다.",
    "createdAt": "2025-01-15T11:00:00",
    "updatedAt": "2025-01-15T11:30:00"
  }
}
```

---

### 4.5 댓글 삭제
```
DELETE /api/v1/comments/{commentId}
Content-Type: application/json
```

**Request Body**:
```json
{
  "authorId": 2,
  "content": ""
}
```

**Example**:
```
DELETE /api/v1/comments/1
```

**Expected Response** (204 No Content):
```
(No content)
```

---

## 🔧 Postman 테스트 순서 추천

1. **회원가입** → 새 회원 생성
2. **로그인** → 토큰 발급 (현재는 미사용)
3. **회원 정보 수정** → 닉네임/프로필 이미지 변경
4. **비밀번호 변경** → 비밀번호 업데이트
5. **게시글 생성** → 테스트용 게시글 생성
6. **게시글 목록 조회** → 생성된 게시글 확인
7. **게시글 단건 조회** → 상세 정보 확인
8. **댓글 생성** → 게시글에 댓글 추가
9. **댓글 목록 조회** → 댓글 확인
10. **게시글 좋아요** → 좋아요 추가
11. **게시글 좋아요 취소** → 좋아요 제거
12. **댓글 수정** → 댓글 내용 변경
13. **게시글 수정** → 게시글 내용 변경
14. **댓글 삭제** → 댓글 제거
15. **게시글 삭제** → 게시글 제거
16. **로그아웃** → 세션 종료
17. **회원 탈퇴** → 회원 계정 삭제 (선택사항)

---

## 📝 참고사항

- 현재 JWT 인증이 미구현 상태이므로, `authorId`와 `memberId`를 요청 본문/파라미터로 전달합니다.
- 204 No Content 응답은 응답 본문이 없습니다.
- 모든 날짜는 ISO 8601 형식입니다.
- 에러 응답은 `success: false`와 적절한 `message`를 포함합니다.
