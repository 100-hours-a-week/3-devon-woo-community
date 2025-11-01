package com.kakaotechbootcamp.community.domain.post.repository;

import com.kakaotechbootcamp.community.domain.post.entity.Comment;

import java.util.List;
import java.util.Map;

public interface CommentQueryRepository {

    /**
     * 특정 게시글의 댓글 목록 조회 (Member와 함께 fetch join)
     */
    List<Comment> findByPostIdWithMember(Long postId);

    /**
     * 여러 게시글의 댓글 수를 한 번의 쿼리로 조회
     * @return Map<PostId, CommentCount>
     */
    Map<Long, Long> countCommentsByPostIds(List<Long> postIds);

    /**
     * 특정 회원의 댓글 목록 조회
     */
    List<Comment> findByMemberId(Long memberId);
}
