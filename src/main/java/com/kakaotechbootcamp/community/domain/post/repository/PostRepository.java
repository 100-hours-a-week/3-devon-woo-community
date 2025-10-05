package com.kakaotechbootcamp.community.domain.post.repository;

import com.kakaotechbootcamp.community.domain.post.entity.Post;
import com.kakaotechbootcamp.community.infra.repository.CustomJpaRepository;

public interface PostRepository extends CustomJpaRepository<Post, Long> {
}
