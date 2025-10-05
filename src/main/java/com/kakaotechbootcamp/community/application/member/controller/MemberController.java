package com.kakaotechbootcamp.community.application.member.controller;

import com.kakaotechbootcamp.community.application.member.dto.request.MemberUpdateRequest;
import com.kakaotechbootcamp.community.application.member.dto.request.PasswordUpdateRequest;
import com.kakaotechbootcamp.community.application.member.dto.response.MemberUpdateResponse;
import com.kakaotechbootcamp.community.application.member.service.MemberService;
import com.kakaotechbootcamp.community.common.dto.api.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/members")
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;

    @PatchMapping("/{id}")
    public ApiResponse<MemberUpdateResponse> updateMember(
            @PathVariable Long id,
            @RequestBody @Validated MemberUpdateRequest request
    ) {
        MemberUpdateResponse response = memberService.updateMember(id, request);
        return ApiResponse.success(response, "member_update_success");
    }

    @PostMapping("/{id}/password")
    public ApiResponse<Void> updatePassword(
            @PathVariable Long id,
            @RequestBody @Validated PasswordUpdateRequest request
    ){
        memberService.updatePassword(id, request);
        return ApiResponse.success(null, "password_update_success");
    }
}
