package com.kakaotechbootcamp.community.application.auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import static com.kakaotechbootcamp.community.common.validation.ValidationPatterns.*;

public record SignupRequest(
        @NotBlank(message = "invalid_request")
        @Email(message = "invalid_email_format")
        String email,

        @NotBlank(message = "invalid_request")
        @Size(min = PASSWORD_MIN_LENGTH, message = "invalid_password_format")
        String password,

        @NotBlank(message = "invalid_request")
        @Size(max = NICKNAME_MAX_LENGTH, message = "invalid_nickname")
        String nickname,

        @Pattern(regexp = URL_PATTERN, message = "invalid_profile_image")
        String profileImage
) {}