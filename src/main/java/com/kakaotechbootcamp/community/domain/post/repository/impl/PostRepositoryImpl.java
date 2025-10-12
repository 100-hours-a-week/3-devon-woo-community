package com.kakaotechbootcamp.community.domain.post.repository.impl;

import com.kakaotechbootcamp.community.domain.post.entity.Post;
import com.kakaotechbootcamp.community.domain.post.repository.PostRepository;
import com.kakaotechbootcamp.community.infra.repository.CustomJpaRepositoryImpl;
import org.springframework.stereotype.Repository;

@Repository
public class PostRepositoryImpl extends CustomJpaRepositoryImpl<Post, Long> implements PostRepository {

    public PostRepositoryImpl() {
        super(Post::getId);
    }
}