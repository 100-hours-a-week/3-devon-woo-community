package com.kakaotechbootcamp.community.domain.post.repository;

import com.kakaotechbootcamp.community.domain.post.entity.Comment;
import com.kakaotechbootcamp.community.infra.repository.CustomJpaRepository;

import java.util.List;

public interface CommentRepository extends CustomJpaRepository<Comment, Long> {

    long countByPostId(Long postId);

    List<Comment> findByPostId(Long postId);

}
