package org.thingai.base.database;

import java.util.List;

public interface IDao<T, K> {
    void insert(T t);
    T read(K id);
    void update(K id, T t);
    void delete(K id);

    List<T> query(String query);
}
