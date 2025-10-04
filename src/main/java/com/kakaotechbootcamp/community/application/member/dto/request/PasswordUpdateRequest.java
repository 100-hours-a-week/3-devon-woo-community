package com.kakaotechbootcamp.community.application.member.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import static com.kakaotechbootcamp.community.common.validation.ValidationPatterns.*;

public record PasswordUpdateRequest(
        @NotBlank(message = "missing_password")
        String currentPassword,

        @NotBlank(message = "invalid_password_format")
        @Size(min = PASSWORD_MIN_LENGTH, message = "invalid_password_format")
        String newPassword
) {}