package com.kakaotechbootcamp.community.domain.post.repository.impl;

import com.kakaotechbootcamp.community.domain.post.entity.Comment;
import com.kakaotechbootcamp.community.domain.post.repository.CommentRepository;
import com.kakaotechbootcamp.community.infra.repository.CustomJpaRepositoryImpl;
import org.springframework.stereotype.Repository;

/**
 * Comment Repository 구현체
 * SimpleJpaRepositoryImpl을 상속받아 Builder 패턴 지원
 */
@Repository
public class CommentRepositoryImpl extends CustomJpaRepositoryImpl<Comment, Long> implements CommentRepository {

    public CommentRepositoryImpl() {
        super(
                Comment::getId,
                (comment, id) -> {
                    if (comment.getId() != null) {
                        return comment;
                    }
                    return comment.withId(id);
                }
        );
    }
}