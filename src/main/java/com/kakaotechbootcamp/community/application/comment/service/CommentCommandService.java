package com.kakaotechbootcamp.community.application.comment.service;

import com.kakaotechbootcamp.community.application.comment.dto.request.CommentCreateRequest;
import com.kakaotechbootcamp.community.application.comment.dto.request.CommentUpdateRequest;
import com.kakaotechbootcamp.community.application.comment.dto.response.CommentResponse;
import com.kakaotechbootcamp.community.common.exception.CustomException;
import com.kakaotechbootcamp.community.common.exception.ErrorCode;
import com.kakaotechbootcamp.community.domain.member.entity.Member;
import com.kakaotechbootcamp.community.domain.member.repository.MemberRepository;
import com.kakaotechbootcamp.community.domain.post.entity.Comment;
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

    public CommentResponse createComment(Long postId, CommentCreateRequest request, Long authorId) {
        postRepository.findById(postId)
                .orElseThrow(() -> new CustomException(ErrorCode.POST_NOT_FOUND));
        Member member = memberRepository.findById(authorId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        Comment comment = Comment.create(postId, authorId, request.content());
        Comment savedComment = commentRepository.save(comment);

        return CommentResponse.of(savedComment, member);
    }

    public CommentResponse updateComment(Long commentId, CommentUpdateRequest request, Long authorId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new CustomException(ErrorCode.COMMENT_NOT_FOUND));
        Member member = memberRepository.findById(comment.getAuthorId())
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        validateAuthor(comment, authorId);

        comment.updateContent(request.content());
        Comment savedComment = commentRepository.save(comment);

        return CommentResponse.of(savedComment, member);
    }

    public void deleteComment(Long commentId, Long authorId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new CustomException(ErrorCode.COMMENT_NOT_FOUND));

        validateAuthor(comment, authorId);

        commentRepository.deleteById(commentId);
    }

    private void validateAuthor(Comment comment, Long authorId) {
        if (!comment.getAuthorId().equals(authorId)) {
            throw new CustomException(ErrorCode.NO_PERMISSION);
        }
    }
}
