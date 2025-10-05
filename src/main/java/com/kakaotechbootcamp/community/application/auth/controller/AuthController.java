package com.kakaotechbootcamp.community.application.auth.controller;

import com.kakaotechbootcamp.community.application.auth.dto.LoginRequest;
import com.kakaotechbootcamp.community.application.auth.dto.SignupRequest;
import com.kakaotechbootcamp.community.common.dto.api.ApiResponse;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    @PostMapping("/signup")
    public ApiResponse<Void> signUp(
            @RequestBody @Validated SignupRequest request
    ){

        return ApiResponse.success();
    }

    @PostMapping("/login")
    public ApiResponse<Void> login(
            @RequestBody @Validated LoginRequest request
    ){

        return ApiResponse.success();
    }

    @PostMapping("/logout")
    public ApiResponse<Void> logout(){

        return ApiResponse.success();
    }
}
