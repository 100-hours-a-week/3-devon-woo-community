package com.kakaotechbootcamp.community.application.comment.service;

import com.kakaotechbootcamp.community.application.comment.dto.request.CommentCreateRequest;
import com.kakaotechbootcamp.community.application.comment.dto.request.CommentUpdateRequest;
import com.kakaotechbootcamp.community.application.comment.dto.response.CommentListResponse;
import com.kakaotechbootcamp.community.application.comment.dto.response.CommentResponse;
import com.kakaotechbootcamp.community.common.exception.CustomException;
import com.kakaotechbootcamp.community.common.exception.ErrorCode;
import com.kakaotechbootcamp.community.domain.member.entity.Member;
import com.kakaotechbootcamp.community.domain.member.repository.MemberRepository;
import com.kakaotechbootcamp.community.domain.post.entity.Comment;
import com.kakaotechbootcamp.community.domain.post.entity.Post;
import com.kakaotechbootcamp.community.domain.post.repository.CommentRepository;
import com.kakaotechbootcamp.community.domain.post.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;
    private final MemberRepository memberRepository;
    private final PostRepository postRepository;

    public CommentResponse createComment(CommentCreateRequest request, Long authorId) {
        Post post = postRepository.findById(request.postId())
                .orElseThrow(() -> new CustomException(ErrorCode.POST_NOT_FOUND));
        Member member = memberRepository.findById(authorId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        Comment comment = Comment.createWithoutId(request.postId(), authorId, request.content());
        Comment savedComment = commentRepository.save(comment);

        return CommentResponse.of(savedComment, member);
    }

    public CommentResponse getComment(Long commentId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new CustomException(ErrorCode.COMMENT_NOT_FOUND));
        Member member = memberRepository.findById(comment.getAuthorId())
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        return CommentResponse.of(comment, member);
    }

    public CommentListResponse getCommentsByPostId(Long postId, int page, int size) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new CustomException(ErrorCode.POST_NOT_FOUND));

        List<Comment> comments = commentRepository.findByPostId(postId);

        List<Long> authorIds = comments.stream()
                .map(Comment::getAuthorId)
                .distinct()
                .toList();

        Map<Long, Member> memberMap = memberRepository.findAllById(authorIds).stream()
                .collect(Collectors.toMap(Member::getId, Function.identity()));

        List<CommentResponse> commentResponses = comments.stream()
                .map(comment -> {
                    Member member = memberMap.get(comment.getAuthorId());
                    if (member == null) {
                        throw new CustomException(ErrorCode.USER_NOT_FOUND);
                    }
                    return CommentResponse.of(comment, member);
                })
                .toList();

        return CommentListResponse.of(postId, commentResponses, page, size);
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