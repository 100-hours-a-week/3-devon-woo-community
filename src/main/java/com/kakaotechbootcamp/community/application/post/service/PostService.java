package com.kakaotechbootcamp.community.application.post.service;

import com.kakaotechbootcamp.community.application.common.dto.response.PageResponse;
import com.kakaotechbootcamp.community.application.post.dto.request.PostCreateRequest;
import com.kakaotechbootcamp.community.application.post.dto.request.PostUpdateRequest;
import com.kakaotechbootcamp.community.application.post.dto.response.PostResponse;
import com.kakaotechbootcamp.community.application.post.dto.response.PostSummaryResponse;
import com.kakaotechbootcamp.community.domain.post.dto.PostSummaryDto;
import com.kakaotechbootcamp.community.application.common.validator.AccessPolicyValidator;
import com.kakaotechbootcamp.community.common.exception.CustomException;
import com.kakaotechbootcamp.community.common.exception.code.MemberErrorCode;
import com.kakaotechbootcamp.community.common.exception.code.PostErrorCode;
import com.kakaotechbootcamp.community.domain.member.entity.Member;
import com.kakaotechbootcamp.community.domain.member.repository.MemberRepository;
import com.kakaotechbootcamp.community.domain.post.entity.Attachment;
import com.kakaotechbootcamp.community.domain.post.entity.Post;
import com.kakaotechbootcamp.community.domain.post.repository.AttachmentRepository;
import com.kakaotechbootcamp.community.domain.post.repository.CommentRepository;
import com.kakaotechbootcamp.community.domain.post.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PostService {
    private final PostRepository postRepository;
    private final MemberRepository memberRepository;
    private final AttachmentRepository attachmentRepository;
    private final CommentRepository commentRepository;
    private final AccessPolicyValidator accessPolicyValidator;


    /**
     * 게시글 생성
     */
    @Transactional
    public PostResponse createPost(PostCreateRequest request, Long memberId) {
        Member member = findMemberById(memberId);

        Post post = Post.create(member, request.title(), request.content());
        Post savedPost = postRepository.save(post);
        Attachment attachment = Attachment.create(savedPost, request.image());
        Attachment savedAttachment = attachmentRepository.save(attachment);

        return PostResponse.of(savedPost, member, savedAttachment);
    }

    /**
     * 게시글 수정
     */
    @Transactional
    public PostResponse updatePost(Long postId, PostUpdateRequest request, Long memberId) {
        Post post = findByIdWithMember(postId);
        Member member = findMemberById(memberId);

        accessPolicyValidator.checkAccess(post.getMember().getId(), memberId);

        post.updatePost(request.title(), request.content());
        Post savedPost = postRepository.save(post);

        Attachment attachment = Optional.ofNullable(request.image())
                .map(img -> attachmentRepository.save(Attachment.create(savedPost, img)))
                .orElseGet(() -> attachmentRepository.findByPostId(postId).orElse(null));

        return PostResponse.of(savedPost, member, attachment);
    }

    /**
     * 게시글 삭제
     */
    @Transactional
    public void deletePost(Long postId, Long memberId) {
        Post post = findByIdWithMember(postId);
        accessPolicyValidator.checkAccess(post.getMember().getId(), memberId);

        postRepository.deleteById(postId);
    }

    /**
     * 게시글 조회
     */
    @Transactional(readOnly = true)
    public PostResponse getPostDetails(Long postId) {
        Post post = findByIdWithMember(postId);

        Member member = post.getMember();
        Attachment attachment = attachmentRepository.findByPostId(postId)
                .orElse(null);

        post.incrementViews();
        postRepository.save(post);

        return PostResponse.of(post, member, attachment);
    }

    /**
     * 게시글 페이지 조회 (+페이징 및 정렬)
     */
    @Transactional(readOnly = true)
    public PageResponse<PostSummaryResponse> getPostPage(Pageable pageable) {
        Page<PostSummaryDto> postDtoPage = postRepository.findAllActiveWithMemberAsDto(pageable);

        List<PostSummaryDto> postDtos = postDtoPage.getContent();
        List<Long> postIds = postDtos.stream()
                .map(PostSummaryDto::getPostId)
                .toList();

        Map<Long, Long> commentCountMap = commentRepository.countCommentsByPostIds(postIds);

        List<PostSummaryResponse> postSummaries = postDtos.stream()
                .map(dto -> PostSummaryResponse.fromDto(dto, commentCountMap.getOrDefault(dto.getPostId(), 0L)))
                .toList();

        return PageResponse.of(postSummaries, postDtoPage);
    }

    private Post findByIdWithMember(Long postId) {
        return postRepository.findByIdWithMember(postId)
                .orElseThrow(() -> new CustomException(PostErrorCode.POST_NOT_FOUND));
    }

    private Member findMemberById(Long memberId) {
        return memberRepository.findById(memberId)
                .orElseThrow(() -> new CustomException(MemberErrorCode.USER_NOT_FOUND));
    }
}
