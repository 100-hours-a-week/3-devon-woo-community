package com.kakaotechbootcamp.community.common.exception.code;

import com.kakaotechbootcamp.community.common.exception.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum CommonErrorCode implements ErrorCode {

    // 공통 에러
    INVALID_REQUEST(HttpStatus.BAD_REQUEST, "잘못된 요청입니다"),
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "서버 내부 오류가 발생했습니다"),

    // 도메인 검증 에러 (500 - 서버 내부 로직 오류)
    INVALID_DOMAIN_STATE(HttpStatus.INTERNAL_SERVER_ERROR, "도메인 상태가 유효하지 않습니다");

    private final HttpStatus httpStatus;
    private final String message;
}
