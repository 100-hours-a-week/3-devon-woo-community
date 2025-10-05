package com.kakaotechbootcamp.community.application.comment.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CommentCreateRequest(
        @NotNull(message = "invalid_request")
        Long postId,

        @NotBlank(message = "invalid_request")
        String content
) {}