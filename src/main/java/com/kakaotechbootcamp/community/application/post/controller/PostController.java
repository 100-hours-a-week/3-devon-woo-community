package com.kakaotechbootcamp.community.application.post.controller;

import com.kakaotechbootcamp.community.application.post.dto.request.PostCreateRequest;
import com.kakaotechbootcamp.community.application.post.dto.request.PostUpdateRequest;
import com.kakaotechbootcamp.community.application.post.dto.response.PostLikeResponse;
import com.kakaotechbootcamp.community.application.post.dto.response.PostListResponse;
import com.kakaotechbootcamp.community.application.post.dto.response.PostResponse;
import com.kakaotechbootcamp.community.application.post.service.PostCommandService;
import com.kakaotechbootcamp.community.application.post.service.PostQueryService;
import com.kakaotechbootcamp.community.common.dto.api.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/posts")
@RequiredArgsConstructor
public class PostController {

    private final PostCommandService postCommandService;
    private final PostQueryService postQueryService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<PostResponse> createPost(
            @RequestBody @Validated PostCreateRequest request
    ) {
        Long authorId = request.authorId(); // TODO: JWT 도입 후 CurrentUser로 변경
        PostResponse response = postCommandService.createPost(request, authorId);
        return ApiResponse.success(response, "post_created");
    }

    @DeleteMapping("/{postId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deletePost(
            @PathVariable Long postId,
            @RequestBody @Validated PostUpdateRequest request
    ) {
        Long authorId = request.authorId(); // TODO: JWT 도입 후 CurrentUser로 변경
        postCommandService.deletePost(postId, authorId);
    }

    @GetMapping("/{postId}")
    public ApiResponse<PostResponse> getPost(@PathVariable Long postId) {
        PostResponse response = postQueryService.getPost(postId);
        return ApiResponse.success(response, "post_retrieved");
    }

    @GetMapping
    public ApiResponse<PostListResponse> getPosts(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        PostListResponse response = postQueryService.getPosts(page, size);
        return ApiResponse.success(response, "posts_retrieved");
    }

    @PostMapping("/{postId}/like")
    public ApiResponse<PostLikeResponse> likePost(
            @PathVariable Long postId,
            @RequestParam Long memberId // TODO: JWT 도입 후 CurrentUser로 변경
    ) {
        PostLikeResponse response = postCommandService.likePost(postId, memberId);
        return ApiResponse.success(response, "post_liked");
    }

    @DeleteMapping("/{postId}/like")
    public ApiResponse<PostLikeResponse> unlikePost(
            @PathVariable Long postId,
            @RequestParam Long memberId // TODO: JWT 도입 후 CurrentUser로 변경
    ) {
        PostLikeResponse response = postCommandService.unlikePost(postId, memberId);
        return ApiResponse.success(response, "post_unliked");
    }
}