package com.kakaotechbootcamp.community.application.member.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import static com.kakaotechbootcamp.community.common.validation.ValidationMessages.*;
import static com.kakaotechbootcamp.community.common.validation.ValidationPatterns.*;

public record PasswordUpdateRequest(
        @NotBlank(message = REQUIRED_PASSWORD)
        String currentPassword,

        @NotBlank(message = REQUIRED_FIELD)
        @Size(min = PASSWORD_MIN_LENGTH, message = INVALID_PASSWORD_FORMAT)
        String newPassword
) {}