package com.kakaotechbootcamp.community.application.post.validator;

import com.kakaotechbootcamp.community.common.exception.CustomException;
import com.kakaotechbootcamp.community.common.exception.ErrorCode;
import com.kakaotechbootcamp.community.domain.post.repository.PostLikeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PostLikePolicyValidator {

    private final PostLikeRepository postLikeRepository;

    public void checkNotAlreadyLiked(Long postId, Long memberId) {
        if (postLikeRepository.existsByPostIdAndMemberId(postId, memberId)) {
            throw new CustomException(ErrorCode.ALREADY_LIKED);
        }
    }

    public void checkLikeExists(Long postId, Long memberId) {
        if (!postLikeRepository.existsByPostIdAndMemberId(postId, memberId)) {
            throw new CustomException(ErrorCode.LIKE_NOT_FOUND);
        }
    }
}
