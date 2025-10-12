package com.kakaotechbootcamp.community.domain.post.repository.impl;

import com.kakaotechbootcamp.community.domain.post.entity.Comment;
import com.kakaotechbootcamp.community.domain.post.repository.CommentRepository;
import com.kakaotechbootcamp.community.infra.repository.CustomJpaRepositoryImpl;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Comment Repository 구현체
 * SimpleJpaRepositoryImpl을 상속받아 Builder 패턴 지원
 */
@Repository
public class CommentRepositoryImpl extends CustomJpaRepositoryImpl<Comment, Long> implements CommentRepository {

    public CommentRepositoryImpl() {
        super(Comment::getId);
    }

    @Override
    public long countByPostId(Long postId) {
        return findAll().stream()
                .filter(comment -> comment.getPostId().equals(postId))
                .count();
    }

    @Override
    public List<Comment> findByPostId(Long postId) {
        return findAll().stream()
                .filter(comment -> comment.getPostId().equals(postId))
                .toList();
    }
}