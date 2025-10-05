package com.kakaotechbootcamp.community.application.post.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PostListResponse {

    private List<PostSummaryResponse> items;
    private int page;
    private int size;
    private long totalElements;
    private int totalPages;
}