package com.kakaotechbootcamp.community.application.post.dto.response;

public record PostLikeResponse(
        Long postId,
        Long likeCount
) {
    public static PostLikeResponse of(Long postId, Long likeCount){
        return new PostLikeResponse(postId, likeCount);
    };
}
