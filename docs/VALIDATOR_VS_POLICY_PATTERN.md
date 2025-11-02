# Validator vs Policy 패턴 가이드

> DDD 기반 Spring Boot 프로젝트의 검증 레이어 분리 및 네이밍 컨벤션

---

## 1. 핵심 차이점

| 구분 | Validator | Policy |
|------|-----------|--------|
| **의도** | 값의 유효성 검증 | 비즈니스 정책 검증 |
| **관점** | "입력값이 유효한가?" | "행동이 허용되는가?" |
| **위치** | `application.[context].validator` | `domain.[context].policy` |
| **예외** | `ValidationException` | `PolicyViolationException` |

### 검증 흐름

```
Controller → Validator (입력 검증) → Application Service
         → Policy (정책 검증) → Entity (불변식 보장)
```

---

## 2. 패키지 구조

```
com.kakaotechbootcamp.community
├── application
│   └── post
│       ├── PostService.java
│       └── validator              ← 입력값 검증
│           └── PostCreateValidator.java
│
└── domain
    └── post
        ├── entity
        │   └── Post.java
        └── policy                  ← 비즈니스 규칙 검증
            ├── PostLikePolicy.java
            └── ViewCountPolicy.java
```

---

## 3. 네이밍 컨벤션

### Validator

| 항목 | 컨벤션 | 예시 |
|------|--------|------|
| **패키지** | `application.[context].validator` | `application.post.validator` |
| **클래스** | `*Validator` | `PostCreateValidator` |
| **메서드** | `validate()` | `validate(request)` |

```java
@Component
public class PostCreateValidator {
    public void validate(PostCreateRequest request) {
        if (request.getTitle().length() > 100) {
            throw new IllegalArgumentException("제목은 100자 이내여야 합니다.");
        }
    }
}
```

### Policy

| 항목 | 컨벤션 | 예시 |
|------|--------|------|
| **패키지** | `domain.[context].policy` | `domain.post.policy` |
| **클래스** | `*Policy` | `PostLikePolicy`, `ViewCountPolicy` |
| **메서드** | `validateCan[Action]()`, `should[Action]()` | `validateCanLike()`, `shouldCount()` |

```java
@Component
public class PostLikePolicy {
    public void validateCanLike(Post post, Member member) {
        if (post.hasLikedBy(member)) {
            throw new IllegalStateException("이미 좋아요를 누른 게시글입니다.");
        }
    }
}

@Component
public class ViewCountPolicy {
    public boolean shouldCount(Long postId, ViewContext context) {
        if (isBot(context.getUserAgent())) {
            return false;
        }
        return true;
    }
}
```

---

## 4. 실무 적용 예시

### 게시글 좋아요

```java
// Controller
@PostMapping("/posts/{postId}/like")
public ResponseEntity<Void> likePost(@PathVariable Long postId) {
    postService.likePost(postId, currentMember);
    return ResponseEntity.ok().build();
}

// Application Service
@Service
@RequiredArgsConstructor
public class PostService {
    private final PostLikeService postLikeService;

    @Transactional
    public void likePost(Long postId, Member member) {
        postLikeService.likePost(postId, member);
    }
}

// Application Service (Policy 사용)
@Service
@RequiredArgsConstructor
public class PostLikeService {
    private final PostRepository postRepository;
    private final MemberRepository memberRepository;
    private final PostLikeRepository postLikeRepository;
    private final PostLikePolicy postLikePolicy;

    @Transactional
    public void likePost(Long postId, Long memberId) {
        Post post = postRepository.findById(postId).orElseThrow();
        Member member = memberRepository.findById(memberId).orElseThrow();

        postLikePolicy.validateCanLike(post, member);  // 정책 검증

        postLikeRepository.save(PostLike.create(post, member));
        postRepository.incrementLikeCount(postId);
    }
}
```

---

## 5. 안티패턴

### ❌ Validator에서 도메인 상태 검증

```java
// 나쁜 예
@Component
public class PostUpdateValidator {
    public void validate(Long postId) {
        Post post = postRepository.findById(postId).orElseThrow();
        if (post.isDeleted()) {  // ❌ 도메인 상태 검증
            throw new IllegalStateException("삭제된 게시글입니다.");
        }
    }
}

// 좋은 예
@Component
public class PostEditPolicy {
    public void validateCanEdit(Post post, Member member) {
        if (post.isDeleted()) {  // ✅ Policy에서 검증
            throw new IllegalStateException("삭제된 게시글입니다.");
        }
    }
}
```

### ❌ Policy에서 입력값 형식 검증

```java
// 나쁜 예
@Component
public class PostLikePolicy {
    public void validateCanLike(Post post, String reason) {
        if (reason.length() > 100) {  // ❌ 입력값 검증
            throw new IllegalArgumentException("100자 이내여야 합니다.");
        }
    }
}

// 좋은 예
@Component
public class PostLikeValidator {
    public void validate(PostLikeRequest request) {
        if (request.getReason().length() > 100) {  // ✅ Validator에서 검증
            throw new IllegalArgumentException("100자 이내여야 합니다.");
        }
    }
}
```

---

## 요약

### Validator
- **위치**: `application.[context].validator`
- **역할**: 입력값 형식 검증 (길이, 필수값, 형식)
- **호출**: Application Service

### Policy
- **위치**: `domain.[context].policy`
- **역할**: 비즈니스 규칙 검증 (권한, 상태, 정책)
- **호출**: Domain Service

### 핵심 원칙

> **Validator로 입력 검증 → Policy로 비즈니스 규칙 검증 → Entity로 불변식 보장**
