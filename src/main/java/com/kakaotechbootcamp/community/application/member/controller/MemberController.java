package com.kakaotechbootcamp.community.application.member.controller;

import com.kakaotechbootcamp.community.application.member.dto.request.MemberUpdateRequest;
import com.kakaotechbootcamp.community.common.dto.api.ApiResponse;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/members")
public class MemberController {

    @PatchMapping("/{id}")
    public ApiResponse<Void> updateMember(
            @PathVariable Long id,
            @RequestBody @Validated MemberUpdateRequest request
    ) {
        return ApiResponse.success();
    }

    @PostMapping("/{id}/password")
    public ApiResponse<Void> updatePassword(){
        return ApiResponse.success(null,"password_update_success");
    }
}
