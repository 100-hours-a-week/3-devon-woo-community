package com.kakaotechbootcamp.community.common.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {

    INVALID_REQUEST(HttpStatus.BAD_REQUEST, "invalid_request"),
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "internal_server_error"),

    INVALID_EMAIL_FORMAT(HttpStatus.BAD_REQUEST, "invalid_email_format"),
    USER_NOT_FOUND(HttpStatus.UNAUTHORIZED, "user_not_found"),
    INVALID_OR_EXPIRED_TOKEN(HttpStatus.UNAUTHORIZED, "invalid_or_expired_token"),

    INVALID_NICKNAME(HttpStatus.BAD_REQUEST, "invalid_nickname"),
    INVALID_PASSWORD_FORMAT(HttpStatus.BAD_REQUEST, "invalid_password_format"),
    MISSING_PASSWORD(HttpStatus.BAD_REQUEST, "missing_password"),
    INVALID_CURRENT_PASSWORD(HttpStatus.UNAUTHORIZED, "invalid_current_password"),

    DUPLICATE_EMAIL(HttpStatus.CONFLICT, "duplicate_email"),
    DUPLICATE_NICKNAME(HttpStatus.CONFLICT, "duplicate_nickname"),
    INVALID_PASSWORD(HttpStatus.UNAUTHORIZED, "invalid_password"),
    SAME_AS_CURRENT_PASSWORD(HttpStatus.BAD_REQUEST, "same_as_current_password"),

    POST_NOT_FOUND(HttpStatus.NOT_FOUND, "post_not_found"),
    NO_PERMISSION(HttpStatus.FORBIDDEN, "no_permission");

    private final HttpStatus httpStatus;
    private final String message;
}