package com.kakaotechbootcamp.community.application.member.service;

import com.kakaotechbootcamp.community.application.member.dto.request.MemberUpdateRequest;
import com.kakaotechbootcamp.community.application.member.dto.request.PasswordUpdateRequest;
import com.kakaotechbootcamp.community.application.member.dto.response.MemberUpdateResponse;
import com.kakaotechbootcamp.community.common.exception.CustomException;
import com.kakaotechbootcamp.community.common.exception.ErrorCode;
import com.kakaotechbootcamp.community.domain.member.entity.Member;
import com.kakaotechbootcamp.community.domain.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;

    public MemberUpdateResponse updateMember(Long id, MemberUpdateRequest request) {
        Member member = memberRepository.findById(id)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        validateUpdateRequest(request, member);

        member.updateProfile(request.nickname(), request.profileImage());
        Member savedMember = memberRepository.save(member);

        return new MemberUpdateResponse(
                savedMember.getNickname(),
                savedMember.getProfileImageUrl()
        );
    }

    public void updatePassword(Long id, PasswordUpdateRequest request) {
        Member member = memberRepository.findById(id)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        validatePasswordUpdate(request, member);
        
        member.updatePassword(request.newPassword());
        memberRepository.save(member);
    }

    private void validateUpdateRequest(MemberUpdateRequest request, Member currentMember) {
        if (request.nickname() != null && !request.nickname().equals(currentMember.getNickname())) {
            if (memberRepository.existsByNickname(request.nickname())) {
                throw new CustomException(ErrorCode.DUPLICATE_NICKNAME);
            }
        }
    }

    private void validatePasswordUpdate(PasswordUpdateRequest request, Member member) {
        if (!request.currentPassword().equals(member.getPassword())) {
            throw new CustomException(ErrorCode.INVALID_CURRENT_PASSWORD);
        }

        if (request.currentPassword().equals(request.newPassword())) {
            throw new CustomException(ErrorCode.SAME_AS_CURRENT_PASSWORD);
        }
    }

}