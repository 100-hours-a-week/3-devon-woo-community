package com.kakaotechbootcamp.community.application.comment.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CommentResponse {

    private Long commentId;
    private Long postId;
    private String content;
    private String author;
    private Long parentCommentId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}