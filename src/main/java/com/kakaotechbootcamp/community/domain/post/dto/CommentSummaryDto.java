package com.kakaotechbootcamp.community.domain.post.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.Instant;

/**
 * QueryDSL Projection용 댓글 요약 DTO
 * 필요한 필드만 조회하여 N+1 문제 해결 및 성능 최적화
 */
@Getter
@AllArgsConstructor
public class CommentSummaryDto {
    private Long commentId;
    private Long postId;
    private String content;

    private Long memberId;
    private String memberNickname;
    private String memberProfileImage;

    private Instant createdAt;
    private Instant updatedAt;

}
