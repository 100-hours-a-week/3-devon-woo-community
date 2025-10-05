package com.kakaotechbootcamp.community.application.post.dto.response;

import com.kakaotechbootcamp.community.domain.post.entity.Post;

import java.time.LocalDateTime;

public record PostResponse(
        Long postId,
        Long authorId,
        String title,
        String content,
        String image,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        Long views,
        Long likes,
        Long commentCount
) {
    public static PostResponse of(Post post) {
        return new PostResponse(
                post.getId(),
                post.getAuthorId(),
                post.getTitle(),
                post.getContent(),
                null, // image는 현재 Post 엔티티에 없음
                post.getCreatedAt(),
                post.getUpdatedAt(),
                post.getViewsCount(),
                post.getLikeCount(),
                0L // TODO: 댓글 수 계산 로직 추가
        );
    }
}