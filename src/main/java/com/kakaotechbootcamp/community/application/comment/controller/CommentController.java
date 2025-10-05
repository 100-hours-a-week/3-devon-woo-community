package com.kakaotechbootcamp.community.application.comment.controller;

import com.kakaotechbootcamp.community.application.comment.dto.request.CommentCreateRequest;
import com.kakaotechbootcamp.community.application.comment.dto.request.CommentUpdateRequest;
import com.kakaotechbootcamp.community.application.comment.dto.response.CommentListResponse;
import com.kakaotechbootcamp.community.application.comment.dto.response.CommentResponse;
import com.kakaotechbootcamp.community.common.dto.api.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/community")
public class CommentController {

    @PostMapping("/comment")
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<CommentResponse> createComment(
            @RequestBody @Validated CommentCreateRequest request
    ) {
        CommentResponse response = CommentResponse.builder()
                .commentId(456L)
                .postId(request.getPostId())
                .content(request.getContent())
                .createdAt(java.time.LocalDateTime.now())
                .build();

        return ApiResponse.success(response, "comment_created");
    }

    @PatchMapping("/comment/{commentId}")
    public ApiResponse<CommentResponse> updateComment(
            @PathVariable Long commentId,
            @RequestBody @Validated CommentUpdateRequest request
    ) {
        CommentResponse response = CommentResponse.builder()
                .commentId(commentId)
                .postId(123L)
                .content(request.getContent())
                .updatedAt(java.time.LocalDateTime.now())
                .build();

        return ApiResponse.success(response, "comment_updated");
    }

    @DeleteMapping("/comment/{commentId}")
    public ApiResponse<CommentResponse> deleteComment(@PathVariable Long commentId) {
        CommentResponse response = CommentResponse.builder()
                .commentId(commentId)
                .build();

        return ApiResponse.success(response, "comment_deleted");
    }

    @GetMapping("/post/{postId}")
    public ApiResponse<CommentListResponse> getCommentsByPost(
            @PathVariable Long postId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        CommentListResponse response = CommentListResponse.builder()
                .postId(postId)
                .items(java.util.List.of(
                    CommentResponse.builder()
                        .commentId(1L)
                        .content("좋은 글이네요!")
                        .author("user1")
                        .parentCommentId(null)
                        .createdAt(java.time.LocalDateTime.now())
                        .build()
                ))
                .page(page)
                .size(size)
                .totalElements(2L)
                .totalPages(1)
                .build();

        return ApiResponse.success(response, "comment_list_fetched");
    }
}