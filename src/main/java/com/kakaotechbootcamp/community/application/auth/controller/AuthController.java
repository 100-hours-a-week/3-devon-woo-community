package com.kakaotechbootcamp.community.application.auth.controller;

import com.kakaotechbootcamp.community.application.auth.dto.LoginRequest;
import com.kakaotechbootcamp.community.application.auth.dto.LoginResponse;
import com.kakaotechbootcamp.community.application.auth.dto.SignupRequest;
import com.kakaotechbootcamp.community.application.auth.dto.SignupResponse;
import com.kakaotechbootcamp.community.application.auth.service.AuthCommandService;
import com.kakaotechbootcamp.community.common.dto.api.ApiResponse;
import com.kakaotechbootcamp.community.common.swagger.CustomExceptionDescription;
import com.kakaotechbootcamp.community.common.swagger.SwaggerResponseDescription;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Auth", description = "인증 관련 API")
@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthCommandService authCommandService;

    @Operation(summary = "회원가입", description = "새로운 회원을 등록합니다.")
    @CustomExceptionDescription(SwaggerResponseDescription.AUTH_SIGNUP)
    @PostMapping("/signup")
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<SignupResponse> signUp(
            @RequestBody @Validated SignupRequest request
    ){
        SignupResponse response = authCommandService.signup(request);
        return ApiResponse.success(response, "signup_success");
    }

    @Operation(summary = "로그인", description = "이메일과 비밀번호로 로그인합니다.")
    @CustomExceptionDescription(SwaggerResponseDescription.AUTH_LOGIN)
    @PostMapping("/login")
    public ApiResponse<LoginResponse> login(
            @RequestBody @Validated LoginRequest request
    ){
        LoginResponse response = authCommandService.login(request);
        return ApiResponse.success(response, "login_success");
    }

    @Operation(summary = "로그아웃", description = "로그아웃 처리를 합니다.")
    @CustomExceptionDescription(SwaggerResponseDescription.AUTH_LOGOUT)
    @PostMapping("/logout")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void logout(){
        // 로그아웃 처리 (세션 무효화 등)
    }
}
