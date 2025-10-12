package com.kakaotechbootcamp.community.infra.csv;

import com.kakaotechbootcamp.community.domain.common.BaseEntity;
import com.kakaotechbootcamp.community.infra.repository.CrudStorage;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.function.Function;
import java.util.function.BiFunction;

/**
 * CSV 파일 기반 제네릭 저장소
 */
@Slf4j
public class GenericCsvStorage<T, ID> implements CrudStorage<T, ID> {

    private static final String DATA_DIR = "data";
    private static final ConcurrentHashMap<String, ReadWriteLock> locks = new ConcurrentHashMap<>();
    private static final ConcurrentHashMap<String, AtomicLong> idGenerators = new ConcurrentHashMap<>();

    private final Class<T> entityClass;
    private final Function<T, ID> idExtractor;
    private final BiFunction<T, ID, T> idSetter;
    private final Path filePath;
    private final ReadWriteLock lock;
    private final AtomicLong idGenerator;
    private final String entityName;

    public GenericCsvStorage(Class<T> entityClass,
                           Function<T, ID> idExtractor,
                           BiFunction<T, ID, T> idSetter) {
        this.entityClass = entityClass;
        this.idExtractor = idExtractor;
        this.idSetter = idSetter;
        this.entityName = entityClass.getSimpleName().toLowerCase();
        this.filePath = Paths.get(DATA_DIR, entityName + ".csv");
        this.lock = locks.computeIfAbsent(entityName, k -> new ReentrantReadWriteLock());
        this.idGenerator = idGenerators.computeIfAbsent(entityName, k -> new AtomicLong(loadMaxId()));

        initializeFile();
    }

    @Override
    public T save(T entity) {
        lock.writeLock().lock();
        try {
            List<String> lines = readAllLines();

            if (lines.isEmpty()) {
                lines.add(createHeader());
            }

            ID id = idExtractor.apply(entity);
            boolean isUpdate = id != null;

            if (entity instanceof BaseEntity) {
                BaseEntity baseEntity = (BaseEntity) entity;
                if (!isUpdate) {
                    baseEntity.onCreate();
                } else {
                    baseEntity.onUpdate();
                }
            }

            if (!isUpdate) {
                id = generateNextId();
                entity = idSetter.apply(entity, id);
                lines.add(entityToCsvLine(entity));
            } else {
                boolean updated = false;
                for (int i = 1; i < lines.size(); i++) {
                    T existing = csvLineToEntity(lines.get(i));
                    if (idExtractor.apply(existing).equals(id)) {
                        lines.set(i, entityToCsvLine(entity));
                        updated = true;
                        break;
                    }
                }
                if (!updated) {
                    lines.add(entityToCsvLine(entity));
                }
            }

            writeAllLines(lines);
            return entity;

        } catch (IOException e) {
            log.error("Failed to save entity: {}", entity, e);
            throw new RuntimeException("Failed to save entity", e);
        } finally {
            lock.writeLock().unlock();
        }
    }

    @Override
    public Optional<T> findById(ID id) {
        lock.readLock().lock();
        try {
            List<String> lines = readAllLines();

            return lines.stream()
                    .skip(1)
                    .map(this::csvLineToEntity)
                    .filter(entity -> idExtractor.apply(entity).equals(id))
                    .findFirst();

        } catch (IOException e) {
            log.error("Failed to find entity by id: {}", id, e);
            return Optional.empty();
        } finally {
            lock.readLock().unlock();
        }
    }

    @Override
    public List<T> findAll() {
        lock.readLock().lock();
        try {
            List<String> lines = readAllLines();

            return lines.stream()
                    .skip(1)
                    .map(this::csvLineToEntity)
                    .toList();

        } catch (IOException e) {
            log.error("Failed to find all entities", e);
            return new ArrayList<>();
        } finally {
            lock.readLock().unlock();
        }
    }

    @Override
    public List<T> findAllById(Iterable<ID> ids) {
        lock.readLock().lock();
        try {
            List<String> lines = readAllLines();
            List<ID> idList = new ArrayList<>();
            ids.forEach(idList::add);

            return lines.stream()
                    .skip(1)
                    .map(this::csvLineToEntity)
                    .filter(entity -> idList.contains(idExtractor.apply(entity)))
                    .toList();

        } catch (IOException e) {
            log.error("Failed to find entities by ids: {}", ids, e);
            return new ArrayList<>();
        } finally {
            lock.readLock().unlock();
        }
    }

