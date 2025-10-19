package com.kakaotechbootcamp.community.domain.post.entity;

import com.kakaotechbootcamp.community.domain.common.BaseEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
public class Attachment extends BaseEntity {

    private Long id;

    private Long postId;

    private String attachmentUrl;

    public static Attachment create(Long postId, String attachmentUrl) {
        return Attachment.builder()
                .postId(postId)
                .attachmentUrl(attachmentUrl)
                .build();
    }
}
