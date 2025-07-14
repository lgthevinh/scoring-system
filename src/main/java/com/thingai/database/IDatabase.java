package com.thingai.database;

import com.thingai.model.BaseModel;
import java.util.List;

public interface IDatabase<T extends BaseModel> {
    void create();
    void read(String id);
    void update(T model);
    void delete(String id);

    List<T> query(String query);
}
