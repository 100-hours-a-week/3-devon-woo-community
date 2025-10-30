package com.kakaotechbootcamp.community.application.post.validator;

import com.kakaotechbootcamp.community.common.exception.CustomException;
import com.kakaotechbootcamp.community.common.exception.code.PostErrorCode;
import com.kakaotechbootcamp.community.domain.post.repository.PostLikeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PostLikePolicyValidator {

    private final PostLikeRepository postLikeRepository;

    public void checkNotAlreadyLiked(Long postId, Long memberId) {
        if (postLikeRepository.existsByPostIdAndMemberId(postId, memberId)) {
            throw new CustomException(PostErrorCode.ALREADY_LIKED);
        }
    }

    public void checkLikeExists(Long postId, Long memberId) {
        if (!postLikeRepository.existsByPostIdAndMemberId(postId, memberId)) {
            throw new CustomException(PostErrorCode.LIKE_NOT_FOUND);
        }
    }
}
