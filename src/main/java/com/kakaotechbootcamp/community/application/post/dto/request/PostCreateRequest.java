package com.kakaotechbootcamp.community.application.post.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

import static com.kakaotechbootcamp.community.common.validation.ValidationPatterns.*;

public record PostCreateRequest(
        @NotBlank(message = "invalid_request")
        String title,

        @NotBlank(message = "invalid_request")
        String content,

        @Pattern(regexp = URL_PATTERN, message = "invalid_image_url")
        String image
) {}