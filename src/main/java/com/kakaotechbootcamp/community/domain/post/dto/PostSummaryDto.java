package com.kakaotechbootcamp.community.domain.post.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.Instant;

/**
 * QueryDSL Projection용 게시글 요약 DTO
 * 필요한 필드만 조회하여 성능 최적화
 */
@Getter
@AllArgsConstructor
public class PostSummaryDto {
    // Post 정보
    private Long postId;
    private String title;
    private Instant createdAt;
    private Long viewsCount;
    private Long likeCount;

    // Member 정보 (필요한 필드만)
    private Long memberId;
    private String memberNickname;
    private String memberEmail;
}
