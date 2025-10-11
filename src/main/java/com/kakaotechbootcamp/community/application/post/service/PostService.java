package com.kakaotechbootcamp.community.application.post.service;

import com.kakaotechbootcamp.community.application.post.dto.request.PostCreateRequest;
import com.kakaotechbootcamp.community.application.post.dto.request.PostUpdateRequest;
import com.kakaotechbootcamp.community.application.post.dto.response.PostLikeResponse;
import com.kakaotechbootcamp.community.application.post.dto.response.PostListResponse;
import com.kakaotechbootcamp.community.application.post.dto.response.PostSummaryResponse;
import com.kakaotechbootcamp.community.application.post.dto.response.PostResponse;
import com.kakaotechbootcamp.community.common.exception.CustomException;
import com.kakaotechbootcamp.community.common.exception.ErrorCode;
import com.kakaotechbootcamp.community.domain.member.entity.Member;
import com.kakaotechbootcamp.community.domain.member.repository.MemberRepository;
import com.kakaotechbootcamp.community.domain.post.entity.Attachment;
import com.kakaotechbootcamp.community.domain.post.entity.Post;
import com.kakaotechbootcamp.community.domain.post.entity.PostLike;
import com.kakaotechbootcamp.community.domain.post.repository.AttachmentRepository;
import com.kakaotechbootcamp.community.domain.post.repository.CommentRepository;
import com.kakaotechbootcamp.community.domain.post.repository.PostLikeRepository;
import com.kakaotechbootcamp.community.domain.post.repository.PostRepository;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;
    private final MemberRepository memberRepository;
    private final AttachmentRepository attachmentRepository;
    private final CommentRepository commentRepository;
    private final PostLikeRepository postLikeRepository;

    public PostResponse createPost(PostCreateRequest request, Long authorId) {
        Member member = memberRepository.findById(authorId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        Post post = Post.createWithoutId(authorId, request.title(), request.content());
        Attachment attachment = Attachment.createWithoutId(post.getId(), request.image());
        Post savedPost = postRepository.save(post);
        Attachment savedAttachment = attachmentRepository.save(attachment);

        return PostResponse.of(savedPost, member, savedAttachment);
    }

    public PostResponse getPost(Long postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new CustomException(ErrorCode.POST_NOT_FOUND));
        Member member = memberRepository.findById(post.getAuthorId())
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
        Attachment attachment = attachmentRepository.findByPostId(postId)
                .orElse(null);

        post.incrementViews();
        postRepository.save(post);

        return PostResponse.of(post, member, attachment);
    }

    public PostListResponse getPosts(int page, int size) {
        List<Post> posts = postRepository.findAll();

        List<Long> authorIds = posts.stream()
                .map(Post::getAuthorId)
                .distinct()
                .toList();

        Map<Long, Member> memberMap = memberRepository.findAllById(authorIds).stream()
                .collect(Collectors.toMap(Member::getId, Function.identity()));

        Map<Long, Long> commentCountMap = posts.stream()
                .collect(Collectors.toMap(
                        Post::getId,
                        post -> commentRepository.countByPostId(post.getId())
                ));

        List<PostSummaryResponse> postSummaries = posts.stream()
                .map(post -> PostSummaryResponse.of(
                        post,
                        memberMap.get(post.getAuthorId()),
                        commentCountMap.getOrDefault(post.getId(), 0L)
                ))
                .toList();

        return PostListResponse.of(postSummaries, page, size);
    }

    public PostResponse updatePost(Long postId, PostUpdateRequest request, Long authorId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new CustomException(ErrorCode.POST_NOT_FOUND));
        Member member = memberRepository.findById(post.getAuthorId())
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
        validateAuthor(post, authorId);

        post.updatePost(request.title(), request.content());
        Post savedPost = postRepository.save(post);

        Attachment attachment = Optional.ofNullable(request.image())
                .map(img -> attachmentRepository.save(Attachment.createWithoutId(postId, img)))
                .orElseGet(() -> attachmentRepository.findByPostId(postId).orElse(null));
        long commentCount = commentRepository.countByPostId(postId);

        return PostResponse.of(savedPost, member, attachment);
    }

    public void deletePost(Long postId, Long authorId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new CustomException(ErrorCode.POST_NOT_FOUND));

        validateAuthor(post, authorId);

        postRepository.deleteById(postId);
    }

    public PostLikeResponse likePost(Long postId, Long memberId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new CustomException(ErrorCode.POST_NOT_FOUND));

        // 이미 좋아요를 눌렀는지 확인
        if (postLikeRepository.existsByPostIdAndMemberId(postId, memberId)) {
            throw new CustomException(ErrorCode.ALREADY_LIKED);
        }

        // PostLike 엔티티 생성 및 저장
        PostLike postLike = PostLike.createWithoutId(postId, memberId);
        postLikeRepository.save(postLike);

        // 좋아요 수 증가
        post.incrementLikes();
        postRepository.save(post);

        return PostLikeResponse.of(postId, post.getLikeCount());
    }

    public PostLikeResponse unlikePost(Long postId, Long memberId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new CustomException(ErrorCode.POST_NOT_FOUND));

        // 좋아요를 눌렀는지 확인
        if (!postLikeRepository.existsByPostIdAndMemberId(postId, memberId)) {
            throw new CustomException(ErrorCode.LIKE_NOT_FOUND);
        }

        // PostLike 엔티티 삭제
        postLikeRepository.deleteByPostIdAndMemberId(postId, memberId);

        // 좋아요 수 감소
        post.decrementLikes();
        postRepository.save(post);

        return PostLikeResponse.of(postId, post.getLikeCount());
    }

    private void validateAuthor(Post post, Long authorId) {
        if (!post.getAuthorId().equals(authorId)) {
            throw new CustomException(ErrorCode.NO_PERMISSION);
        }
    }

}