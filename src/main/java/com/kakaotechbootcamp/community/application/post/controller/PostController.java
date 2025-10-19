package com.kakaotechbootcamp.community.application.post.controller;

import com.kakaotechbootcamp.community.application.post.dto.request.PostCreateRequest;
import com.kakaotechbootcamp.community.application.post.dto.request.PostUpdateRequest;
import com.kakaotechbootcamp.community.application.post.dto.response.PostLikeResponse;
import com.kakaotechbootcamp.community.application.post.dto.response.PostListResponse;
import com.kakaotechbootcamp.community.application.post.dto.response.PostResponse;
import com.kakaotechbootcamp.community.application.post.service.PostService;
import com.kakaotechbootcamp.community.common.dto.api.ApiResponse;
import com.kakaotechbootcamp.community.common.swagger.CustomExceptionDescription;
import com.kakaotechbootcamp.community.common.swagger.SwaggerResponseDescription;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Post", description = "게시글 관련 API")
@RestController
@RequestMapping("/api/v1/posts")
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;

    @Operation(summary = "게시글 생성", description = "새로운 게시글을 작성합니다.")
    @CustomExceptionDescription(SwaggerResponseDescription.POST_CREATE)
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<PostResponse> createPost(
            @RequestBody @Validated PostCreateRequest request
    ) {
        Long authorId = request.authorId(); // TODO: JWT 도입 후 CurrentUser로 변경
        PostResponse response = postService.createPost(request, authorId);
        return ApiResponse.success(response, "post_created");
    }

    @Operation(summary = "게시글 수정", description = "기존 게시글을 수정합니다.")
    @CustomExceptionDescription(SwaggerResponseDescription.POST_UPDATE)
    @PatchMapping("/{postId}")
    public ApiResponse<PostResponse> updatePost(
            @Parameter(description = "게시글 ID") @PathVariable Long postId,
            @RequestBody @Validated PostUpdateRequest request
    ) {
        Long authorId = request.authorId(); // TODO: JWT 도입 후 CurrentUser로 변경
        PostResponse response = postService.updatePost(postId, request, authorId);
        return ApiResponse.success(response, "post_updated");
    }

    @Operation(summary = "게시글 삭제", description = "게시글을 삭제합니다.")
    @CustomExceptionDescription(SwaggerResponseDescription.POST_DELETE)
    @DeleteMapping("/{postId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deletePost(
            @Parameter(description = "게시글 ID") @PathVariable Long postId,
            @RequestBody @Validated PostUpdateRequest request
    ) {
        Long authorId = request.authorId(); // TODO: JWT 도입 후 CurrentUser로 변경
        postService.deletePost(postId, authorId);
    }

    @Operation(summary = "게시글 단건 조회", description = "특정 게시글의 상세 정보를 조회합니다.")
    @CustomExceptionDescription(SwaggerResponseDescription.POST_GET)
    @GetMapping("/{postId}")
    public ApiResponse<PostResponse> getPost(@Parameter(description = "게시글 ID") @PathVariable Long postId) {
        PostResponse response = postService.getPost(postId);
        return ApiResponse.success(response, "post_retrieved");
    }

    @Operation(summary = "게시글 목록 조회", description = "게시글 목록을 페이징하여 조회합니다.")
    @CustomExceptionDescription(SwaggerResponseDescription.POST_LIST)
    @GetMapping
    public ApiResponse<PostListResponse> getPosts(
            @Parameter(description = "페이지 번호") @RequestParam(defaultValue = "1") int page,
            @Parameter(description = "페이지 크기") @RequestParam(defaultValue = "20") int size
    ) {
        PostListResponse response = postService.getPosts(page, size);
        return ApiResponse.success(response, "posts_retrieved");
    }

    @Operation(summary = "게시글 좋아요", description = "게시글에 좋아요를 추가합니다.")
    @CustomExceptionDescription(SwaggerResponseDescription.POST_LIKE)
    @PostMapping("/{postId}/like")
    public ApiResponse<PostLikeResponse> likePost(
            @Parameter(description = "게시글 ID") @PathVariable Long postId,
            @Parameter(description = "회원 ID") @RequestParam Long memberId // TODO: JWT 도입 후 CurrentUser로 변경
    ) {
        PostLikeResponse response = postService.likePost(postId, memberId);
        return ApiResponse.success(response, "post_liked");
    }

    @Operation(summary = "게시글 좋아요 취소", description = "게시글의 좋아요를 취소합니다.")
    @CustomExceptionDescription(SwaggerResponseDescription.POST_UNLIKE)
    @DeleteMapping("/{postId}/like")
    public ApiResponse<PostLikeResponse> unlikePost(
            @Parameter(description = "게시글 ID") @PathVariable Long postId,
            @Parameter(description = "회원 ID") @RequestParam Long memberId // TODO: JWT 도입 후 CurrentUser로 변경
    ) {
        PostLikeResponse response = postService.unlikePost(postId, memberId);
        return ApiResponse.success(response, "post_unliked");
    }
}