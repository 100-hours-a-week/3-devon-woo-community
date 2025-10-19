package com.kakaotechbootcamp.community.application.member.dto.request;

import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import static com.kakaotechbootcamp.community.common.validation.ValidationMessages.*;
import static com.kakaotechbootcamp.community.common.validation.ValidationPatterns.*;

public record MemberUpdateRequest(
        @Size(max = NICKNAME_MAX_LENGTH, message = INVALID_NICKNAME)
        String nickname,

        @Pattern(regexp = URL_PATTERN, message = INVALID_PROFILE_IMAGE)
        String profileImage
) {}
