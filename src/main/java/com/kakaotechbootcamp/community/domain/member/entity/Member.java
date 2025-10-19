package com.kakaotechbootcamp.community.domain.member.entity;

import lombok.*;

@Getter
@Builder(toBuilder = true)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@NoArgsConstructor
public class Member {

    private Long id;

    private String email;

    private String password;

    private String nickname;

    private String profileImageUrl;

    public static Member create(String email, String password, String nickname, String profileImageUrl) {
        return Member.builder()
                .email(email)
                .password(password)
                .nickname(nickname)
                .profileImageUrl(profileImageUrl)
                .build();
    }

    public void updateProfile(String nickname, String profileImageUrl) {
        this.nickname = nickname != null ? nickname : this.nickname;
        this.profileImageUrl = profileImageUrl != null ? profileImageUrl : this.profileImageUrl;
    }

    public void updatePassword(String newPassword) {
        this.password = newPassword;
    }
}
