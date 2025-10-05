package com.kakaotechbootcamp.community.domain.post.entity;

import com.kakaotechbootcamp.community.domain.common.BaseEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
public class Comment extends BaseEntity {

    private Long id;

    private Long authorId;

    private Long postId;

    private String content;

    public Comment withId(Long id) {
        return this.toBuilder().id(id).build();
    }

    public static Comment createWithoutId(Long authorId, Long postId, String content) {
        return Comment.builder()
                .authorId(authorId)
                .postId(postId)
                .content(content)
                .build();
    }
}
