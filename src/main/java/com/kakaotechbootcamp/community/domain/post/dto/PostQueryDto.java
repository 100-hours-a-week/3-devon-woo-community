package com.kakaotechbootcamp.community.domain.post.dto;

import java.time.Instant;

/**
 * QueryDSL Projection용 게시글 요약 DTO
 * 필요한 필드만 조회하여 성능 최적화
 */
public record PostQueryDto(
        Long postId,
        String title,
        Instant createdAt,
        Long viewsCount,
        Long likeCount,
        Long memberId,
        String memberNickname,
        String memberEmail
) {
}
