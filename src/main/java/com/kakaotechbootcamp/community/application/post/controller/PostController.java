package com.kakaotechbootcamp.community.application.post.controller;

import com.kakaotechbootcamp.community.application.post.dto.request.PostCreateRequest;
import com.kakaotechbootcamp.community.application.post.dto.request.PostUpdateRequest;
import com.kakaotechbootcamp.community.application.post.dto.response.PostLikeResponse;
import com.kakaotechbootcamp.community.application.post.dto.response.PostListResponse;
import com.kakaotechbootcamp.community.application.post.dto.response.PostResponse;
import com.kakaotechbootcamp.community.application.post.service.PostService;
import com.kakaotechbootcamp.community.common.dto.api.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/posts")
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<PostResponse> createPost(
            @RequestBody @Validated PostCreateRequest request
    ) {
        Long authorId = request.authorId(); // TODO: JWT 도입 후 CurrentUser로 변경
        PostResponse response = postService.createPost(request, authorId);
        return ApiResponse.success(response, "post_created");
    }

    @PatchMapping("/{postId}")
    public ApiResponse<PostResponse> updatePost(
            @PathVariable Long postId,
            @RequestBody @Validated PostUpdateRequest request
    ) {
        Long authorId = request.authorId(); // TODO: JWT 도입 후 CurrentUser로 변경
        PostResponse response = postService.updatePost(postId, request, authorId);
        return ApiResponse.success(response, "post_updated");
    }

    @DeleteMapping("/{postId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deletePost(
            @PathVariable Long postId,
            @RequestBody @Validated PostUpdateRequest request
    ) {
        Long authorId = request.authorId(); // TODO: JWT 도입 후 CurrentUser로 변경
        postService.deletePost(postId, authorId);
    }

    @GetMapping("/{postId}")
    public ApiResponse<PostResponse> getPost(@PathVariable Long postId) {
        PostResponse response = postService.getPost(postId);
        return ApiResponse.success(response, "post_retrieved");
    }

    @GetMapping
    public ApiResponse<PostListResponse> getPosts(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        PostListResponse response = postService.getPosts(page, size);
        return ApiResponse.success(response, "posts_retrieved");
    }

    @PutMapping("/{postId}/like")
    public ApiResponse<PostLikeResponse> likePost(
            @PathVariable Long postId,
            @RequestParam Long memberId // TODO: JWT 도입 후 CurrentUser로 변경
    ) {
        PostLikeResponse response = postService.likePost(postId, memberId);
        return ApiResponse.success(response, "post_liked");
    }

    @DeleteMapping("/{postId}/like")
    public ApiResponse<PostLikeResponse> unlikePost(
            @PathVariable Long postId,
            @RequestParam Long memberId // TODO: JWT 도입 후 CurrentUser로 변경
    ) {
        PostLikeResponse response = postService.unlikePost(postId, memberId);
        return ApiResponse.success(response, "post_unliked");
    }
}