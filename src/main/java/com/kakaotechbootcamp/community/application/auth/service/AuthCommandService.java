package com.kakaotechbootcamp.community.application.auth.service;

import com.kakaotechbootcamp.community.application.auth.dto.LoginRequest;
import com.kakaotechbootcamp.community.application.auth.dto.LoginResponse;
import com.kakaotechbootcamp.community.application.auth.dto.SignupRequest;
import com.kakaotechbootcamp.community.application.auth.dto.SignupResponse;
import com.kakaotechbootcamp.community.common.exception.CustomException;
import com.kakaotechbootcamp.community.common.exception.ErrorCode;
import com.kakaotechbootcamp.community.domain.member.entity.Member;
import com.kakaotechbootcamp.community.domain.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthCommandService {
    private final MemberRepository memberRepository;

    public SignupResponse signup(SignupRequest request){
        validateSignupRequest(request);

        Member member = Member.create(
                request.email(),
                request.password(),
                request.nickname(),
                request.profileImage()
        );

        Member savedMember = memberRepository.save(member);
        return new SignupResponse(savedMember.getId());
    }

    public LoginResponse login(LoginRequest request){
        Member member = memberRepository.findByEmail(request.email())
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        validatePassword(request.password(), member.getPassword());

        return new LoginResponse(member.getId());
    }

    private void validateSignupRequest(SignupRequest request){
        if (memberRepository.existsByEmail(request.email())) {
            throw new CustomException(ErrorCode.DUPLICATE_EMAIL);
        }

        if (memberRepository.existsByNickname(request.nickname())) {
            throw new CustomException(ErrorCode.DUPLICATE_NICKNAME);
        }
    }

    private void validatePassword(String rawPassword, String storedPassword) {
        if (!rawPassword.equals(storedPassword)) {
            throw new CustomException(ErrorCode.INVALID_PASSWORD);
        }
    }
}
