package com.kakaotechbootcamp.community.common.exception.code;

import com.kakaotechbootcamp.community.common.exception.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum PostErrorCode implements ErrorCode {

    // 게시글 조회 에러 (404)
    POST_NOT_FOUND(HttpStatus.NOT_FOUND, "게시글을 찾을 수 없습니다"),

    // 권한 에러 (403)
    NO_PERMISSION(HttpStatus.FORBIDDEN, "권한이 없습니다"),

    // 좋아요 관련 에러
    ALREADY_LIKED(HttpStatus.CONFLICT, "이미 좋아요를 눌렀습니다"),
    LIKE_NOT_FOUND(HttpStatus.NOT_FOUND, "좋아요를 찾을 수 없습니다");

    private final HttpStatus httpStatus;
    private final String message;
}
