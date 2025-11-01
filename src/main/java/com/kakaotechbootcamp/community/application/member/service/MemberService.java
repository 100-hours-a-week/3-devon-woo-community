package com.kakaotechbootcamp.community.application.member.service;

import com.kakaotechbootcamp.community.application.member.dto.request.MemberUpdateRequest;
import com.kakaotechbootcamp.community.application.member.dto.request.PasswordUpdateRequest;
import com.kakaotechbootcamp.community.application.member.dto.response.MemberResponse;
import com.kakaotechbootcamp.community.application.member.dto.response.MemberUpdateResponse;
import com.kakaotechbootcamp.community.application.member.validator.MemberValidator;
import com.kakaotechbootcamp.community.common.exception.CustomException;
import com.kakaotechbootcamp.community.common.exception.code.MemberErrorCode;
import com.kakaotechbootcamp.community.domain.member.entity.Member;
import com.kakaotechbootcamp.community.domain.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;
    private final MemberValidator memberValidator;

    /**
     * 회원 프로필 조회
     */
    @Transactional(readOnly = true)
    public MemberResponse getMemberProfile(Long id) {
        Member member = findMemberById(id);
        return MemberResponse.of(member);
    }

    /**
     * 회원 정보 수정
     */
    @Transactional
    public MemberUpdateResponse updateMember(Long id, MemberUpdateRequest request) {
        Member member = findMemberById(id);
        memberValidator.checkUniqueNickname(request.nickname(), member);

        member.changeNickname(request.nickname());
        member.updateProfileImage(request.profileImage());

        memberRepository.save(member);

        return MemberUpdateResponse.of(member);
    }

    /**
     * 회원 비밀 번호 검증 및 변경
     */
    @Transactional
    public void updatePassword(Long id, PasswordUpdateRequest request) {
        Member member = findMemberById(id);

        memberValidator.checkPasswordChangeAllowed(request, member);

        member.changePassword(request.newPassword());

        memberRepository.save(member);
    }

    /**
     * 회원 탈퇴
     */
    @Transactional
    public void deleteMember(Long id) {
        Member member = findMemberById(id);
        memberRepository.deleteById(member.getId());
    }


    private Member findMemberById(Long id) {
        return memberRepository.findById(id)
                .orElseThrow(() -> new CustomException(MemberErrorCode.USER_NOT_FOUND));
    }
}
