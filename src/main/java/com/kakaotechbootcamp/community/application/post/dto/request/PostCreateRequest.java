package com.kakaotechbootcamp.community.application.post.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

import static com.kakaotechbootcamp.community.common.validation.ValidationMessages.*;
import static com.kakaotechbootcamp.community.common.validation.ValidationPatterns.*;

public record PostCreateRequest(
        @NotNull(message = REQUIRED_AUTHOR_ID)
        Long authorId, // TODO: JWT 도입 후 제거 예정

        @NotBlank(message = REQUIRED_POST_TITLE)
        String title,

        @NotBlank(message = REQUIRED_POST_CONTENT)
        String content,

        @Pattern(regexp = URL_PATTERN, message = INVALID_IMAGE_URL)
        String image
) {}