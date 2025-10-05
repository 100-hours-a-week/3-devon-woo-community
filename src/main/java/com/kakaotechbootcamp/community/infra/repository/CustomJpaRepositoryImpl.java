package com.kakaotechbootcamp.community.infra.repository;

import com.kakaotechbootcamp.community.infra.csv.GenericCsvStorage;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.util.List;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Function;

/**
 * JPA Repository의 기본 구현을 제공하는 추상 클래스
 * Builder 패턴을 지원하는 함수형 프로그래밍 방식 사용
 */
@Slf4j
public abstract class CustomJpaRepositoryImpl<T, ID> implements CustomJpaRepository<T, ID> {

    protected final CrudStorage<T, ID> storage;
    private final Class<T> entityClass;

    @SuppressWarnings("unchecked")
    protected CustomJpaRepositoryImpl(Function<T, ID> idExtractor, BiFunction<T, ID, T> idSetter) {
        ParameterizedType parameterizedType = (ParameterizedType) getClass().getGenericSuperclass();
        this.entityClass = (Class<T>) parameterizedType.getActualTypeArguments()[0];
        this.storage = new GenericCsvStorage<>(entityClass, idExtractor, idSetter);
    }

    @Deprecated
    @SuppressWarnings("unchecked")
    protected CustomJpaRepositoryImpl() {
        ParameterizedType parameterizedType = (ParameterizedType) getClass().getGenericSuperclass();
        this.entityClass = (Class<T>) parameterizedType.getActualTypeArguments()[0];
        Field idField = findIdField(entityClass);
        idField.setAccessible(true);
        this.storage = new GenericCsvStorage<>(
                entityClass,
                entity -> extractIdReflection(entity, idField),
                (entity, id) -> createWithIdReflection(entity, id, idField)
        );
    }

    @Override
    public T save(T entity) {
        return storage.save(entity);
    }

    @Override
    public Optional<T> findById(ID id) {
        return storage.findById(id);
    }

    @Override
    public List<T> findAll() {
        return storage.findAll();
    }

    @Override
    public void deleteById(ID id) {
        storage.deleteById(id);
    }

    @Override
    public long count() {
        return storage.count();
    }

    @SuppressWarnings("unchecked")
    private ID extractIdReflection(T entity, Field idField) {
        try {
            return (ID) idField.get(entity);
        } catch (IllegalAccessException e) {
            log.error("Failed to extract ID from entity: {}", entity, e);
            throw new RuntimeException("Failed to extract ID from entity", e);
        }
    }

    private T createWithIdReflection(T entity, ID id, Field idField) {
        try {
            T newEntity = copyEntityReflection(entity);
            idField.set(newEntity, id);
            return newEntity;
        } catch (IllegalAccessException e) {
            log.error("Failed to create entity with ID: {}, id: {}", entity, id, e);
            throw new RuntimeException("Failed to create entity with ID", e);
        }
    }

    @Deprecated
    @SuppressWarnings("unchecked")
    private T copyEntityReflection(T entity) {
        try {
            return (T) entity.getClass().getDeclaredConstructor().newInstance();
        } catch (Exception e) {
            log.warn("Cannot create new instance, using original entity: {}", entity.getClass());
            return entity;
        }
    }

    protected Class<T> getEntityClass() {
        return entityClass;
    }

    private Field findIdField(Class<?> clazz) {
        try {
            return clazz.getDeclaredField("id");
        } catch (NoSuchFieldException e) {
            if (clazz.getSuperclass() != null) {
                return findIdField(clazz.getSuperclass());
            }
            throw new RuntimeException("Entity must have 'id' field: " + clazz.getName());
        }
    }
}