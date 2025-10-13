package com.kakaotechbootcamp.community.application.comment.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Schema(description = "댓글 생성 요청 DTO")
public record CommentCreateRequest(
        @Schema(description = "작성자 ID", example = "1")
        @NotNull(message = "invalid_request")
        Long authorId, // TODO: JWT 도입 후 제거 예정

        @Schema(description = "댓글 내용", example = "This is a comment.")
        @NotBlank(message = "invalid_request")
        String content
) {}