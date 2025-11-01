package com.kakaotechbootcamp.community.domain.post.repository;

import com.kakaotechbootcamp.community.domain.post.dto.PostSummaryDto;
import com.kakaotechbootcamp.community.domain.post.entity.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface PostQueryRepository {

    /**
     * 게시글 목록 조회 (Member와 함께 fetch join)
     */
    Page<Post> findAllWithMember(Pageable pageable);

    /**
     * 삭제되지 않은 게시글 목록 조회 (Member와 함께 fetch join)
     */
    Page<Post> findAllActiveWithMember(Pageable pageable);

    /**
     * 삭제되지 않은 게시글 목록 조회 (Projection 사용 - 필요한 필드만)
     * fetch join 대신 필요한 컬럼만 SELECT하여 성능 최적화
     */
    Page<PostSummaryDto> findAllActiveWithMemberAsDto(Pageable pageable);

    /**
     * 제목 또는 내용으로 게시글 검색
     */
    Page<Post> searchByTitleOrContent(String keyword, Pageable pageable);
}
