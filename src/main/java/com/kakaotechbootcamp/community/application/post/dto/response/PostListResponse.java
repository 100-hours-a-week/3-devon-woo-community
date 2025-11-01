package com.kakaotechbootcamp.community.application.post.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.data.domain.Page;

import java.util.List;

@Schema(description = "게시글 목록 응답 DTO")
public record PostListResponse(
        @Schema(description = "게시글 목록")
        List<PostSummaryResponse> items,

        @Schema(description = "현재 페이지 번호 (0부터 시작)", example = "0")
        int page,

        @Schema(description = "페이지 크기", example = "20")
        int size,

        @Schema(description = "전체 게시글 수", example = "100")
        long totalElements,

        @Schema(description = "전체 페이지 수", example = "5")
        int totalPages
) {
    public static PostListResponse of(List<PostSummaryResponse> items, Page<?> page) {
        return new PostListResponse(
                items,
                page.getNumber(),
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages()
        );
    }
}