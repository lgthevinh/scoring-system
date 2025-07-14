package com.thingai.model;

import com.thingai.core.DaoField;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

public abstract class BaseModel {

    public Map<String, Object> toMap() {
        Map<String, Object> map = new HashMap<>();
        Field[] fields = this.getClass().getDeclaredFields();

        for (Field field : fields) {
            String key;
            if (field.isAnnotationPresent(DaoField.class)) {
                DaoField daoField = field.getAnnotation(DaoField.class);
                key = daoField.name();
            } else {
                key = field.getName();
            }

            try {
                field.setAccessible(true);
                Object value = field.get(this);
                map.put(key, value);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        return map;
    }

}