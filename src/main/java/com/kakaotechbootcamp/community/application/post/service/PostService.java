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
import com.kakaotechbootcamp.community.domain.post.repository.AttachmentRepository;
import com.kakaotechbootcamp.community.domain.post.repository.CommentRepository;
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

    public PostResponse createPost(PostCreateRequest request, Long authorId) {
        Member member = memberRepository.findById(authorId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        Post post = Post.createWithoutId(authorId, request.title(), request.content());
        Post savedPost = postRepository.save(post);

        return PostResponse.of(savedPost, member, null);
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

    public PostLikeResponse likePost(Long postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new CustomException(ErrorCode.POST_NOT_FOUND));

        post.incrementLikes();
        postRepository.save(post);

        return PostLikeResponse.of(postId, post.getLikeCount());
    }

    public PostLikeResponse unlikePost(Long postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new CustomException(ErrorCode.POST_NOT_FOUND));

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