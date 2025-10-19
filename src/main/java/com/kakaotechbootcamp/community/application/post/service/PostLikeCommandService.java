package com.kakaotechbootcamp.community.application.post.service;

import com.kakaotechbootcamp.community.application.post.dto.response.PostLikeResponse;
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


    public PostLikeResponse likePost(Long postId, Long memberId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new CustomException(ErrorCode.POST_NOT_FOUND));

        if (postLikeRepository.existsByPostIdAndMemberId(postId, memberId)) {
            throw new CustomException(ErrorCode.ALREADY_LIKED);
        }

        PostLike postLike = PostLike.create(postId, memberId);
        postLikeRepository.save(postLike);

        post.incrementLikes();
        postRepository.save(post);

        return PostLikeResponse.of(postId, post.getLikeCount());
    }

    public PostLikeResponse unlikePost(Long postId, Long memberId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new CustomException(ErrorCode.POST_NOT_FOUND));

        if (!postLikeRepository.existsByPostIdAndMemberId(postId, memberId)) {
            throw new CustomException(ErrorCode.LIKE_NOT_FOUND);
        }

        postLikeRepository.deleteByPostIdAndMemberId(postId, memberId);

        post.decrementLikes();
        postRepository.save(post);

        return PostLikeResponse.of(postId, post.getLikeCount());
    }
}
