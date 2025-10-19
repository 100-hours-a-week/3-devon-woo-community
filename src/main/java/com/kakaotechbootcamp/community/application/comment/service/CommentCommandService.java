package com.kakaotechbootcamp.community.application.comment.service;

import com.kakaotechbootcamp.community.application.comment.dto.request.CommentCreateRequest;
import com.kakaotechbootcamp.community.application.comment.dto.request.CommentUpdateRequest;
import com.kakaotechbootcamp.community.application.comment.dto.response.CommentResponse;
import com.kakaotechbootcamp.community.common.exception.CustomException;
import com.kakaotechbootcamp.community.common.exception.ErrorCode;
import com.kakaotechbootcamp.community.application.validator.AccessPolicyValidator;
import com.kakaotechbootcamp.community.domain.member.entity.Member;
import com.kakaotechbootcamp.community.domain.member.repository.MemberRepository;
import com.kakaotechbootcamp.community.domain.post.entity.Comment;
import com.kakaotechbootcamp.community.domain.post.entity.Post;
import com.kakaotechbootcamp.community.domain.post.repository.CommentRepository;
import com.kakaotechbootcamp.community.domain.post.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CommentCommandService {

    private final CommentRepository commentRepository;
    private final MemberRepository memberRepository;
    private final PostRepository postRepository;
    private final AccessPolicyValidator accessPolicyValidator;

    public CommentResponse createComment(Long postId, CommentCreateRequest request, Long authorId) {
        findPostById(postId);
        Member author = findMemberById(authorId);

        Comment comment = Comment.create(postId, authorId, request.content());
        commentRepository.save(comment);

        return CommentResponse.of(comment, author);
    }

    public CommentResponse updateComment(Long commentId, CommentUpdateRequest request, Long requesterId) {
        Comment comment = findCommentById(commentId);
        Member author = findMemberById(comment.getAuthorId());

        accessPolicyValidator.checkAccess(comment.getAuthorId(), requesterId);

        comment.updateContent(request.content());
        commentRepository.save(comment);

        return CommentResponse.of(comment, author);
    }

    public void deleteComment(Long commentId, Long requesterId) {
        Comment comment = findCommentById(commentId);
        accessPolicyValidator.checkAccess(comment.getAuthorId(), requesterId);

        commentRepository.deleteById(comment.getId());
    }

    private Post findPostById(Long postId) {
        return postRepository.findById(postId)
                .orElseThrow(() -> new CustomException(ErrorCode.POST_NOT_FOUND));
    }

    private Comment findCommentById(Long commentId) {
        return commentRepository.findById(commentId)
                .orElseThrow(() -> new CustomException(ErrorCode.COMMENT_NOT_FOUND));
    }

    private Member findMemberById(Long memberId) {
        return memberRepository.findById(memberId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
    }
}

