package com.kakaotechbootcamp.community.application.comment.service;

import com.kakaotechbootcamp.community.application.comment.dto.request.CommentCreateRequest;
import com.kakaotechbootcamp.community.application.comment.dto.request.CommentUpdateRequest;
import com.kakaotechbootcamp.community.application.comment.dto.response.CommentResponse;
import com.kakaotechbootcamp.community.application.common.dto.response.PageResponse;
import com.kakaotechbootcamp.community.application.common.validator.AccessPolicyValidator;
import com.kakaotechbootcamp.community.common.exception.CustomException;
import com.kakaotechbootcamp.community.common.exception.code.CommentErrorCode;
import com.kakaotechbootcamp.community.common.exception.code.MemberErrorCode;
import com.kakaotechbootcamp.community.common.exception.code.PostErrorCode;
import com.kakaotechbootcamp.community.domain.member.entity.Member;
import com.kakaotechbootcamp.community.domain.member.repository.MemberRepository;
import com.kakaotechbootcamp.community.domain.post.dto.CommentSummaryDto;
import com.kakaotechbootcamp.community.domain.post.entity.Comment;
import com.kakaotechbootcamp.community.domain.post.entity.Post;
import com.kakaotechbootcamp.community.domain.post.repository.CommentRepository;
import com.kakaotechbootcamp.community.domain.post.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;
    private final MemberRepository memberRepository;
    private final PostRepository postRepository;
    private final AccessPolicyValidator accessPolicyValidator;

    /**
     * 댓글 작성
     */
    public CommentResponse createComment(Long postId, CommentCreateRequest request, Long memberId) {
        Post post = findPostById(postId);
        Member member = findMemberById(memberId);

        Comment comment = Comment.create(member, post, request.content());
        commentRepository.save(comment);

        return CommentResponse.of(comment, member);
    }

    /**
     * 댓글 업데이튼
     */
    public CommentResponse updateComment(Long commentId, CommentUpdateRequest request, Long requesterId) {
        Comment comment = findCommentById(commentId);
        Member member = comment.getMember();

        accessPolicyValidator.checkAccess(comment.getMember().getId(), requesterId);

        comment.updateContent(request.content());
        commentRepository.save(comment);

        return CommentResponse.of(comment, member);
    }

    /**
     * 댓글 삭제
     */
    public void deleteComment(Long commentId, Long requesterId) {
        Comment comment = findCommentById(commentId);
        accessPolicyValidator.checkAccess(comment.getMember().getId(), requesterId);

        commentRepository.deleteById(comment.getId());
    }

    /**
     * 댓글 살세 조회
     */
    public CommentResponse getCommentsDetails(Long commentId) {
        Comment comment = findCommentById(commentId);
        Member member = comment.getMember();

        return CommentResponse.of(comment, member);
    }

    /**
     * 게시글의 댓글 페이지 조회 (+페이징 및 정렬)
     */
    public PageResponse<CommentResponse> getCommentPageByPostId(Long postId, Pageable pageable) {
        findPostById(postId);

        Page<CommentSummaryDto> commentDtoPage = commentRepository.findByPostIdWithMemberAsDto(postId, pageable);

        List<CommentResponse> commentResponses = commentDtoPage.getContent().stream()
                .map(CommentResponse::of)
                .toList();

        return PageResponse.of(commentResponses, commentDtoPage);
    }

    private Post findPostById(Long postId) {
        return postRepository.findById(postId)
                .orElseThrow(() -> new CustomException(PostErrorCode.POST_NOT_FOUND));
    }

    private Comment findCommentById(Long commentId) {
        return commentRepository.findById(commentId)
                .orElseThrow(() -> new CustomException(CommentErrorCode.COMMENT_NOT_FOUND));
    }

    private Member findMemberById(Long memberId) {
        return memberRepository.findById(memberId)
                .orElseThrow(() -> new CustomException(MemberErrorCode.USER_NOT_FOUND));
    }
}
