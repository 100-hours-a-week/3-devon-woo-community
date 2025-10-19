package com.kakaotechbootcamp.community.application.post.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "게시글 좋아요 응답 DTO")
public record PostLikeResponse(
        @Schema(description = "게시글 ID", example = "1")
        Long postId,
        @Schema(description = "좋아요 수", example = "10")
        Long likeCount
) {
    public static PostLikeResponse of(Long postId, Long likeCount){
        return new PostLikeResponse(postId, likeCount);
    };
}
