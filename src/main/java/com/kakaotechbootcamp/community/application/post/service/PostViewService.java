package com.kakaotechbootcamp.community.application.post.service;

import com.kakaotechbootcamp.community.application.post.dto.ViewContext;
import com.kakaotechbootcamp.community.application.post.policy.ViewCountPolicy;
import com.kakaotechbootcamp.community.domain.post.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@RequiredArgsConstructor
public class PostViewService {

    private final PostRepository postRepository;
    private final ViewCountPolicy viewCountPolicy;

    /**
     * 조회수 증가
     */
    @Transactional
    public void incrementViewCount(Long postId, ViewContext context) {
        if (!viewCountPolicy.shouldCount(postId, context)) {
            return;
        }
        postRepository.incrementViewCount(postId);
    }
}
