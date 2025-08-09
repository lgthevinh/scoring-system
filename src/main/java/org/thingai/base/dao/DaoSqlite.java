package org.thingai.base.dao;

import org.thingai.base.dao.annotations.DaoColumn;
import org.thingai.base.dao.annotations.DaoIgnore;
import org.thingai.base.dao.annotations.DaoTable;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DaoSqlite<T, K> extends Dao<T, K> {

    private static Field[] getAllFields(Class clazz) {
        List<Field> fields = new ArrayList<>();
        while (clazz != null) {
            fields.addAll(Arrays.asList(clazz.getDeclaredFields()));
            clazz = clazz.getSuperclass();
        }
        return fields.toArray(new Field[0]);
    }

    public static void createTables(Class[] classes) {
        for (Class clazz : classes) {

            DaoTable daoTable = (DaoTable) clazz.getAnnotation(DaoTable.class);

            String query = "CREATE TABLE IF NOT EXISTS ";

            if (daoTable != null) {
                query += daoTable.name();
            } else {
                query += clazz.getSimpleName();
            }

            query += " (";

            Field[] fields = getAllFields(clazz);
            for (Field field : fields) {
                if (field.isAnnotationPresent(DaoIgnore.class)) {
                    continue; // skip this field
                }

                // table name
                DaoColumn daoColumn = field.getAnnotation(DaoColumn.class);
                if (daoColumn != null && !daoColumn.name().isEmpty()) {
                    query += daoColumn.name() + " ";

                    // constraint
                    if (daoColumn.primaryKey()) {
                        query += "PRIMARY KEY ";
                    }
                    if (!daoColumn.nullable()) {
                        query += "NOT NULL ";
                    }
                    if (daoColumn.autoIncrement()) {
                        query += "AUTOINCREMENT ";
                    }
                    if (daoColumn.defaultValue() != null) {
                        query += "DEFAULT " + daoColumn.defaultValue() + " ";
                    }
                } else {
                    query += field.getName() + " ";

                    // column type
                    if (field.getType() == String.class) {
                        query += "TEXT";
                    } else if (field.getType() == int.class || field.getType() == Integer.class) {
                        query += "INTEGER";
                    } else if (field.getType() == boolean.class || field.getType() == Boolean.class) {
                        query += "INTEGER";
                    } else if (field.getType() == double.class || field.getType() == Double.class) {
                        query += "REAL";
                    } else {
                        query += "BLOB";
                    }
                }

                query += ", ";
            }

            // Remove trailing comma and space
            if (query.endsWith(", ")) {
                query = query.substring(0, query.length() - 2);
            }

            query += ");";
            System.out.println(query);
        }

    }

    @Override
    public void insert(T t) {

    }

    @Override
    public T read(K id) {
        return null;
    }

    @Override
    public void update(K id, T t) {

    }

    @Override
    public void delete(K id) {

    }

    @Override
    public List<T> query(String query) {
        return List.of();
    }
}
