package com.kakaotechbootcamp.community.domain.common;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * 모든 엔티티의 기본 클래스
 * 공통 필드인 생성/수정 시간 관리
 */
@Getter
@Setter
public abstract class BaseEntity {

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    public void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    public boolean isNew() {
        return this.createdAt == null;
    }
}
