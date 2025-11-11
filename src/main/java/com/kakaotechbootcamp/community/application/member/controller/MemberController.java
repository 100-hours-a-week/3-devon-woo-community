package com.kakaotechbootcamp.community.application.member.controller;

import com.kakaotechbootcamp.community.application.member.dto.request.MemberUpdateRequest;
import com.kakaotechbootcamp.community.application.member.dto.request.PasswordUpdateRequest;
import com.kakaotechbootcamp.community.application.member.dto.response.MemberDetailsResponse;
import com.kakaotechbootcamp.community.application.member.dto.response.MemberResponse;
import com.kakaotechbootcamp.community.application.member.dto.response.MemberUpdateResponse;
import com.kakaotechbootcamp.community.application.member.service.MemberService;
import com.kakaotechbootcamp.community.common.dto.api.ApiResponse;
import com.kakaotechbootcamp.community.common.swagger.CustomExceptionDescription;
import com.kakaotechbootcamp.community.common.swagger.SwaggerResponseDescription;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Member", description = "회원 관련 API")
@RestController
@RequestMapping("/api/v1/members")
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;

    @Operation(summary = "회원 정보 조회", description = "회원의 프로필 정보를 조회합니다.")
    @CustomExceptionDescription(SwaggerResponseDescription.MEMBER_GET)
    @GetMapping("/{id}")
    public ApiResponse<MemberDetailsResponse> getMemberProfile(
            @Parameter(description = "회원 ID") @PathVariable Long id
    ) {
        MemberDetailsResponse response = memberService.getMemberProfile(id);
        return ApiResponse.success(response, "member_get_success");
    }


    @Operation(summary = "회원 정보 수정", description = "회원의 프로필 정보를 수정합니다.")
    @CustomExceptionDescription(SwaggerResponseDescription.MEMBER_UPDATE)
    @PatchMapping("/{id}")
    public ApiResponse<MemberUpdateResponse> updateMember(
            @Parameter(description = "회원 ID") @PathVariable Long id,
            @RequestBody @Validated MemberUpdateRequest request
    ) {
        MemberUpdateResponse response = memberService.updateMember(id, request);
        return ApiResponse.success(response, "member_update_success");
    }

    @Operation(summary = "비밀번호 변경", description = "회원의 비밀번호를 변경합니다.")
    @CustomExceptionDescription(SwaggerResponseDescription.MEMBER_PASSWORD_UPDATE)
    @PatchMapping("/{id}/password")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void updatePassword(
            @Parameter(description = "회원 ID") @PathVariable Long id,
            @RequestBody @Validated PasswordUpdateRequest request
    ){
        memberService.updatePassword(id, request);
    }

    @Operation(summary = "회원 탈퇴", description = "회원을 탈퇴 처리합니다.")
    @CustomExceptionDescription(SwaggerResponseDescription.MEMBER_DELETE)
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteMember(@Parameter(description = "회원 ID") @PathVariable Long id) {
        memberService.deleteMember(id);
    }
}
