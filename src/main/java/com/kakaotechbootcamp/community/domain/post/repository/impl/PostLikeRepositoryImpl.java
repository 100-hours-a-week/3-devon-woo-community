package com.kakaotechbootcamp.community.domain.post.repository.impl;

import com.kakaotechbootcamp.community.domain.post.entity.PostLike;
import com.kakaotechbootcamp.community.domain.post.repository.PostLikeRepository;
import com.kakaotechbootcamp.community.infra.repository.CustomJpaRepositoryImpl;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class PostLikeRepositoryImpl extends CustomJpaRepositoryImpl<PostLike, Long> implements PostLikeRepository {

    public PostLikeRepositoryImpl() {
        super(
                PostLike::getId,
                (postLike, id) -> {
                    if (postLike.getId() != null) {
                        return postLike;
                    }
                    return postLike.withId(id);
                }
        );
    }

    @Override
    public Optional<PostLike> findByPostIdAndMemberId(Long postId, Long memberId) {
        return findAll().stream()
                .filter(postLike -> postLike.getPostId().equals(postId)
                        && postLike.getMemberId().equals(memberId))
                .findFirst();
    }

    @Override
    public boolean existsByPostIdAndMemberId(Long postId, Long memberId) {
        return findByPostIdAndMemberId(postId, memberId).isPresent();
    }

    @Override
    public void deleteByPostIdAndMemberId(Long postId, Long memberId) {
        findByPostIdAndMemberId(postId, memberId)
                .ifPresent(postLike -> deleteById(postLike.getId()));
    }

    @Override
    public long countByPostId(Long postId) {
        return findAll().stream()
                .filter(postLike -> postLike.getPostId().equals(postId))
                .count();
    }
}
