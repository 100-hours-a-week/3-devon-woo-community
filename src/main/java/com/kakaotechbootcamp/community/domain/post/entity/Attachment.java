package com.kakaotechbootcamp.community.domain.post.entity;

import com.kakaotechbootcamp.community.domain.common.BaseEntity;
import lombok.*;

@Getter
@Builder(toBuilder = true)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
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
