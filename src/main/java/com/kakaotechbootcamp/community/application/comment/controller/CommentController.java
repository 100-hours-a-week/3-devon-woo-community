package com.kakaotechbootcamp.community.application.comment.controller;

import com.kakaotechbootcamp.community.application.comment.dto.request.CommentCreateRequest;
import com.kakaotechbootcamp.community.application.comment.dto.request.CommentUpdateRequest;
import com.kakaotechbootcamp.community.application.comment.dto.response.CommentListResponse;
import com.kakaotechbootcamp.community.application.comment.dto.response.CommentResponse;
import com.kakaotechbootcamp.community.application.comment.service.CommentService;
import com.kakaotechbootcamp.community.common.dto.api.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;

    @PostMapping("/posts/{postId}/comments")
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<CommentResponse> createComment(
            @PathVariable Long postId,
            @RequestBody @Validated CommentCreateRequest request
    ) {
        Long authorId = 1L; // TODO: 인증된 사용자 ID로 변경
        CommentResponse response = commentService.createComment(postId, request, authorId);
        return ApiResponse.success(response, "comment_created");
    }

    @GetMapping("/posts/{postId}/comments")
    public ApiResponse<CommentListResponse> getCommentsByPost(
            @PathVariable Long postId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        CommentListResponse response = commentService.getCommentsByPostId(postId, page, size);
        return ApiResponse.success(response, "comment_list_fetched");
    }

    @GetMapping("/comments/{commentId}")
    public ApiResponse<CommentResponse> getComment(@PathVariable Long commentId) {
        CommentResponse response = commentService.getComment(commentId);
        return ApiResponse.success(response, "comment_fetched");
    }

    @PatchMapping("/comments/{commentId}")
    public ApiResponse<CommentResponse> updateComment(
            @PathVariable Long commentId,
            @RequestBody @Validated CommentUpdateRequest request
    ) {
        Long authorId = 1L; // TODO: 인증된 사용자 ID로 변경
        CommentResponse response = commentService.updateComment(commentId, request, authorId);
        return ApiResponse.success(response, "comment_updated");
    }

    @DeleteMapping("/comments/{commentId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteComment(@PathVariable Long commentId) {
        Long authorId = 1L; // TODO: 인증된 사용자 ID로 변경
        commentService.deleteComment(commentId, authorId);
    }
}