package com.kakaotechbootcamp.community.application.auth.service;

import com.kakaotechbootcamp.community.application.auth.dto.LoginRequest;
import com.kakaotechbootcamp.community.application.auth.dto.LoginResponse;
import com.kakaotechbootcamp.community.application.auth.dto.SignupRequest;
import com.kakaotechbootcamp.community.application.auth.dto.SignupResponse;
import com.kakaotechbootcamp.community.application.auth.validation.AuthValidationService;
import com.kakaotechbootcamp.community.common.exception.CustomException;
import com.kakaotechbootcamp.community.common.exception.code.MemberErrorCode;
import com.kakaotechbootcamp.community.domain.member.entity.Member;
import com.kakaotechbootcamp.community.domain.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final MemberRepository memberRepository;
    private final AuthValidationService authValidationService;

    public SignupResponse signup(SignupRequest request){
        authValidationService.validateSignupRequest(request);

        Member member = Member.create(
                request.email(),
                request.password(),
                request.nickname()
        );

        member.updateProfileImage(request.profileImage());

        Member savedMember = memberRepository.save(member);
        return new SignupResponse(savedMember.getId());
    }

    public LoginResponse login(LoginRequest request){
        Member member = memberRepository.findByEmail(request.email())
                .orElseThrow(() -> new CustomException(MemberErrorCode.USER_NOT_FOUND));

        authValidationService.validatePassword(request.password(), member.getPassword());

        return new LoginResponse(member.getId());
    }
}
