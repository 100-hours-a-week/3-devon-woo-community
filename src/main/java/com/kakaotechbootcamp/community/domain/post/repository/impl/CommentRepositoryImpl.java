package com.kakaotechbootcamp.community.domain.post.repository.impl;

import com.kakaotechbootcamp.community.domain.post.entity.Comment;
import com.kakaotechbootcamp.community.domain.post.repository.CommentQueryRepository;
import com.querydsl.core.Tuple;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.kakaotechbootcamp.community.domain.member.entity.QMember.member;
import static com.kakaotechbootcamp.community.domain.post.entity.QComment.comment;
import static com.kakaotechbootcamp.community.domain.post.entity.QPost.post;

@Repository
@RequiredArgsConstructor
public class CommentRepositoryImpl implements CommentQueryRepository {

    private final JPAQueryFactory queryFactory;

    @Override
    public List<Comment> findByPostIdWithMember(Long postId) {
        return queryFactory
                .selectFrom(comment)
                .join(comment.member, member).fetchJoin()
                .where(comment.post.id.eq(postId))
                .orderBy(comment.createdAt.asc())
                .fetch();
    }

    @Override
    public Map<Long, Long> countCommentsByPostIds(List<Long> postIds) {
        List<Tuple> results = queryFactory
                .select(comment.post.id, comment.count())
                .from(comment)
                .where(comment.post.id.in(postIds))
                .groupBy(comment.post.id)
                .fetch();

        return results.stream()
                .collect(Collectors.toMap(
                        tuple -> tuple.get(comment.post.id),
                        tuple -> tuple.get(comment.count())
                ));
    }

    @Override
    public List<Comment> findByMemberId(Long memberId) {
        return queryFactory
                .selectFrom(comment)
                .join(comment.post, post).fetchJoin()
                .where(comment.member.id.eq(memberId))
                .orderBy(comment.createdAt.desc())
                .fetch();
    }
}
