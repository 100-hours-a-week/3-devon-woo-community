package com.kakaotechbootcamp.community.application.auth.dto;


public record SignupRequest(

        String email,

        String password,

        String nickname,

        String profileImageUrl

) {}