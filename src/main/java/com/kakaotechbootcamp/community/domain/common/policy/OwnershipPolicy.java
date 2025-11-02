package com.kakaotechbootcamp.community.domain.common.policy;

import com.kakaotechbootcamp.community.common.exception.CustomException;
import com.kakaotechbootcamp.community.common.exception.code.CommonErrorCode;
import org.springframework.stereotype.Component;


@Component
public class OwnershipPolicy {

    /**
     * 리소스 소유권 검증
     */
    public void validateOwnership(Long resourceOwnerId, Long requesterId) {
        if (!resourceOwnerId.equals(requesterId)) {
            throw new CustomException(CommonErrorCode.NO_PERMISSION);
        }
    }
}
