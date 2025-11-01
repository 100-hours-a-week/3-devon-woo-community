package com.kakaotechbootcamp.community.domain.post.repository;

import com.kakaotechbootcamp.community.domain.post.dto.PostQueryDto;
import com.kakaotechbootcamp.community.domain.post.entity.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface PostQueryRepository {

    /**
     * 삭제되지 않은 게시글 목록 조회 (Projection 사용 - 필요한 필드만)
     * fetch join 대신 필요한 컬럼만 SELECT하여 성능 최적화
     */
    Page<PostQueryDto> findAllActiveWithMemberAsDto(Pageable pageable);

    /**
     * 제목 또는 내용으로 게시글 검색
     */
    Page<Post> searchByTitleOrContent(String keyword, Pageable pageable);
}
