package com.kakaotechbootcamp.community.application.auth.validation;

import com.kakaotechbootcamp.community.application.auth.dto.SignupRequest;
import com.kakaotechbootcamp.community.common.exception.CustomException;
import com.kakaotechbootcamp.community.common.exception.code.MemberErrorCode;
import com.kakaotechbootcamp.community.domain.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthValidationService {

    private final MemberRepository memberRepository;

    public void validateSignupRequest(SignupRequest request) {
        validateEmailNotDuplicated(request.email());
        validateNicknameNotDuplicated(request.nickname());
    }

    public void validatePassword(String rawPassword, String storedPassword) {
        if (!rawPassword.equals(storedPassword)) {
            throw new CustomException(MemberErrorCode.INVALID_PASSWORD);
        }
    }

    private void validateEmailNotDuplicated(String email) {
        if (memberRepository.existsByEmail(email)) {
            throw new CustomException(MemberErrorCode.DUPLICATE_EMAIL);
        }
    }

    private void validateNicknameNotDuplicated(String nickname) {
        if (memberRepository.existsByNickname(nickname)) {
            throw new CustomException(MemberErrorCode.DUPLICATE_NICKNAME);
        }
    }
}
