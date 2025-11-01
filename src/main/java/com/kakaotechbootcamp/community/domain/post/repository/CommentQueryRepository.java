package com.kakaotechbootcamp.community.domain.post.repository;

import com.kakaotechbootcamp.community.domain.post.dto.CommentSummaryDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Map;

public interface CommentQueryRepository {

    /**
     * 특정 게시글의 댓글 목록 조회 (Projection 사용 - 필요한 필드만)
     * fetch join 대신 필요한 컬럼만 SELECT하여 N+1 문제 해결 및 성능 최적화
     */
    Page<CommentSummaryDto> findByPostIdWithMemberAsDto(Long postId, Pageable pageable);

    /**
     * 여러 게시글의 댓글 수를 한 번의 쿼리로 조회
     * @return Map<PostId, CommentCount>
     */
    Map<Long, Long> countCommentsByPostIds(List<Long> postIds);

}
