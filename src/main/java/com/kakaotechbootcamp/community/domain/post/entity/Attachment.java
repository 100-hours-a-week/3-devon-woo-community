package com.kakaotechbootcamp.community.domain.post.entity;

import com.kakaotechbootcamp.community.domain.common.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.util.Assert;

@Entity
@Getter
@Builder(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "attachment")
public class Attachment extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id", nullable = false)
    private Post post;

    @Column(name = "attachment_url", length = 500, nullable = false)
    private String attachmentUrl;

    @Column(name = "is_deleted", nullable = false)
    private Boolean isDeleted;

    public static Attachment create(Post post, String attachmentUrl) {
        Assert.notNull(post, "post required");
        Assert.hasText(attachmentUrl, "attachment url required");

        if (attachmentUrl.length() > 500) {
            throw new IllegalArgumentException("attachment url too long");
        }

        return Attachment.builder()
                .post(post)
                .attachmentUrl(attachmentUrl)
                .isDeleted(false)
                .build();
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
}
