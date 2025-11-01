package com.kakaotechbootcamp.community.application.post.dto.response;

import com.kakaotechbootcamp.community.application.member.dto.response.MemberResponse;
import com.kakaotechbootcamp.community.domain.post.dto.PostQueryDto;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.Instant;

@Schema(description = "게시글 요약 응답 DTO")
public record PostSummaryResponse(
        @Schema(description = "게시글 ID", example = "1")
        Long postId,
        @Schema(description = "게시글 제목", example = "This is a title.")
        String title,
        @Schema(description = "작성자 정보")
        MemberResponse member,
        @Schema(description = "생성 시각")
        Instant createdAt,
        @Schema(description = "조회수", example = "100")
        Long views,
        @Schema(description = "좋아요 수", example = "10")
        Long likes,
        @Schema(description = "댓글 수", example = "5")
        Long commentsCount
) {
    public static PostSummaryResponse fromDto(PostQueryDto dto, long commentsCount) {
        return new PostSummaryResponse(
                dto.postId(),
                dto.title(),
                new MemberResponse(
                        dto.memberId(),
                        dto.memberNickname(),
                        dto.memberEmail()
                ),
                dto.createdAt(),
                dto.viewsCount(),
                dto.likeCount(),
                commentsCount
        );
    }
}