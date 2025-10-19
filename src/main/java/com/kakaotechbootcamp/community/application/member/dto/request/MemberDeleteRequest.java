package com.kakaotechbootcamp.community.application.member.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "회원 탈퇴 요청 DTO")
public record MemberDeleteRequest(
        @Schema(description = "현재 비밀번호", example = "password1234")
        @NotBlank(message = "missing_password")
        String password
) {}