package com.kakaotechbootcamp.community.application.member.dto.request;

import jakarta.validation.constraints.NotBlank;

public record MemberDeleteRequest(
        @NotBlank(message = "missing_password")
        String password
) {}