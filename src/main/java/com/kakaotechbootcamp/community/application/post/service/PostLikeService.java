package com.kakaotechbootcamp.community.application.post.service;

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
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PostLikeService {

    private final PostRepository postRepository;
    private final PostLikeRepository postLikeRepository;
    private final MemberRepository memberRepository;
    private final PostLikePolicyValidator postLikePolicyValidator;

    /**
     * 게시글 좋아요
     */
    @Transactional
    public void likePost(Long postId, Long memberId) {
        Post post = findPostById(postId);
        Member member = findMemberById(memberId);

        postLikePolicyValidator.checkNotAlreadyLiked(postId, memberId);

        postLikeRepository.save(PostLike.create(post, member));
        postRepository.incrementLikeCount(postId);
    }

    /**
     * 게시글 좋아요 취소
     */
    @Transactional
    public void unlikePost(Long postId, Long memberId) {
        findPostById(postId); // 게시글 존재 여부 확인
        postLikePolicyValidator.checkLikeExists(postId, memberId);

        postLikeRepository.deleteByPostIdAndMemberId(postId, memberId);
        postRepository.decrementLikeCount(postId);
    }

    private Post findPostById(Long postId) {
        return postRepository.findByIdWithMember(postId)
                .orElseThrow(() -> new CustomException(PostErrorCode.POST_NOT_FOUND));
    }

    private Member findMemberById(Long memberId) {
        return memberRepository.findById(memberId)
                .orElseThrow(() -> new CustomException(MemberErrorCode.USER_NOT_FOUND));
    }
}
