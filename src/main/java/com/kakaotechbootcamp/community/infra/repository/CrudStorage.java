package com.kakaotechbootcamp.community.infra.repository;

import java.util.List;
import java.util.Optional;

/**
 * 실제 데이터 저장소 인터페이스
 * CSV, Database, Memory 등 다양한 구현체를 위한 추상화
 */
public interface CrudStorage<T, ID> {

    T save(T entity);

    Optional<T> findById(ID id);

    List<T> findAll();

    void deleteById(ID id);

    long count();

    ID generateNextId();
}