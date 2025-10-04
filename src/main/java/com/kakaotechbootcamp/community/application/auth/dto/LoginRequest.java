package com.kakaotechbootcamp.community.application.auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record LoginRequest(
        @NotBlank(message = "invalid_request")
        @Email(message = "invalid_email_format")
        String email,

        @NotBlank(message = "invalid_request")
        String password
) {}
