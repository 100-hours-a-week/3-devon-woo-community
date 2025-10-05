package com.kakaotechbootcamp.community.application.comment.dto.response;

import com.kakaotechbootcamp.community.application.member.dto.response.MemberResponse;
import com.kakaotechbootcamp.community.domain.member.entity.Member;
import com.kakaotechbootcamp.community.domain.post.entity.Comment;

import java.time.LocalDateTime;

public record CommentResponse(
        Long commentId,
        Long postId,
        String content,
        MemberResponse author,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
    public static CommentResponse of(Comment comment, Member member) {
        return new CommentResponse(
                comment.getId(),
                comment.getPostId(),
                comment.getContent(),
                MemberResponse.of(member),
                comment.getCreatedAt(),
                comment.getUpdatedAt()
        );
    }
}