package com.kakaotechbootcamp.community.application.auth.service;

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
public class SignupService {

    private final MemberRepository memberRepository;

    public SignupResponse signup(SignupRequest request){
        validateSignupRequest(request);

        Member member = Member.createWithoutId(
                request.email(),
                request.password(),
                request.nickname(),
                request.profileImage()
        );

        Member savedMember = memberRepository.save(member);
        return new SignupResponse(savedMember.getId());
    }

    private void validateSignupRequest(SignupRequest request){
        if (memberRepository.existsByEmail(request.email())) {
            throw new CustomException(ErrorCode.DUPLICATE_EMAIL);
        }

        if (memberRepository.existsByNickname(request.nickname())) {
            throw new CustomException(ErrorCode.DUPLICATE_NICKNAME);
        }
    }
}
