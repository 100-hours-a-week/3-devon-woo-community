package com.kakaotechbootcamp.community.domain.post.entity;

import com.kakaotechbootcamp.community.domain.common.BaseEntity;
import lombok.*;

@Getter
@Builder(toBuilder = true)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@NoArgsConstructor
public class PostLike extends BaseEntity {

    private Long id;

    private Long postId;

    private Long memberId;

    public static PostLike create(Long postId, Long memberId) {
        return PostLike.builder()
                .postId(postId)
                .memberId(memberId)
                .build();
    }
}
