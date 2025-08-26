package org.thingai.base.dao;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/*
* Work in progress - File-based DAO implementation
*
* AI-generated initial version. Needs enhancements.
*/
@SuppressWarnings("work-in-progress")
public class DaoFile<T, K> extends Dao<T, String> {
    private Path rootDir;
    private Path entityDir;
    private Class<T> entityClass;
    private ObjectMapper objectMapper;

    public DaoFile(String rootDir) {
        this.rootDir = Path.of(rootDir);
        this.objectMapper = new ObjectMapper().enable(SerializationFeature.INDENT_OUTPUT);
    }

    public DaoFile(Class<T> entityClass) {
        this.entityClass = entityClass;
        this.entityDir = rootDir.resolve(entityClass.getSimpleName().toLowerCase());
    }

    public DaoFile(String rootDir, Class<T> entityClass) {
        this.rootDir = Path.of(rootDir);
        this.entityClass = entityClass;
        this.entityDir = this.rootDir.resolve(entityClass.getSimpleName().toLowerCase());
        this.objectMapper = new ObjectMapper().enable(SerializationFeature.INDENT_OUTPUT);
    }

    @Override
    public void initDao(Class[] classes) {
        try {
            if (!Files.exists(rootDir)) {
                Files.createDirectories(rootDir);
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to create root directory", e);
        }

        for (Class cls : classes) {
            Path dir = rootDir.resolve(cls.getSimpleName().toLowerCase());
            try {
                if (!Files.exists(dir)) {
                    Files.createDirectories(dir);
                }
            } catch (IOException e) {
                throw new RuntimeException("Failed to create entity directory", e);
            }
        }
    }

    @Override
    public void insert(T t) {
        try {
            String id = getIdFromEntity(t);
            Path filePath = entityDir.resolve(id + ".json");
            objectMapper.writeValue(filePath.toFile(), t);
        } catch (IOException e) {
            throw new RuntimeException("Insert failed", e);
        }
    }

    @Override
    public T read(String id) {
        try {
            Path filePath = entityDir.resolve(id + ".json");
            if (!Files.exists(filePath)) {
                return null;
            }
            return objectMapper.readValue(filePath.toFile(), entityClass);
        } catch (IOException e) {
            throw new RuntimeException("Read failed", e);
        }
    }

    @Override
    public void update(String id, T t) {
        insert(t); // overwrite JSON
    }

    @Override
    public void delete(String id) {
        try {
            Path filePath = entityDir.resolve(id + ".json");
            Files.deleteIfExists(filePath);
        } catch (IOException e) {
            throw new RuntimeException("Delete failed", e);
        }
    }

    @Override
    public List<T> query(String[] columns, String[] values) {
        try (Stream<Path> files = Files.list(entityDir)) {
            return files
                    .filter(path -> path.toString().endsWith(".json"))
                    .map(path -> {
                        try {
                            return objectMapper.readValue(path.toFile(), entityClass);
                        } catch (IOException e) {
                            return null;
                        }
                    })
                    .filter(entity -> entity != null && matches(entity, columns, values))
                    .collect(Collectors.toList());
        } catch (IOException e) {
            return List.of();
        }
    }

    @Override
    public List<T> query(String query) {
        try (Stream<Path> files = Files.list(entityDir)) {
            List<T> entities = new ArrayList<>();
            for (Path file : files.collect(Collectors.toList())) {
                if (file.toString().endsWith(".json")) {
                    entities.add(objectMapper.readValue(file.toFile(), entityClass));
                }
            }
            return entities;
        } catch (IOException e) {
            return List.of();
        }
    }

    // --- Helpers ---
    private String getIdFromEntity(T t) {
        try {
            var method = t.getClass().getMethod("getId");
            return (String) method.invoke(t);
        } catch (Exception e) {
            throw new RuntimeException("Entity must have getId() method returning String", e);
        }
    }

    private boolean matches(T entity, String[] columns, String[] values) {
        try {
            for (int i = 0; i < columns.length; i++) {
                var method = entity.getClass().getMethod("get" + capitalize(columns[i]));
                Object val = method.invoke(entity);
                if (val == null || !val.toString().equals(values[i])) {
                    return false;
                }
            }
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private String capitalize(String s) {
        if (s == null || s.isEmpty()) return s;
        return Character.toUpperCase(s.charAt(0)) + s.substring(1);
    }
}
