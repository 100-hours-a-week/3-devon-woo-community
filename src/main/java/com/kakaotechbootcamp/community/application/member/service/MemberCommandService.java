package com.kakaotechbootcamp.community.application.member.service;

import com.kakaotechbootcamp.community.application.member.dto.request.MemberUpdateRequest;
import com.kakaotechbootcamp.community.application.member.dto.request.PasswordUpdateRequest;
import com.kakaotechbootcamp.community.application.member.dto.response.MemberUpdateResponse;
import com.kakaotechbootcamp.community.application.member.validator.MemberValidator;
import com.kakaotechbootcamp.community.common.exception.CustomException;
import com.kakaotechbootcamp.community.common.exception.ErrorCode;
import com.kakaotechbootcamp.community.domain.member.entity.Member;
import com.kakaotechbootcamp.community.domain.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MemberCommandService {

    private final MemberRepository memberRepository;
    private final MemberValidator memberValidator;

    public MemberUpdateResponse updateMember(Long id, MemberUpdateRequest request) {
        Member member = findMemberById(id);
        memberValidator.checkUniqueNickname(request.nickname(), member);

        member.updateProfile(request.nickname(), request.profileImage());

        memberRepository.save(member);

        return MemberUpdateResponse.of(member);
    }

    public void updatePassword(Long id, PasswordUpdateRequest request) {
        Member member = findMemberById(id);

        memberValidator.checkPasswordChangeAllowed(request, member);

        member.updatePassword(request.newPassword());

        memberRepository.save(member);
    }

    public void deleteMember(Long id) {
        Member member = findMemberById(id);
        memberRepository.deleteById(member.getId());
    }

    private Member findMemberById(Long id) {
        return memberRepository.findById(id)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
    }
}
