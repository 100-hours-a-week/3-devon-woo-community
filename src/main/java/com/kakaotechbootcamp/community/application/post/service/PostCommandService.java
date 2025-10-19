package com.kakaotechbootcamp.community.application.post.service;

import com.kakaotechbootcamp.community.application.post.dto.request.PostCreateRequest;
import com.kakaotechbootcamp.community.application.post.dto.request.PostUpdateRequest;
import com.kakaotechbootcamp.community.application.post.dto.response.PostLikeResponse;
import com.kakaotechbootcamp.community.application.post.dto.response.PostResponse;
import com.kakaotechbootcamp.community.common.exception.CustomException;
import com.kakaotechbootcamp.community.common.exception.ErrorCode;
import com.kakaotechbootcamp.community.application.validator.AccessPolicyValidator;
import com.kakaotechbootcamp.community.domain.member.entity.Member;
import com.kakaotechbootcamp.community.domain.member.repository.MemberRepository;
import com.kakaotechbootcamp.community.domain.post.entity.Attachment;
import com.kakaotechbootcamp.community.domain.post.entity.Post;
import com.kakaotechbootcamp.community.domain.post.entity.PostLike;
import com.kakaotechbootcamp.community.domain.post.repository.AttachmentRepository;
import com.kakaotechbootcamp.community.domain.post.repository.PostLikeRepository;
import com.kakaotechbootcamp.community.domain.post.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PostCommandService {
    private final PostRepository postRepository;
    private final MemberRepository memberRepository;
    private final AttachmentRepository attachmentRepository;
    private final PostLikeRepository postLikeRepository;
    private final AccessPolicyValidator accessPolicyValidator;

    public PostResponse createPost(PostCreateRequest request, Long authorId) {
        Member member = memberRepository.findById(authorId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        Post post = Post.create(authorId, request.title(), request.content());
        Attachment attachment = Attachment.create(post.getId(), request.image());
        Post savedPost = postRepository.save(post);
        Attachment savedAttachment = attachmentRepository.save(attachment);

        return PostResponse.of(savedPost, member, savedAttachment);
    }

    public PostResponse updatePost(Long postId, PostUpdateRequest request, Long authorId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new CustomException(ErrorCode.POST_NOT_FOUND));
        Member member = memberRepository.findById(post.getAuthorId())
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
        accessPolicyValidator.checkAccess(post.getAuthorId(), authorId);

        post.updatePost(request.title(), request.content());
        Post savedPost = postRepository.save(post);

        Attachment attachment = Optional.ofNullable(request.image())
                .map(img -> attachmentRepository.save(Attachment.create(postId, img)))
                .orElseGet(() -> attachmentRepository.findByPostId(postId).orElse(null));

        return PostResponse.of(savedPost, member, attachment);
    }

    public void deletePost(Long postId, Long authorId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new CustomException(ErrorCode.POST_NOT_FOUND));

        accessPolicyValidator.checkAccess(post.getAuthorId(), authorId);

        postRepository.deleteById(postId);
    }

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
