package com.kakaotechbootcamp.community.application.member.controller;

import com.kakaotechbootcamp.community.application.member.dto.request.MemberUpdateRequest;
import com.kakaotechbootcamp.community.application.member.dto.request.PasswordUpdateRequest;
import com.kakaotechbootcamp.community.application.member.dto.response.MemberUpdateResponse;
import com.kakaotechbootcamp.community.application.member.service.MemberCommandService;
import com.kakaotechbootcamp.community.common.dto.api.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/members")
@RequiredArgsConstructor
public class MemberController {

    private final MemberCommandService memberCommandService;

    @PatchMapping("/{id}")
    public ApiResponse<MemberUpdateResponse> updateMember(
            @PathVariable Long id,
            @RequestBody @Validated MemberUpdateRequest request
    ) {
        MemberUpdateResponse response = memberCommandService.updateMember(id, request);
        return ApiResponse.success(response, "member_update_success");
    }

    @PatchMapping("/{id}/password")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void updatePassword(
            @PathVariable Long id,
            @RequestBody @Validated PasswordUpdateRequest request
    ){
        memberCommandService.updatePassword(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteMember(@PathVariable Long id) {
        memberCommandService.deleteMember(id);
    }
}
