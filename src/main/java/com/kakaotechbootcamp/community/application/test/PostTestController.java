package com.kakaotechbootcamp.community.application.test;

import com.kakaotechbootcamp.community.domain.member.entity.Member;
import com.kakaotechbootcamp.community.domain.member.repository.MemberRepository;
import com.kakaotechbootcamp.community.domain.member.repository.impl.MemberRepositoryImpl;
import com.kakaotechbootcamp.community.domain.post.entity.Post;
import com.kakaotechbootcamp.community.domain.post.repository.PostRepository;
import com.kakaotechbootcamp.community.domain.post.repository.impl.PostRepositoryImpl;
import com.kakaotechbootcamp.community.common.dto.api.ApiResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * Post 엔티티 테스트를 위한 컨트롤러
 * 개선된 SimpleJpaRepositoryImpl 아키텍처 검증
 */
@Slf4j
@RestController
@RequestMapping("/api/test/posts")
@RequiredArgsConstructor
public class PostTestController {

    private final PostRepository postRepository;
    private final MemberRepository memberRepository;

    @GetMapping
    public ApiResponse<List<Post>> getAllPosts() {
        log.info("Getting all posts...");
        List<Post> posts = postRepository.findAll();
        log.info("Found {} posts", posts.size());
        return ApiResponse.success(posts);
    }

    @GetMapping("/{id}")
    public ApiResponse<Post> getPostById(@PathVariable Long id) {
        log.info("Getting post by id: {}", id);
        return postRepository.findById(id)
                .map(ApiResponse::success)
                .orElse(ApiResponse.failure("Post not found with id: " + id));
    }

    @GetMapping("/count")
    public ApiResponse<Long> getPostCount() {
        long count = postRepository.count();
        log.info("Total posts count: {}", count);
        return ApiResponse.success(count);
    }

    @PostMapping
    public ApiResponse<Post> createPost(@RequestBody Map<String, Object> request) {
        log.info("Creating new post: {}", request);

        // authorId 직접 사용 (CSV 호환성을 위해)
        Long authorId = Long.valueOf(request.get("authorId").toString());

        // Post 생성 (Builder 패턴 사용)
        Post post = Post.builder()
                .authorId(authorId)  // Member 객체 대신 ID 직접 사용
                .title(request.get("title").toString())
                .content(request.get("content").toString())
                .viewsCount(0L)  // 기본값
                .likeCount(0L)   // 기본값
                .build();

        log.info("Before save - Post: {}", post);
        Post savedPost = postRepository.save(post);
        log.info("After save - Post: {}", savedPost);

        return ApiResponse.success(savedPost);
    }

    @PostMapping("/init")
    public ApiResponse<String> initTestData() {
        log.info("Initializing test posts...");

        // 첫 번째 멤버 ID를 작성자로 사용
        Member author = memberRepository.findAll().stream()
                .findFirst()
                .orElseThrow(() -> new RuntimeException("No members found. Create members first."));
        Long authorId = author.getId();

        // 테스트 포스트 생성
        Post post1 = Post.builder()
                .authorId(authorId)  // ID 사용
                .title("첫 번째 게시글")
                .content("이것은 개선된 SimpleJpaRepositoryImpl로 생성된 첫 번째 게시글입니다.")
                .viewsCount(0L)
                .likeCount(0L)
                .build();

        Post post2 = Post.builder()
                .authorId(authorId)  // ID 사용
                .title("두 번째 게시글")
                .content("Builder 패턴과 함수형 프로그래밍의 조합으로 만든 게시글입니다.")
                .viewsCount(5L)
                .likeCount(2L)
                .build();

        Post savedPost1 = postRepository.save(post1);
        Post savedPost2 = postRepository.save(post2);

        log.info("Test posts created: {} and {}", savedPost1.getId(), savedPost2.getId());
        return ApiResponse.success("Test posts initialized successfully");
    }

    @DeleteMapping("/{id}")
    public ApiResponse<String> deletePost(@PathVariable Long id) {
        log.info("Deleting post with id: {}", id);
        postRepository.deleteById(id);
        return ApiResponse.success("Post deleted successfully");
    }
}