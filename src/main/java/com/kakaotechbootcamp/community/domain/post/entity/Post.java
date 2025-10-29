package com.kakaotechbootcamp.community.domain.post.entity;

import com.kakaotechbootcamp.community.domain.common.BaseEntity;
import com.kakaotechbootcamp.community.domain.member.entity.Member;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Builder(toBuilder = true)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@NoArgsConstructor
@Table(name = "post")
public class Post extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "author_id")
    private Member author;

    private String title;

    private String content;

    @Builder.Default
    private Long viewsCount = 0L;

    @Builder.Default
    private Long likeCount = 0L;

    public static Post create(Member author, String title, String content) {
        return Post.builder()
                .author(author)
                .title(title)
                .content(content)
                .viewsCount(0L)
                .likeCount(0L)
                .build();
    }

    public void updatePost(String title, String content) {
        this.title = title != null ? title : this.title;
        this.content = content != null ? content : this.content;
    }

    public void incrementViews() {
        this.viewsCount++;
    }

    public void incrementLikes() {
        this.likeCount++;
    }

    public void decrementLikes() {
        if (this.likeCount > 0) {
            this.likeCount--;
        }
    }
}
