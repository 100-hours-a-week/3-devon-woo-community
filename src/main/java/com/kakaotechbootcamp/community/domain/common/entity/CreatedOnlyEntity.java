package com.kakaotechbootcamp.community.domain.common.entity;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import org.springframework.data.annotation.CreatedDate;

import java.time.Instant;

@MappedSuperclass
@Getter
public abstract class CreatedOnlyEntity {

    @Column(updatable = false)
    @CreatedDate
    private Instant createdAt;
}
