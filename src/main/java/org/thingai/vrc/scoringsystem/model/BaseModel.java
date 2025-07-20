package org.thingai.vrc.scoringsystem.model;

import org.thingai.vrc.scoringsystem.annotations.DaoField;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

public abstract class BaseModel<T> {

    @DaoField(name = "id")
    protected int id;

    public Map<String, Object> toMap() {
        Map<String, Object> map = new HashMap<>();
        Field[] fields = this.getClass().getDeclaredFields();

        for (Field field : fields) {
            String key;

            map.put("id", this.id); // Always include the id field

            if (field.isAnnotationPresent(DaoField.class)) {
                DaoField daoField = field.getAnnotation(DaoField.class);
                key = daoField.name();
            } else continue;

            try {
                field.setAccessible(true);
                Object value = field.get(this);

                if (value == null) {
                    continue; // Skip null values
                }

                map.put(key, value);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        return map;
    }

    public abstract T fromMap(Map<String, Object> map);

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

}