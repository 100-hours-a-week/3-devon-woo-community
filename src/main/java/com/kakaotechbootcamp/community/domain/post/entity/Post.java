package com.kakaotechbootcamp.community.domain.post.entity;

import com.kakaotechbootcamp.community.domain.common.BaseEntity;
import com.kakaotechbootcamp.community.domain.member.entity.Member;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.util.Assert;

@Entity
@Getter
@Builder(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "post")
public class Post extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @Column(name = "title", length = 200, nullable = false)
    private String title;

    @Column(name = "content", columnDefinition = "TEXT", nullable = false)
    private String content;

    @Column(name = "views_count", nullable = false)
    private Long viewsCount;

    @Column(name = "like_count", nullable = false)
    private Long likeCount;

    @Column(name = "is_deleted", nullable = false)
    private Boolean isDeleted;

    public static Post create(Member member, String title, String content) {
        validateCreate(member, title, content);
        return Post.builder()
                .member(member)
                .title(title)
                .content(content)
                .viewsCount(0L)
                .likeCount(0L)
                .isDeleted(false)
                .build();
    }

    public void updatePost(String title, String content) {
        if (title != null) {
            Assert.hasText(title, "title required");
            if (title.length() > 200) {
                throw new IllegalArgumentException("title too long");
            }
            this.title = title;
        }
        if (content != null) {
            Assert.hasText(content, "content required");
            this.content = content;
        }
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

    public void delete() {
        this.isDeleted = true;
    }

    public void restore() {
        this.isDeleted = false;
    }

    public boolean isDeleted() {
        return this.isDeleted;
    }

    private static void validateCreate(Member member, String title, String content){
        Assert.notNull(member, "member required");
        Assert.hasText(title, "title required");
        Assert.hasText(content, "content required");

        if (title.length() > 200) {
            throw new IllegalArgumentException("title too long");
        }
    }
}
