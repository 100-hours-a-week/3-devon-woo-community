package com.kakaotechbootcamp.community.application.comment.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CommentUpdateRequest(
        @NotNull(message = "invalid_request")
        Long authorId, // TODO: JWT 도입 후 제거 예정

        @NotBlank(message = "invalid_request")
        String content
) {}