package com.kakaotechbootcamp.community.domain.post.entity;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class Attachment {

    private Long id;

    private Long postId;

    private String attachmentUrl;

}
