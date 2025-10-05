package com.kakaotechbootcamp.community.application.comment.dto.request;

import jakarta.validation.constraints.NotBlank;

public record CommentUpdateRequest(
        @NotBlank(message = "invalid_request")
        String content
) {}