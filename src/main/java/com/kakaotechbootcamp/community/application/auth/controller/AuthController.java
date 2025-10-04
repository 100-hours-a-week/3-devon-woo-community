package com.kakaotechbootcamp.community.application.auth.controller;

import com.kakaotechbootcamp.community.application.auth.dto.LoginRequest;
import com.kakaotechbootcamp.community.application.auth.dto.SignupRequest;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    @PostMapping("/signup")
    public ResponseEntity<Void> signUp(
            @RequestBody @Valid SignupRequest request
    ){

        return ResponseEntity.ok().build();
    }

    @PostMapping("/login")
    public ResponseEntity<Void> login(
            @RequestBody @Valid LoginRequest request
    ){

        return ResponseEntity.ok().build();
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(){

        return ResponseEntity.ok().build();
    }
}
