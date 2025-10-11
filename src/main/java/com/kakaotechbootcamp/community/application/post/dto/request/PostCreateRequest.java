package com.kakaotechbootcamp.community.application.post.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

import static com.kakaotechbootcamp.community.common.validation.ValidationPatterns.*;

public record PostCreateRequest(
        @NotNull(message = "invalid_request")
        Long authorId, // TODO: JWT 도입 후 제거 예정

        @NotBlank(message = "invalid_request")
        String title,

        @NotBlank(message = "invalid_request")
        String content,

        @Pattern(regexp = URL_PATTERN, message = "invalid_image_url")
        String image
) {}