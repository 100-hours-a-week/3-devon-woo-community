package com.kakaotechbootcamp.community.application.post.service;

import com.kakaotechbootcamp.community.application.post.dto.request.PostCreateRequest;
import com.kakaotechbootcamp.community.application.post.dto.request.PostUpdateRequest;
import com.kakaotechbootcamp.community.application.post.dto.response.PostListResponse;
import com.kakaotechbootcamp.community.application.post.dto.response.PostResponse;
import com.kakaotechbootcamp.community.application.post.dto.response.PostSummaryResponse;
import com.kakaotechbootcamp.community.common.exception.CustomException;
import com.kakaotechbootcamp.community.common.exception.ErrorCode;
import com.kakaotechbootcamp.community.domain.post.entity.Post;
import com.kakaotechbootcamp.community.domain.post.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;

    public PostResponse createPost(PostCreateRequest request, Long authorId) {
        Post post = Post.createWithoutId(authorId, request.title(), request.content());
        Post savedPost = postRepository.save(post);

        return convertToPostResponse(savedPost);
    }

    public PostResponse getPost(Long postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new CustomException(ErrorCode.POST_NOT_FOUND));

        post.incrementViews();
        postRepository.save(post);

        return convertToPostResponse(post);
    }

    public PostListResponse getPosts(int page, int size) {
        List<Post> posts = postRepository.findAll();

        List<PostSummaryResponse> summaries = posts.stream()
                .map(this::convertToPostSummaryResponse)
                .toList();

        return PostListResponse.builder()
                .items(summaries)
                .page(page)
                .size(size)
                .totalElements((long) posts.size())
                .totalPages((int) Math.ceil((double) posts.size() / size))
                .build();
    }

    public PostResponse updatePost(Long postId, PostUpdateRequest request, Long authorId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new CustomException(ErrorCode.POST_NOT_FOUND));

        validateAuthor(post, authorId);

        post.updatePost(request.title(), request.content());
        Post savedPost = postRepository.save(post);

        return convertToPostResponse(savedPost);
    }

    public void deletePost(Long postId, Long authorId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new CustomException(ErrorCode.POST_NOT_FOUND));

        validateAuthor(post, authorId);

        postRepository.deleteById(postId);
    }

    public PostResponse likePost(Long postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new CustomException(ErrorCode.POST_NOT_FOUND));

        post.incrementLikes();
        Post savedPost = postRepository.save(post);

        return convertToPostResponse(savedPost);
    }

    public PostResponse unlikePost(Long postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new CustomException(ErrorCode.POST_NOT_FOUND));

        post.decrementLikes();
        Post savedPost = postRepository.save(post);

        return convertToPostResponse(savedPost);
    }

    private void validateAuthor(Post post, Long authorId) {
        if (!post.getAuthorId().equals(authorId)) {
            throw new CustomException(ErrorCode.NO_PERMISSION);
        }
    }

    private PostResponse convertToPostResponse(Post post) {
        return PostResponse.builder()
                .postId(post.getId())
                .authorId(post.getAuthorId())
                .title(post.getTitle())
                .content(post.getContent())
                .createdAt(post.getCreatedAt())
                .updatedAt(post.getUpdatedAt())
                .views(post.getViewsCount())
                .likes(post.getLikeCount())
                .build();
    }

    private PostSummaryResponse convertToPostSummaryResponse(Post post) {
        return PostSummaryResponse.builder()
                .postId(post.getId())
                .title(post.getTitle())
                .authorId(post.getAuthorId())
                .createdAt(post.getCreatedAt())
                .views(post.getViewsCount())
                .likes(post.getLikeCount())
                .commentsCount(0L) // TODO: 댓글 수 계산 로직 추가
                .build();
    }
}