package com.kakaotechbootcamp.community.application.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "회원가입 응답 DTO")
public record SignupResponse(
        @Schema(description = "사용자 ID", example = "1")
        Long userId
) {}