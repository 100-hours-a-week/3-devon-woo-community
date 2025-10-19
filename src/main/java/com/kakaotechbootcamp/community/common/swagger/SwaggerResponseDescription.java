package com.kakaotechbootcamp.community.common.swagger;

import com.kakaotechbootcamp.community.common.exception.ErrorCode;
import lombok.Getter;

import java.util.LinkedHashSet;
import java.util.Set;

import static com.kakaotechbootcamp.community.common.exception.ErrorCode.*;

/**
 * Swagger API 응답 설명을 위한 Enum
 * 각 API별로 발생 가능한 에러 코드를 정의
 */
@Getter
public enum SwaggerResponseDescription {

    // Auth API
    AUTH_SIGNUP(new LinkedHashSet<>(Set.of(
            INVALID_EMAIL_FORMAT,
            INVALID_PASSWORD_FORMAT,
            INVALID_NICKNAME,
            DUPLICATE_EMAIL,
            DUPLICATE_NICKNAME
    ))),
    AUTH_LOGIN(new LinkedHashSet<>(Set.of(
            INVALID_EMAIL_FORMAT,
            USER_NOT_FOUND,
            INVALID_PASSWORD
    ))),
    AUTH_LOGOUT(new LinkedHashSet<>(Set.of())),

    // Member API
    MEMBER_UPDATE(new LinkedHashSet<>(Set.of(
            USER_NOT_FOUND,
            INVALID_NICKNAME,
            DUPLICATE_NICKNAME
    ))),
    MEMBER_PASSWORD_UPDATE(new LinkedHashSet<>(Set.of(
            USER_NOT_FOUND,
            INVALID_PASSWORD_FORMAT,
            INVALID_CURRENT_PASSWORD,
            SAME_AS_CURRENT_PASSWORD
    ))),
    MEMBER_DELETE(new LinkedHashSet<>(Set.of(
            USER_NOT_FOUND
    ))),

    // Post API
    POST_CREATE(new LinkedHashSet<>(Set.of(
            USER_NOT_FOUND
    ))),
    POST_UPDATE(new LinkedHashSet<>(Set.of(
            POST_NOT_FOUND,
            NO_PERMISSION
    ))),
    POST_DELETE(new LinkedHashSet<>(Set.of(
            POST_NOT_FOUND,
            NO_PERMISSION
    ))),
    POST_GET(new LinkedHashSet<>(Set.of(
            POST_NOT_FOUND
    ))),
    POST_LIST(new LinkedHashSet<>(Set.of())),
    POST_LIKE(new LinkedHashSet<>(Set.of(
            POST_NOT_FOUND,
            USER_NOT_FOUND,
            ALREADY_LIKED
    ))),
    POST_UNLIKE(new LinkedHashSet<>(Set.of(
            POST_NOT_FOUND,
            USER_NOT_FOUND,
            LIKE_NOT_FOUND
    ))),

    // Comment API
    COMMENT_CREATE(new LinkedHashSet<>(Set.of(
            POST_NOT_FOUND,
            USER_NOT_FOUND
    ))),
    COMMENT_LIST(new LinkedHashSet<>(Set.of(
            POST_NOT_FOUND
    ))),
    COMMENT_GET(new LinkedHashSet<>(Set.of(
            COMMENT_NOT_FOUND
    ))),
    COMMENT_UPDATE(new LinkedHashSet<>(Set.of(
            COMMENT_NOT_FOUND,
            NO_PERMISSION
    ))),
    COMMENT_DELETE(new LinkedHashSet<>(Set.of(
            COMMENT_NOT_FOUND,
            NO_PERMISSION
    )));

    private final Set<ErrorCode> errorCodeList;

    SwaggerResponseDescription(Set<ErrorCode> errorCodeList) {
        // 공통 에러 추가
        errorCodeList.addAll(new LinkedHashSet<>(Set.of(
                INVALID_REQUEST,
                INTERNAL_SERVER_ERROR
        )));

        this.errorCodeList = errorCodeList;
    }
}
