package com.kakaotechbootcamp.community.application.member.dto.response;

import com.kakaotechbootcamp.community.domain.member.entity.Member;

public record MemberResponse(
        Long memberId,
        String nickname,
        String profileImage
) {
    public static MemberResponse of(Member member) {
        return new MemberResponse(
                member.getId(),
                member.getNickname(),
                member.getProfileImageUrl()
        );
    }
}
