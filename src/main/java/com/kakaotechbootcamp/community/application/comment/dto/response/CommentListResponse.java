package com.kakaotechbootcamp.community.application.comment.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CommentListResponse {

    private Long postId;
    private List<CommentResponse> items;
    private int page;
    private int size;
    private long totalElements;
    private int totalPages;
}