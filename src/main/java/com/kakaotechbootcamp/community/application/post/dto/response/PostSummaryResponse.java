package com.kakaotechbootcamp.community.application.post.dto.response;

import com.kakaotechbootcamp.community.domain.post.entity.Post;

import java.time.LocalDateTime;

public record PostSummaryResponse(
        Long postId,
        String title,
        Long authorId,
        LocalDateTime createdAt,
        Long views,
        Long likes,
        Long commentsCount
) {
    public static PostSummaryResponse of(Post post) {
        return new PostSummaryResponse(
                post.getId(),
                post.getTitle(),
                post.getAuthorId(),
                post.getCreatedAt(),
                post.getViewsCount(),
                post.getLikeCount(),
                0L // TODO: 댓글 수 계산 로직 추가
        );
    }
}