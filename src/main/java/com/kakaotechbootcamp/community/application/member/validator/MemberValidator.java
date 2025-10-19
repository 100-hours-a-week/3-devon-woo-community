package com.kakaotechbootcamp.community.application.member.validator;

import com.kakaotechbootcamp.community.application.member.dto.request.PasswordUpdateRequest;
import com.kakaotechbootcamp.community.common.exception.CustomException;
import com.kakaotechbootcamp.community.common.exception.ErrorCode;
import com.kakaotechbootcamp.community.domain.member.entity.Member;
import com.kakaotechbootcamp.community.domain.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class MemberValidator {

    private final MemberRepository memberRepository;

    public void checkUniqueNickname(String nickname, Member currentMember) {
        if (nickname != null && !nickname.equals(currentMember.getNickname())) {
            if (memberRepository.existsByNickname(nickname)) {
                throw new CustomException(ErrorCode.DUPLICATE_NICKNAME);
            }
        }
    }

    public void checkPasswordChangeAllowed(PasswordUpdateRequest request, Member member) {
        if (!request.currentPassword().equals(member.getPassword())) {
            throw new CustomException(ErrorCode.INVALID_CURRENT_PASSWORD);
        }

        if (request.newPassword().equals(member.getPassword())) {
            throw new CustomException(ErrorCode.SAME_AS_CURRENT_PASSWORD);
        }
    }
}
