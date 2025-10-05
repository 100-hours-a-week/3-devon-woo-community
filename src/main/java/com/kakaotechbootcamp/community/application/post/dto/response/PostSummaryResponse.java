package com.kakaotechbootcamp.community.application.post.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PostSummaryResponse {

    private Long postId;
    private String title;
    private Long authorId;
    private LocalDateTime createdAt;
    private Long views;
    private Long likes;
    private Long commentsCount;
}