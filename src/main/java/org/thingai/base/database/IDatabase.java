package org.thingai.base.database;

import java.sql.PreparedStatement;
import java.util.List;

public interface IDatabase<T> {
    void insert(T model);
    void read(String id);
    void update(T model);
    void delete(String id);

    List<T> query(String query);
    PreparedStatement prepareStatement(String sql);
}
