package com.kakaotechbootcamp.community.application.post.service;

import com.kakaotechbootcamp.community.application.post.dto.request.PostCreateRequest;
import com.kakaotechbootcamp.community.application.post.dto.request.PostUpdateRequest;
import com.kakaotechbootcamp.community.application.post.dto.response.PostListResponse;
import com.kakaotechbootcamp.community.application.post.dto.response.PostResponse;
import com.kakaotechbootcamp.community.application.post.dto.response.PostSummaryResponse;
import com.kakaotechbootcamp.community.application.validator.AccessPolicyValidator;
import com.kakaotechbootcamp.community.common.exception.CustomException;
import com.kakaotechbootcamp.community.common.exception.ErrorCode;
import com.kakaotechbootcamp.community.domain.member.entity.Member;
import com.kakaotechbootcamp.community.domain.member.repository.MemberRepository;
import com.kakaotechbootcamp.community.domain.post.entity.Attachment;
import com.kakaotechbootcamp.community.domain.post.entity.Post;
import com.kakaotechbootcamp.community.domain.post.repository.AttachmentRepository;
import com.kakaotechbootcamp.community.domain.post.repository.CommentRepository;
import com.kakaotechbootcamp.community.domain.post.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PostService {
    private final PostRepository postRepository;
    private final MemberRepository memberRepository;
    private final AttachmentRepository attachmentRepository;
    private final CommentRepository commentRepository;
    private final AccessPolicyValidator accessPolicyValidator;

    public PostResponse createPost(PostCreateRequest request, Long authorId) {
        Member member = findMemberById(authorId);

        Post post = Post.create(authorId, request.title(), request.content());
        Attachment attachment = Attachment.create(post.getId(), request.image());
        Post savedPost = postRepository.save(post);
        Attachment savedAttachment = attachmentRepository.save(attachment);

        return PostResponse.of(savedPost, member, savedAttachment);
    }

    public PostResponse updatePost(Long postId, PostUpdateRequest request, Long authorId) {
        Post post = findPostById(postId);
        Member member = findMemberById(authorId);

        accessPolicyValidator.checkAccess(post.getAuthorId(), authorId);

        post.updatePost(request.title(), request.content());
        Post savedPost = postRepository.save(post);

        Attachment attachment = Optional.ofNullable(request.image())
                .map(img -> attachmentRepository.save(Attachment.create(postId, img)))
                .orElseGet(() -> attachmentRepository.findByPostId(postId).orElse(null));

        return PostResponse.of(savedPost, member, attachment);
    }

    public void deletePost(Long postId, Long authorId) {
        Post post = findPostById(postId);
        accessPolicyValidator.checkAccess(post.getAuthorId(), authorId);

        postRepository.deleteById(postId);
    }

    public PostResponse getPost(Long postId) {
        Post post = findPostById(postId);
        Member member = findMemberById(post.getAuthorId());
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

    private Post findPostById(Long postId) {
        return postRepository.findById(postId)
                .orElseThrow(() -> new CustomException(ErrorCode.POST_NOT_FOUND));
    }

    private Member findMemberById(Long memberId) {
        return memberRepository.findById(memberId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
    }
}
