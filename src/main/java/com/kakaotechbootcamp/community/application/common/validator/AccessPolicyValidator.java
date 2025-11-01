package com.kakaotechbootcamp.community.application.common.validator;

import com.kakaotechbootcamp.community.common.exception.CustomException;
import com.kakaotechbootcamp.community.common.exception.code.PostErrorCode;
import org.springframework.stereotype.Service;

@Service
public class AccessPolicyValidator {

    public void checkAccess(Long resourceMemberId, Long requesterId) {
        if (!resourceMemberId.equals(requesterId)) {
            throw new CustomException(PostErrorCode.NO_PERMISSION);
        }
    }
}
