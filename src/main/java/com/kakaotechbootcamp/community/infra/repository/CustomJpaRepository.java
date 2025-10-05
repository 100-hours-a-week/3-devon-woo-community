package com.kakaotechbootcamp.community.infra.repository;

import java.util.List;
import java.util.Optional;

/**
 * 기본적인 JPA Repository 인터페이스
 * 핵심 CRUD 기능만 제공
 */
public interface CustomJpaRepository<T, ID> {

    T save(T entity);

    Optional<T> findById(ID id);

    List<T> findAll();

    void deleteById(ID id);

    long count();
}