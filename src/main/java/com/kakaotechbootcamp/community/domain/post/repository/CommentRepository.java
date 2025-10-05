package com.kakaotechbootcamp.community.domain.post.repository;

import com.kakaotechbootcamp.community.domain.post.entity.Comment;
import com.kakaotechbootcamp.community.infra.repository.CustomJpaRepository;

public interface CommentRepository extends CustomJpaRepository<Comment, Long> {
}
