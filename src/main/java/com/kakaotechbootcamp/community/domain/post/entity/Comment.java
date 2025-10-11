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
public class Comment extends BaseEntity {

    private Long id;

    private Long authorId;

    private Long postId;

    private String content;

    public static Comment create(Long authorId, Long postId, String content) {
        return Comment.builder()
                .authorId(authorId)
                .postId(postId)
                .content(content)
                .build();
    }

    public void updateContent(String content) {
        this.content = content != null ? content : this.content;
    }
}
