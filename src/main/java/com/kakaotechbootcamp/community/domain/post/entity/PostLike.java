package com.kakaotechbootcamp.community.domain.post.entity;

import com.kakaotechbootcamp.community.domain.common.BaseEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
public class PostLike extends BaseEntity {

    private Long id;

    private Long postId;

    private Long memberId;

    public PostLike withId(Long id) {
        this.id = id;
        return this;
    }

    public static PostLike createWithoutId(Long postId, Long memberId) {
        return PostLike.builder()
                .postId(postId)
                .memberId(memberId)
                .build();
    }
}
