package com.kakaotechbootcamp.community.application.comment.controller;

import com.kakaotechbootcamp.community.application.comment.dto.request.CommentCreateRequest;
import com.kakaotechbootcamp.community.application.comment.dto.request.CommentUpdateRequest;
import com.kakaotechbootcamp.community.application.comment.dto.response.CommentListResponse;
import com.kakaotechbootcamp.community.application.comment.dto.response.CommentResponse;
import com.kakaotechbootcamp.community.application.comment.service.CommentService;
import com.kakaotechbootcamp.community.common.dto.api.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Comment", description = "댓글 관련 API")
@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;

    @Operation(summary = "댓글 생성")
    @PostMapping("/posts/{postId}/comments")
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<CommentResponse> createComment(
            @PathVariable Long postId,
            @RequestBody @Validated CommentCreateRequest request
    ) {
        Long authorId = request.authorId(); // TODO: JWT 도입 후 CurrentUser로 변경
        CommentResponse response = commentService.createComment(postId, request, authorId);
        return ApiResponse.success(response, "comment_created");
    }

    @Operation(summary = "게시글의 댓글 목록 조회")
    @GetMapping("/posts/{postId}/comments")
    public ApiResponse<CommentListResponse> getCommentsByPost(
            @PathVariable Long postId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        CommentListResponse response = commentService.getCommentsByPostId(postId, page, size);
        return ApiResponse.success(response, "comment_list_fetched");
    }

    @Operation(summary = "댓글 단건 조회")
    @GetMapping("/comments/{commentId}")
    public ApiResponse<CommentResponse> getComment(@PathVariable Long commentId) {
        CommentResponse response = commentService.getComment(commentId);
        return ApiResponse.success(response, "comment_fetched");
    }

    @Operation(summary = "댓글 수정")
    @PatchMapping("/comments/{commentId}")
    public ApiResponse<CommentResponse> updateComment(
            @PathVariable Long commentId,
            @RequestBody @Validated CommentUpdateRequest request
    ) {
        Long authorId = request.authorId(); // TODO: JWT 도입 후 CurrentUser로 변경
        CommentResponse response = commentService.updateComment(commentId, request, authorId);
        return ApiResponse.success(response, "comment_updated");
    }

    @Operation(summary = "댓글 삭제")
    @DeleteMapping("/comments/{commentId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteComment(
            @PathVariable Long commentId,
            @RequestBody @Validated CommentUpdateRequest request
    ) {
        Long authorId = request.authorId(); // TODO: JWT 도입 후 CurrentUser로 변경
        commentService.deleteComment(commentId, authorId);
    }
}