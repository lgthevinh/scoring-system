package org.thingai.vrc.scoringsystem.database;

import org.thingai.vrc.scoringsystem.model.BaseModel;
import java.util.List;

public interface IDatabase<T extends BaseModel> {
    void insert(T model);
    void read(String id);
    void update(T model);
    void delete(String id);

    List<T> query(String query);
}
