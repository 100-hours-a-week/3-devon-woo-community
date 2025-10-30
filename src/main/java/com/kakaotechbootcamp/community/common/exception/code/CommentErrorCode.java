package com.kakaotechbootcamp.community.common.exception.code;

import com.kakaotechbootcamp.community.common.exception.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum CommentErrorCode implements ErrorCode {

    // 댓글 조회 에러 (404)
    COMMENT_NOT_FOUND(HttpStatus.NOT_FOUND, "댓글을 찾을 수 없습니다"),

    // 권한 에러 (403)
    NO_PERMISSION(HttpStatus.FORBIDDEN, "권한이 없습니다");

    private final HttpStatus httpStatus;
    private final String message;
}
