package com.kakaotechbootcamp.community.application.member.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import static com.kakaotechbootcamp.community.common.validation.ValidationPatterns.*;

@Schema(description = "비밀번호 변경 요청 DTO")
public record PasswordUpdateRequest(
        @Schema(description = "현재 비밀번호", example = "password1234")
        @NotBlank(message = "missing_password")
        String currentPassword,

        @Schema(description = "새 비밀번호", example = "new_password1234")
        @NotBlank(message = "invalid_password_format")
        @Size(min = PASSWORD_MIN_LENGTH, message = "invalid_password_format")
        String newPassword
) {}