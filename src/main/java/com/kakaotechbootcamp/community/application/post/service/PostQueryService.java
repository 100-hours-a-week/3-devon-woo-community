package com.kakaotechbootcamp.community.application.post.service;

import com.kakaotechbootcamp.community.application.post.dto.response.PostListResponse;
import com.kakaotechbootcamp.community.application.post.dto.response.PostResponse;
import com.kakaotechbootcamp.community.application.post.dto.response.PostSummaryResponse;
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
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PostQueryService {
    private final PostRepository postRepository;
    private final MemberRepository memberRepository;
    private final AttachmentRepository attachmentRepository;
    private final CommentRepository commentRepository;

    public PostResponse getPost(Long postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new CustomException(ErrorCode.POST_NOT_FOUND));
        Member member = memberRepository.findById(post.getAuthorId())
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
        Attachment attachment = attachmentRepository.findByPostId(postId)
                .orElse(null);

        // TODO: 조회수 증가는 Command의 역할이므로 추후 분리 필요
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
}
