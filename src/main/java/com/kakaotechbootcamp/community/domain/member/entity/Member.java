package com.kakaotechbootcamp.community.domain.member.entity;

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
            throw new IllegalArgumentException("업데이트할 프로필 정보가 없습니다.");
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
            throw new IllegalArgumentException("닉네임은 필수입니다.");
        }
        if (nickname.length() > 10) {
            throw new IllegalArgumentException("닉네임은 10자를 초과할 수 없습니다.");
        }
    }

    private void validateProfileImageUrl(String profileImageUrl) {
        if (profileImageUrl != null && profileImageUrl.length() > 500) {
            throw new IllegalArgumentException("프로필 이미지 URL은 500자를 초과할 수 없습니다.");
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