    @Override
    public void deleteById(ID id) {
        lock.writeLock().lock();
        try {
            List<String> lines = readAllLines();
            List<String> newLines = new ArrayList<>();
            newLines.add(lines.get(0));

            lines.stream()
                    .skip(1)
                    .filter(line -> !idExtractor.apply(csvLineToEntity(line)).equals(id))
                    .forEach(newLines::add);

            writeAllLines(newLines);

        } catch (IOException e) {
            log.error("Failed to delete entity by id: {}", id, e);
            throw new RuntimeException("Failed to delete entity", e);
        } finally {
            lock.writeLock().unlock();
        }
    }

    @Override
    public long count() {
        lock.readLock().lock();
        try {
            List<String> lines = readAllLines();
            return Math.max(0, lines.size() - 1);

        } catch (IOException e) {
            log.error("Failed to count entities", e);
            return 0;
        } finally {
            lock.readLock().unlock();
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public ID generateNextId() {
        return (ID) Long.valueOf(idGenerator.incrementAndGet());
    }

    private void initializeFile() {
        try {
            if (!Files.exists(filePath.getParent())) {
                Files.createDirectories(filePath.getParent());
            }

            if (!Files.exists(filePath)) {
                List<String> lines = new ArrayList<>();
                lines.add(createHeader());
                writeAllLines(lines);
            }

        } catch (IOException e) {
            log.error("Failed to initialize CSV file for {}", entityName, e);
            throw new RuntimeException("Failed to initialize CSV file", e);
        }
    }

    private List<String> readAllLines() throws IOException {
        if (!Files.exists(filePath)) {
            List<String> lines = new ArrayList<>();
            lines.add(createHeader());
            return lines;
        }
        return new ArrayList<>(Files.readAllLines(filePath));
    }

    private void writeAllLines(List<String> lines) throws IOException {
        Files.write(filePath, lines);
    }

    private String createHeader() {
        List<String> fieldNames = new ArrayList<>();

        Class<?> currentClass = entityClass;
        while (currentClass != null && currentClass != Object.class) {
            Field[] fields = currentClass.getDeclaredFields();
            for (Field field : fields) {
                fieldNames.add(field.getName());
            }
            currentClass = currentClass.getSuperclass();
        }

        return String.join(",", fieldNames);
    }

    private String entityToCsvLine(T entity) {
        List<String> values = new ArrayList<>();

        Class<?> currentClass = entityClass;
        while (currentClass != null && currentClass != Object.class) {
            Field[] fields = currentClass.getDeclaredFields();
            for (Field field : fields) {
                field.setAccessible(true);
                try {
                    Object value = field.get(entity);
                    values.add(value == null ? "" : value.toString());
                } catch (IllegalAccessException e) {
                    values.add("");
                }
            }
            currentClass = currentClass.getSuperclass();
        }

        return String.join(",", values);
    }

    private T csvLineToEntity(String line) {
        String[] parts = line.split(",", -1);

        try {
            T entity = entityClass.getDeclaredConstructor().newInstance();

            List<Field> allFields = new ArrayList<>();
            Class<?> currentClass = entityClass;
            while (currentClass != null && currentClass != Object.class) {
                Field[] fields = currentClass.getDeclaredFields();
                for (Field field : fields) {
                    allFields.add(field);
                }
                currentClass = currentClass.getSuperclass();
            }

            for (int i = 0; i < Math.min(parts.length, allFields.size()); i++) {
                Field field = allFields.get(i);
                field.setAccessible(true);
                String value = parts[i];

                if (!value.isEmpty()) {
                    Object convertedValue = convertValue(value, field.getType());
                    field.set(entity, convertedValue);
                }
            }

            return entity;

        } catch (Exception e) {
            log.error("Failed to convert CSV line to entity: {}", line, e);
            throw new RuntimeException("Failed to convert CSV line to entity", e);
        }
    }

    private Object convertValue(String value, Class<?> targetType) {
        if (targetType == String.class) {
            return value;
        } else if (targetType == Long.class || targetType == long.class) {
            return Long.valueOf(value);
        } else if (targetType == Integer.class || targetType == int.class) {
            return Integer.valueOf(value);
        } else if (targetType == Boolean.class || targetType == boolean.class) {
            return Boolean.valueOf(value);
        } else if (targetType == LocalDateTime.class) {
            return LocalDateTime.parse(value);
        }

        return value;
    }

    private long loadMaxId() {
        try {
            List<String> lines = readAllLines();

            return lines.stream()
                    .skip(1) // 헤더 스킵
                    .mapToLong(line -> {
                        try {
                            T entity = csvLineToEntity(line);
                            ID id = idExtractor.apply(entity);
                            return id instanceof Long ? (Long) id : 0L;
                        } catch (Exception e) {
                            return 0L;
                        }
                    })
                    .max()
                    .orElse(0L);

        } catch (IOException e) {
            log.warn("Failed to load max ID for {}, starting from 0", entityName, e);
            return 0L;
        }
    }
}