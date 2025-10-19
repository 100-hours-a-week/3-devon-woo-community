package com.kakaotechbootcamp.community.application.member.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "회원 정보 수정 응답 DTO")
public record MemberUpdateResponse(
        @Schema(description = "수정된 닉네임", example = "new_devon")
        String nickname,
        @Schema(description = "수정된 프로필 이미지 URL", example = "https://picsum.photos/300")
        String profileImage
) {}
