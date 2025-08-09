package org.thingai.base.dao;

import java.util.List;

public abstract class Dao<T, K> {

    public abstract void insert(T t);
    public abstract  T read(K id);
    public abstract void update(K id, T t);
    public abstract void delete(K id);

    public abstract List<T> query(String query);
}
