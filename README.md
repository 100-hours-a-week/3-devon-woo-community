# 카카오테크 부트캠프 커뮤니티

Spring Boot 기반 커뮤니티 백엔드 API 프로젝트입니다.

## 기술 스택

### Backend
- Java 21
- Spring Boot 3.4.10
- Gradle
- Lombok

### 데이터 저장
- CSV 파일 기반 저장소 (GenericCsvStorage)
- 리플렉션을 활용한 제네릭 CRUD 구현
- 동시성 제어 (ReadWriteLock)

## 주요 기능

### 인증 (Auth)
- 회원가입
- 로그인/로그아웃

### 회원 (Member)
- 회원 정보 조회
- 프로필 수정 (닉네임, 프로필 이미지)
- 비밀번호 변경
- 회원 탈퇴

### 게시글 (Post)
- 게시글 작성/조회/수정/삭제
- 게시글 목록 조회 (페이지네이션)
- 조회수 자동 증가
- 좋아요/좋아요 취소

### 댓글 (Comment)
- 댓글 작성/조회/수정/삭제
- 게시글별 댓글 목록 조회 (페이지네이션)

## 프로젝트 구조

```
src/main/java/com/kakaotechbootcamp/community/
├── application/                 # 애플리케이션 계층
│   ├── auth/                   # 인증
│   │   ├── controller/         # AuthController
│   │   ├── dto/               # LoginRequest, SignupRequest 등
│   │   └── service/           # LoginService, SignupService
│   ├── comment/               # 댓글
│   │   ├── controller/        # CommentController
│   │   ├── dto/
│   │   │   ├── request/      # CommentCreateRequest, CommentUpdateRequest
│   │   │   └── response/     # CommentResponse, CommentListResponse
│   │   └── service/          # CommentService
│   ├── member/               # 회원
│   │   ├── controller/       # MemberController
│   │   ├── dto/
│   │   │   ├── request/      # MemberUpdateRequest, PasswordUpdateRequest
│   │   │   └── response/     # MemberResponse, MemberUpdateResponse
│   │   └── service/          # MemberService
│   └── post/                 # 게시글
│       ├── controller/       # PostController
│       ├── dto/
│       │   ├── request/      # PostCreateRequest, PostUpdateRequest
│       │   └── response/     # PostResponse, PostListResponse
│       └── service/          # PostService
│
├── domain/                    # 도메인 계층
│   ├── common/               # BaseEntity (생성/수정 시간 관리)
│   ├── member/
│   │   ├── entity/          # Member 엔티티
│   │   └── repository/      # MemberRepository, MemberRepositoryImpl
│   └── post/
│       ├── entity/          # Post, Comment, Attachment, PostLike
│       └── repository/      # PostRepository, CommentRepository 등
│
├── infra/                     # 인프라 계층
│   ├── csv/                  # GenericCsvStorage (CSV 저장소 구현)
│   └── repository/           # CrudStorage 인터페이스, CustomJpaRepository
│
└── common/                    # 공통 모듈
    ├── dto/api/              # ApiResponse (공통 응답 형식)
    ├── exception/            # CustomException, ErrorCode, GlobalExceptionHandler
    └── validation/           # ValidationPatterns (입력값 검증)
```

## API 엔드포인트

### 인증 (Auth)
- `POST /api/v1/auth/signup` - 회원가입
- `POST /api/v1/auth/login` - 로그인
- `POST /api/v1/auth/logout` - 로그아웃

### 회원 (Member)
- `PATCH /api/v1/members/{id}` - 회원 정보 수정
- `PATCH /api/v1/members/{id}/password` - 비밀번호 변경
- `DELETE /api/v1/members/{id}` - 회원 탈퇴

### 게시글 (Post)
- `POST /api/v1/posts` - 게시글 작성
- `GET /api/v1/posts` - 게시글 목록 조회
- `GET /api/v1/posts/{postId}` - 게시글 단건 조회
- `PATCH /api/v1/posts/{postId}` - 게시글 수정
- `DELETE /api/v1/posts/{postId}` - 게시글 삭제
- `PUT /api/v1/posts/{postId}/like` - 게시글 좋아요
- `DELETE /api/v1/posts/{postId}/like` - 게시글 좋아요 취소

### 댓글 (Comment)
- `POST /api/v1/posts/{postId}/comments` - 댓글 작성
- `GET /api/v1/posts/{postId}/comments` - 게시글의 댓글 목록 조회
- `GET /api/v1/comments/{commentId}` - 댓글 단건 조회
- `PATCH /api/v1/comments/{commentId}` - 댓글 수정
- `DELETE /api/v1/comments/{commentId}` - 댓글 삭제

자세한 API 명세는 [API_TEST.md](API_TEST.md)를 참조하세요.

## 아키텍처 특징

### 계층형 아키텍처
- **Application Layer**: Controller, Service, DTO
- **Domain Layer**: Entity, Repository Interface
- **Infrastructure Layer**: Repository Implementation, CSV Storage

### 제네릭 CSV 저장소
- 리플렉션을 활용한 범용 CSV CRUD 구현
- 동시성 제어를 위한 ReadWriteLock 사용
- BaseEntity를 통한 생성/수정 시간 자동 관리
- 자동 ID 생성 및 관리

### 공통 모듈
- 통일된 API 응답 형식 (ApiResponse)
- 전역 예외 처리 (GlobalExceptionHandler)
- 커스텀 예외 및 에러 코드 관리

## 실행 방법

### 빌드 및 실행
```bash
# 빌드
./gradlew build

# 실행
./gradlew bootRun
```

서버는 `http://localhost:8080`에서 실행됩니다.

### 데이터 저장
애플리케이션 실행 시 `data/` 디렉토리가 자동 생성되며, 다음 CSV 파일들이 생성됩니다:
- `member.csv` - 회원 데이터
- `post.csv` - 게시글 데이터
- `comment.csv` - 댓글 데이터
- `attachment.csv` - 첨부파일 데이터
- `postlike.csv` - 게시글 좋아요 데이터

## 테스트

Postman 등의 API 테스트 도구를 사용하여 테스트할 수 있습니다.
상세한 테스트 가이드는 [API_TEST.md](API_TEST.md)를 참조하세요.