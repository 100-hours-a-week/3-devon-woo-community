package com.kakaotechbootcamp.community.application.post.dto.response;

import com.kakaotechbootcamp.community.domain.post.entity.Post;

import java.util.List;

public record PostListResponse(
        List<PostSummaryResponse> items,
        int page,
        int size,
        long totalElements,
        int totalPages
) {
    public static PostListResponse of(List<PostSummaryResponse> posts, int page, int size) {

        return new PostListResponse(
                posts,
                page,
                size,
                (long) posts.size(),
                (int) Math.ceil((double) posts.size() / size)
        );
    }
}