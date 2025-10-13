package com.kakaotechbootcamp.community.application.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import static com.kakaotechbootcamp.community.common.validation.ValidationPatterns.*;

@Schema(description = "회원가입 요청 DTO")
public record SignupRequest(
        @Schema(description = "사용자 이메일", example = "devon@email.com")
        @NotBlank(message = "invalid_request")
        @Email(message = "invalid_email_format")
        String email,

        @Schema(description = "사용자 비밀번호", example = "password1234")
        @NotBlank(message = "invalid_request")
        @Size(min = PASSWORD_MIN_LENGTH, message = "invalid_password_format")
        String password,

        @Schema(description = "사용자 닉네임", example = "devon")
        @NotBlank(message = "invalid_request")
        @Size(max = NICKNAME_MAX_LENGTH, message = "invalid_nickname")
        String nickname,

        @Schema(description = "프로필 이미지 URL", example = "https://picsum.photos/200")
        @Pattern(regexp = URL_PATTERN, message = "invalid_profile_image")
        String profileImage
) {}