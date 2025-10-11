package com.kakaotechbootcamp.community.application.post.dto.response;

import com.kakaotechbootcamp.community.application.member.dto.response.MemberResponse;
import com.kakaotechbootcamp.community.domain.member.entity.Member;
import com.kakaotechbootcamp.community.domain.post.entity.Attachment;
import com.kakaotechbootcamp.community.domain.post.entity.Post;

import java.time.LocalDateTime;

public record PostResponse(
        Long postId,
        MemberResponse author,
        String title,
        String content,
        String imageUrl,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        Long views,
        Long likes
) {
    public static PostResponse of(Post post, Member member, Attachment attachment) {
        return new PostResponse(
                post.getId(),
                MemberResponse.of(member),
                post.getTitle(),
                post.getContent(),
                attachment != null ? attachment.getAttachmentUrl() : null,
                post.getCreatedAt(),
                post.getUpdatedAt(),
                post.getViewsCount(),
                post.getLikeCount()
        );
    }
}