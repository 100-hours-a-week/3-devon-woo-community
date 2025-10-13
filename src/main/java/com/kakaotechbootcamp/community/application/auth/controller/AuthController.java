package com.kakaotechbootcamp.community.application.auth.controller;

import com.kakaotechbootcamp.community.application.auth.dto.LoginRequest;
import com.kakaotechbootcamp.community.application.auth.dto.LoginResponse;
import com.kakaotechbootcamp.community.application.auth.dto.SignupRequest;
import com.kakaotechbootcamp.community.application.auth.dto.SignupResponse;
import com.kakaotechbootcamp.community.application.auth.service.AuthCommandService;
import com.kakaotechbootcamp.community.common.dto.api.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthCommandService authCommandService;

    @PostMapping("/signup")
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<SignupResponse> signUp(
            @RequestBody @Validated SignupRequest request
    ){
        SignupResponse response = authCommandService.signup(request);
        return ApiResponse.success(response, "signup_success");
    }

    @PostMapping("/login")
    public ApiResponse<LoginResponse> login(
            @RequestBody @Validated LoginRequest request
    ){
        LoginResponse response = authCommandService.login(request);
        return ApiResponse.success(response, "login_success");
    }

    @PostMapping("/logout")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void logout(){
        // 로그아웃 처리 (세션 무효화 등)
    }
}
