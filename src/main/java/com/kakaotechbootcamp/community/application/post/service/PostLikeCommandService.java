package com.kakaotechbootcamp.community.application.post.service;

import com.kakaotechbootcamp.community.application.post.dto.response.PostLikeResponse;
import com.kakaotechbootcamp.community.application.post.validator.PostLikePolicyValidator;
import com.kakaotechbootcamp.community.common.exception.CustomException;
import com.kakaotechbootcamp.community.common.exception.ErrorCode;
import com.kakaotechbootcamp.community.domain.post.entity.Post;
import com.kakaotechbootcamp.community.domain.post.entity.PostLike;
import com.kakaotechbootcamp.community.domain.post.repository.PostLikeRepository;
import com.kakaotechbootcamp.community.domain.post.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PostLikeCommandService {

    private final PostRepository postRepository;
    private final PostLikeRepository postLikeRepository;
    private final PostLikePolicyValidator postLikePolicyValidator;

    public PostLikeResponse likePost(Long postId, Long memberId) {
        Post post = findPostById(postId);
        postLikePolicyValidator.checkNotAlreadyLiked(postId, memberId);

        postLikeRepository.save(PostLike.create(postId, memberId));
        post.incrementLikes();

        return PostLikeResponse.of(postId, post.getLikeCount());
    }

    public PostLikeResponse unlikePost(Long postId, Long memberId) {
        Post post = findPostById(postId);
        postLikePolicyValidator.checkLikeExists(postId, memberId);

        postLikeRepository.deleteByPostIdAndMemberId(postId, memberId);
        post.decrementLikes();

        return PostLikeResponse.of(postId, post.getLikeCount());
    }

    private Post findPostById(Long postId) {
        return postRepository.findById(postId)
                .orElseThrow(() -> new CustomException(ErrorCode.POST_NOT_FOUND));
    }
}
