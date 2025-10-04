package com.kakaotechbootcamp.community.domain.post.entity;


import com.kakaotechbootcamp.community.domain.common.BaseEntity;
import com.kakaotechbootcamp.community.domain.member.entity.Member;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class Post extends BaseEntity {

    private Long id;

    private Member author;

    private String title;

    private String content;

    private Long viewsCount = 0L;

    private Long likeCount = 0L;


}
