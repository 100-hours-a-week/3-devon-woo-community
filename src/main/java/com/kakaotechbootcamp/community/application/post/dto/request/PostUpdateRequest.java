package com.kakaotechbootcamp.community.application.post.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

import static com.kakaotechbootcamp.community.common.validation.ValidationPatterns.*;

@Schema(description = "게시글 수정 요청 DTO")
public record PostUpdateRequest(
        @Schema(description = "작성자 ID", example = "1")
        @NotNull(message = "invalid_request")
        Long authorId, // TODO: JWT 도입 후 제거 예정

        @Schema(description = "수정할 게시글 제목", example = "This is an updated title.")
        @NotBlank(message = "invalid_request")
        String title,

        @Schema(description = "수정할 게시글 내용", example = "This is an updated content.")
        @NotBlank(message = "invalid_request")
        String content,

        @Schema(description = "수정할 이미지 URL", example = "https://picsum.photos/300")
        @Pattern(regexp = URL_PATTERN, message = "invalid_image_url")
        String image
) {}