package com.kakaotechbootcamp.community.application.test;

import com.kakaotechbootcamp.community.domain.post.entity.Comment;
import com.kakaotechbootcamp.community.domain.post.repository.CommentRepository;
import com.kakaotechbootcamp.community.domain.post.repository.impl.CommentRepositoryImpl;
import com.kakaotechbootcamp.community.common.dto.api.ApiResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * Comment 엔티티 테스트를 위한 컨트롤러
 * 개선된 SimpleJpaRepositoryImpl 아키텍처 검증
 */
@Slf4j
@RestController
@RequestMapping("/api/test/comments")
@RequiredArgsConstructor
public class CommentTestController {

    private final CommentRepository commentRepository;

    @GetMapping
    public ApiResponse<List<Comment>> getAllComments() {
        log.info("Getting all comments...");
        List<Comment> comments = commentRepository.findAll();
        log.info("Found {} comments", comments.size());
        return ApiResponse.success(comments);
    }

    @GetMapping("/{id}")
    public ApiResponse<Comment> getCommentById(@PathVariable Long id) {
        log.info("Getting comment by id: {}", id);
        return commentRepository.findById(id)
                .map(ApiResponse::success)
                .orElse(ApiResponse.failure("Comment not found with id: " + id));
    }

    @GetMapping("/count")
    public ApiResponse<Long> getCommentCount() {
        long count = commentRepository.count();
        log.info("Total comments count: {}", count);
        return ApiResponse.success(count);
    }

    @PostMapping
    public ApiResponse<Comment> createComment(@RequestBody Map<String, Object> request) {
        log.info("Creating new comment: {}", request);

        // Comment 생성 (Builder 패턴 사용)
        Comment comment = Comment.builder()
                .authorId(Long.valueOf(request.get("authorId").toString()))
                .postId(Long.valueOf(request.get("postId").toString()))
                .content(request.get("content").toString())
                .build();

        log.info("Before save - Comment: {}", comment);
        Comment savedComment = commentRepository.save(comment);
        log.info("After save - Comment: {}", savedComment);

        return ApiResponse.success(savedComment);
    }

    @PostMapping("/init")
    public ApiResponse<String> initTestData() {
        log.info("Initializing test comments...");

        // 테스트 댓글 생성 (포스트 ID 1, 2와 멤버 ID 10, 17 사용)
        Comment comment1 = Comment.builder()
                .authorId(10L)  // 기존 멤버 ID
                .postId(1L)     // 첫 번째 포스트
                .content("첫 번째 댓글입니다. 개선된 아키텍처가 잘 작동하네요!")
                .build();

        Comment comment2 = Comment.builder()
                .authorId(17L)  // 새로 생성한 멤버 ID
                .postId(1L)     // 첫 번째 포스트
                .content("두 번째 댓글입니다. Builder 패턴이 정말 깔끔하네요.")
                .build();

        Comment comment3 = Comment.builder()
                .authorId(10L)  // 기존 멤버 ID
                .postId(2L)     // 두 번째 포스트
                .content("함수형 프로그래밍과 Builder 패턴의 조합이 훌륭합니다!")
                .build();

        Comment savedComment1 = commentRepository.save(comment1);
        Comment savedComment2 = commentRepository.save(comment2);
        Comment savedComment3 = commentRepository.save(comment3);

        log.info("Test comments created: {}, {}, {}",
                savedComment1.getId(), savedComment2.getId(), savedComment3.getId());
        return ApiResponse.success("Test comments initialized successfully");
    }

    @DeleteMapping("/{id}")
    public ApiResponse<String> deleteComment(@PathVariable Long id) {
        log.info("Deleting comment with id: {}", id);
        commentRepository.deleteById(id);
        return ApiResponse.success("Comment deleted successfully");
    }
}