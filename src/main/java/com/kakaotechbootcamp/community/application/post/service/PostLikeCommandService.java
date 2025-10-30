package com.kakaotechbootcamp.community.application.post.service;

import com.kakaotechbootcamp.community.application.post.dto.response.PostLikeResponse;
import com.kakaotechbootcamp.community.application.post.validator.PostLikePolicyValidator;
import com.kakaotechbootcamp.community.common.exception.CustomException;
import com.kakaotechbootcamp.community.common.exception.code.MemberErrorCode;
import com.kakaotechbootcamp.community.common.exception.code.PostErrorCode;
import com.kakaotechbootcamp.community.domain.member.entity.Member;
import com.kakaotechbootcamp.community.domain.member.repository.MemberRepository;
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
    private final MemberRepository memberRepository;
    private final PostLikePolicyValidator postLikePolicyValidator;

    public PostLikeResponse likePost(Long postId, Long memberId) {
        Post post = findPostById(postId);
        Member member = findMemberById(memberId);
        postLikePolicyValidator.checkNotAlreadyLiked(postId, memberId);

        postLikeRepository.save(PostLike.create(post, member));
        post.incrementLikes();
        postRepository.save(post);

        return PostLikeResponse.of(postId, post.getLikeCount());
    }

    public PostLikeResponse unlikePost(Long postId, Long memberId) {
        Post post = findPostById(postId);
        postLikePolicyValidator.checkLikeExists(postId, memberId);

        postLikeRepository.deleteByPostIdAndMemberId(postId, memberId);
        post.decrementLikes();
        postRepository.save(post);

        return PostLikeResponse.of(postId, post.getLikeCount());
    }

    private Post findPostById(Long postId) {
        return postRepository.findById(postId)
                .orElseThrow(() -> new CustomException(PostErrorCode.POST_NOT_FOUND));
    }

    private Member findMemberById(Long memberId) {
        return memberRepository.findById(memberId)
                .orElseThrow(() -> new CustomException(MemberErrorCode.USER_NOT_FOUND));
    }
}
