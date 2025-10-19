package com.kakaotechbootcamp.community.application.member.dto.response;

import com.kakaotechbootcamp.community.domain.member.entity.Member;

public record MemberUpdateResponse(
        String nickname,
        String profileImage
) {
    public static MemberUpdateResponse of(Member member) {
        return new MemberUpdateResponse(
                member.getNickname(),
                member.getProfileImageUrl()
        );
    }
}
