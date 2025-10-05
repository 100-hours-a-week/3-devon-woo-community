package com.kakaotechbootcamp.community.application.comment.dto.response;

import java.util.List;

public record CommentListResponse(
        Long postId,
        List<CommentResponse> items,
        int page,
        int size,
        long totalElements,
        int totalPages
) {
    public static CommentListResponse of(Long postId, List<CommentResponse> comments, int page, int size) {
        return new CommentListResponse(
                postId,
                comments,
                page,
                size,
                (long) comments.size(),
                (int) Math.ceil((double) comments.size() / size)
        );
    }
}