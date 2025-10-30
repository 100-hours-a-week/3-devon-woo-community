package com.kakaotechbootcamp.community.domain.member.entity;

import com.kakaotechbootcamp.community.common.exception.CustomException;
import com.kakaotechbootcamp.community.common.exception.code.MemberErrorCode;
import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

@Entity
@Getter
@Builder(toBuilder = true)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@NoArgsConstructor
@Table(name = "member")
public class Member {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "email", unique = true)
    private String email;

    @Column(name = "password", length = 20)
    private String password;

    @Column(name = "nickname", length = 10)
    private String nickname;

    @Column(name = "profile_image_url", length = 500)
    private String profileImageUrl;

    @Column(name = "status")
    private MemberStatus status;

    @Column(name = "last_login_at")
    private Instant lastLoginAt;

    public static Member create(String email, String password, String nickname, String profileImageUrl) {
        return Member.builder()
                .email(email)
                .password(password)
                .nickname(nickname)
                .profileImageUrl(profileImageUrl)
                .status(MemberStatus.ACTIVE)
                .build();
    }

    public void updateProfile(String nickname, String profileImageUrl) {
        if (nickname == null && profileImageUrl == null) {
            throw new CustomException(MemberErrorCode.MEMBER_NO_PROFILE_UPDATE_DATA);
        }

        if (nickname != null) {
            validateNickname(nickname);
            this.nickname = nickname;
        }

        if (profileImageUrl != null) {
            validateProfileImageUrl(profileImageUrl);
            this.profileImageUrl = profileImageUrl;
        }
    }

    public void updateNickname(String nickname) {
        validateNickname(nickname);
        this.nickname = nickname;
    }

    public void updateProfileImage(String profileImageUrl) {
        validateProfileImageUrl(profileImageUrl);
        this.profileImageUrl = profileImageUrl;
    }

    private void validateNickname(String nickname) {
        if (nickname == null || nickname.trim().isEmpty()) {
            throw new CustomException(MemberErrorCode.MEMBER_NICKNAME_REQUIRED);
        }
        if (nickname.length() > 10) {
            throw new CustomException(MemberErrorCode.MEMBER_NICKNAME_TOO_LONG);
        }
    }

    private void validateProfileImageUrl(String profileImageUrl) {
        if (profileImageUrl != null && profileImageUrl.length() > 500) {
            throw new CustomException(MemberErrorCode.MEMBER_PROFILE_IMAGE_URL_TOO_LONG);
        }
    }

    public void updatePassword(String newPassword) {
        this.password = newPassword;
    }

    public void updateLastLoginTime() {
        this.lastLoginAt = Instant.now();
    }

    public void deactivate() {
        this.status = MemberStatus.INACTIVE;
    }

    public void reactivate() {
        this.status = MemberStatus.ACTIVE;
    }

    public void withdraw() {
        this.status = MemberStatus.WITHDRAWN;
    }

    public boolean isActive() {
        return this.status == MemberStatus.ACTIVE;
    }

    public boolean isWithdrawn() {
        return this.status == MemberStatus.WITHDRAWN;
    }

    public boolean canLogin() {
        return this.status == MemberStatus.ACTIVE;
    }
}
