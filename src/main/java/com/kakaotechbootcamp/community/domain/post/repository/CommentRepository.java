package com.kakaotechbootcamp.community.domain.post.repository;

import com.kakaotechbootcamp.community.domain.post.entity.Comment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long>, CommentQueryRepository {

    long countByPostId(Long postId);

    List<Comment> findByPostId(Long postId);

    Page<Comment> findByPostId(Long postId, Pageable pageable);

    List<Comment> findByPostIdIn(List<Long> postIds);

}
