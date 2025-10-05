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
public class Attachment extends BaseEntity {

    private Long id;

    private Long postId;

    private String attachmentUrl;

    public Attachment withId(Long id) {
        return this.toBuilder().id(id).build();
    }

    public static Attachment createWithoutId(Long postId, String attachmentUrl) {
        return Attachment.builder()
                .postId(postId)
                .attachmentUrl(attachmentUrl)
                .build();
    }
}
