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
public class Post extends BaseEntity {

    private Long id;

    private Long authorId;

    private String title;

    private String content;

    @Builder.Default
    private Long viewsCount = 0L;

    @Builder.Default
    private Long likeCount = 0L;

    public Post withId(Long id) {
        this.id = id;
        return this;
    }

    public static Post createWithoutId(Long authorId, String title, String content) {
        return Post.builder()
                .authorId(authorId)
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
