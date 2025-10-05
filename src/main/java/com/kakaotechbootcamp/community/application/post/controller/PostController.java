package com.kakaotechbootcamp.community.application.post.controller;

import com.kakaotechbootcamp.community.application.post.dto.request.PostCreateRequest;
import com.kakaotechbootcamp.community.application.post.dto.request.PostUpdateRequest;
import com.kakaotechbootcamp.community.application.post.dto.response.PostListResponse;
import com.kakaotechbootcamp.community.application.post.dto.response.PostResponse;
import com.kakaotechbootcamp.community.common.dto.api.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/posts")
public class PostController {

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<PostResponse> createPost(
            @RequestBody @Validated PostCreateRequest request
    ) {
        PostResponse response = PostResponse.builder()
                .postId(123L)
                .title(request.getTitle())
                .content(request.getContent())
                .image(request.getImage())
                .updatedAt(java.time.LocalDateTime.now())
                .build();

        return ApiResponse.success(response);
    }

    @PatchMapping("/{postId}")
    public ApiResponse<PostResponse> updatePost(
            @PathVariable Long postId,
            @RequestBody @Validated PostUpdateRequest request
    ) {
        PostResponse response = PostResponse.builder()
                .postId(postId)
                .title(request.getTitle())
                .content(request.getContent())
                .image(request.getImage())
                .createdAt(java.time.LocalDateTime.now().minusHours(1))
                .updatedAt(java.time.LocalDateTime.now())
                .build();

        return ApiResponse.success(response);
    }

    @DeleteMapping("/{postId}")
    public ApiResponse<PostResponse> deletePost(@PathVariable Long postId) {
        PostResponse response = PostResponse.builder()
                .postId(postId)
                .build();

        return ApiResponse.success(response, "post_deleted");
    }

    @GetMapping("/{postId}")
    public ApiResponse<PostResponse> getPost(@PathVariable Long postId) {
        PostResponse response = PostResponse.builder()
                .postId(postId)
                .authorId(10L)
                .title("게시글 제목")
                .content("게시글 내용")
                .createdAt(java.time.LocalDateTime.now().minusHours(2))
                .updatedAt(java.time.LocalDateTime.now().minusMinutes(30))
                .views(123L)
                .likes(45L)
                .commentCount(6L)
                .build();

        return ApiResponse.success(response, "post_retrieved");
    }

    @GetMapping
    public ApiResponse<PostListResponse> getPosts(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        PostListResponse response = PostListResponse.builder()
                .items(java.util.List.of(
                    com.kakaotechbootcamp.community.application.post.dto.response.PostSummaryResponse.builder()
                        .postId(1L)
                        .title("게시글 제목 예시")
                        .authorId(10L)
                        .createdAt(java.time.LocalDateTime.now())
                        .views(1024L)
                        .likes(56L)
                        .commentsCount(6L)
                        .build()
                ))
                .page(page)
                .size(size)
                .totalElements(345L)
                .totalPages(18)
                .build();

        return ApiResponse.success(response, "posts_retrieved");
    }

    @PutMapping("/{postId}/like")
    public ApiResponse<PostResponse> likePost(@PathVariable Long postId) {
        PostResponse response = PostResponse.builder()
                .postId(postId)
                .likes(15L)
                .build();

        return ApiResponse.success(response, "post_liked");
    }

    @DeleteMapping("/{postId}/like")
    public ApiResponse<PostResponse> unlikePost(@PathVariable Long postId) {
        PostResponse response = PostResponse.builder()
                .postId(postId)
                .likes(14L)
                .build();

        return ApiResponse.success(response, "post_unliked");
    }
}