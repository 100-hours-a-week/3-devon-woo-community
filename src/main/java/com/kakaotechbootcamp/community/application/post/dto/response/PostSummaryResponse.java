package com.kakaotechbootcamp.community.application.post.dto.response;

import com.kakaotechbootcamp.community.application.member.dto.response.MemberResponse;
import com.kakaotechbootcamp.community.common.exception.CustomException;
import com.kakaotechbootcamp.community.common.exception.ErrorCode;
import com.kakaotechbootcamp.community.domain.member.entity.Member;
import com.kakaotechbootcamp.community.domain.post.entity.Post;

import java.time.LocalDateTime;

public record PostSummaryResponse(
        Long postId,
        String title,
        MemberResponse author,
        LocalDateTime createdAt,
        Long views,
        Long likes,
        Long commentsCount
) {
    public static PostSummaryResponse of(Post post, Member member , long commentsCount) {
        if (member == null) {
            throw new CustomException(ErrorCode.USER_NOT_FOUND);
        }
        return new PostSummaryResponse(
                post.getId(),
                post.getTitle(),
                MemberResponse.of(member),
                post.getCreatedAt(),
                post.getViewsCount(),
                post.getLikeCount(),
                commentsCount
        );
    }
}