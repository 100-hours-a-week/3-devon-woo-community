package com.kakaotechbootcamp.community.domain.member.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
public class Member {

    private Long id;

    private String email;

    private String password;

    private String nickname;

    private String profileImageUrl;

    public Member withId(Long id) {
        return this.toBuilder().id(id).build();
    }

    public static Member createWithoutId(String email, String password, String nickname, String profileImageUrl) {
        return Member.builder()
                .email(email)
                .password(password)
                .nickname(nickname)
                .profileImageUrl(profileImageUrl)
                .build();
    }
}
