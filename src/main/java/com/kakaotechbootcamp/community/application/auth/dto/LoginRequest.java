package com.kakaotechbootcamp.community.application.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "로그인 요청 DTO")
public record LoginRequest(
        @Schema(description = "사용자 이메일", example = "devon@email.com")
        @NotBlank(message = "invalid_request")
        @Email(message = "invalid_email_format")
        String email,

        @Schema(description = "사용자 비밀번호", example = "password1234")
        @NotBlank(message = "invalid_request")
        String password
) {}
