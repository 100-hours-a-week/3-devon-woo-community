package com.kakaotechbootcamp.community.domain.post.repository;

import com.kakaotechbootcamp.community.domain.post.entity.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

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
     * 게시글 ID로 조회 (Member와 함께 fetch join)
     */
    Optional<Post> findByIdWithMember(Long postId);

    /**
     * 특정 회원의 게시글 목록 조회
     */
    List<Post> findByMemberId(Long memberId);

    /**
     * 제목 또는 내용으로 게시글 검색
     */
    Page<Post> searchByTitleOrContent(String keyword, Pageable pageable);
}
