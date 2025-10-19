package com.kakaotechbootcamp.community.application.validator;

import com.kakaotechbootcamp.community.common.exception.CustomException;
import com.kakaotechbootcamp.community.common.exception.ErrorCode;
import org.springframework.stereotype.Component;

@Component
public class AccessPolicyValidator {

    public void checkAccess(Long resourceAuthorId, Long requesterId) {
        if (!resourceAuthorId.equals(requesterId)) {
            throw new CustomException(ErrorCode.NO_PERMISSION);
        }
    }
}
