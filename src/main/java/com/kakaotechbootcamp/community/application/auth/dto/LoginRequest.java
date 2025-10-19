package com.kakaotechbootcamp.community.application.auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

import static com.kakaotechbootcamp.community.common.validation.ValidationMessages.*;

public record LoginRequest(
        @NotBlank(message = REQUIRED_FIELD)
        @Email(message = INVALID_EMAIL_FORMAT)
        String email,

        @NotBlank(message = REQUIRED_FIELD)
        String password
) {}
