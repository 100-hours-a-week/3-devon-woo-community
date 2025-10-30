package com.kakaotechbootcamp.community.application.validator;

import com.kakaotechbootcamp.community.common.exception.CustomException;
import com.kakaotechbootcamp.community.common.exception.code.PostErrorCode;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

@Service
public class AccessPolicyValidator {

    public void checkAccess(Long resourceAuthorId, Long requesterId) {
        if (!resourceAuthorId.equals(requesterId)) {
            throw new CustomException(PostErrorCode.NO_PERMISSION);
        }
    }
}
