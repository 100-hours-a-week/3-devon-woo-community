package com.kakaotechbootcamp.community.domain.post.entity;

import com.kakaotechbootcamp.community.domain.common.entity.BaseTimeEntity;
import com.kakaotechbootcamp.community.domain.member.entity.Member;
import com.kakaotechbootcamp.community.domain.post.entity.id.PostLikeId;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.util.Assert;

@Entity
@Getter
@Builder(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "post_like")
public class PostLike extends BaseTimeEntity {

    @EmbeddedId
    private PostLikeId id;

    @MapsId("postId")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id", nullable = false)
    private Post post;

    @MapsId("memberId")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    public static PostLike create(Post post, Member member) {
        validateCreate(post, member);
        return PostLike.builder()
                .id(PostLikeId.create(post.getId(), member.getId()))
                .post(post)
                .member(member)
                .build();
    }

    private static void validateCreate(Post post, Member member) {
        Assert.notNull(post, "post required");
        Assert.notNull(member, "member required");
    }
}
