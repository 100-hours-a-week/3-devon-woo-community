package com.kakaotechbootcamp.community.application.member.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import static com.kakaotechbootcamp.community.common.validation.ValidationPatterns.*;

@Schema(description = "회원 정보 수정 요청 DTO")
public record MemberUpdateRequest(
        @Schema(description = "새 닉네임", example = "new_devon")
        @Size(max = NICKNAME_MAX_LENGTH, message = "invalid_nickname")
        String nickname,

        @Schema(description = "새 프로필 이미지 URL", example = "https://picsum.photos/300")
        @Pattern(regexp = URL_PATTERN, message = "invalid_profile_image")
        String profileImage
) {}
