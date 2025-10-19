package com.kakaotechbootcamp.community.application.comment.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import static com.kakaotechbootcamp.community.common.validation.ValidationMessages.*;

public record CommentUpdateRequest(
        @NotNull(message = REQUIRED_AUTHOR_ID)
        Long authorId, // TODO: JWT 도입 후 제거 예정

        @NotBlank(message = REQUIRED_COMMENT_CONTENT)
        String content
) {}