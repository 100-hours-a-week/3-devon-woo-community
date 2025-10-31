package com.kakaotechbootcamp.community.domain.post.entity;

import com.kakaotechbootcamp.community.common.exception.CustomException;
import com.kakaotechbootcamp.community.common.exception.code.MemberErrorCode;
import com.kakaotechbootcamp.community.common.exception.code.PostErrorCode;
import jakarta.persistence.Embeddable;
import lombok.*;
import lombok.extern.slf4j.Slf4j;

import java.io.Serializable;
import java.util.Objects;

@Slf4j
@Embeddable
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class PostLikeId implements Serializable {

    private Long postId;
    private Long memberId;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof PostLikeId)) return false;

        PostLikeId that = (PostLikeId) o;
        return Objects.equals(postId, that.postId) &&
                Objects.equals(memberId, that.memberId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(postId, memberId);
    }

    public static PostLikeId create(Long postId, Long memberId) {
        validateCreate(postId, memberId);
        return PostLikeId.builder()
                .postId(postId)
                .memberId(memberId)
                .build();
    }

    private static void validateCreate(Long postId, Long memberId) {
        if (postId == null) {
            log.error("생성 오류 - postId : null");
            throw new CustomException(PostErrorCode.POST_NOT_FOUND);
        }
        if (memberId == null) {
            log.error("생성 오류 - memberId : null");
            throw new CustomException(MemberErrorCode.USER_NOT_FOUND);
        }
    }
}
