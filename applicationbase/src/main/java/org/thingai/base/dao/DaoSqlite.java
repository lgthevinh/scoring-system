package org.thingai.base.dao;

import org.thingai.base.dao.annotations.DaoColumn;
import org.thingai.base.dao.annotations.DaoTable;

import java.lang.reflect.Field;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DaoSqlite<T, K> extends Dao<T, K> {
    private static Connection connection;
    private static String dbPath;
    private Class<T> clazz;

    public DaoSqlite(Class<T> clazz) {
        this.clazz = clazz;
    }

    public DaoSqlite(String dbPath) {
        this.dbPath = dbPath;
    }

    public DaoSqlite(Class<T> clazz, String dbPath) {
        this.clazz = clazz;
        this.dbPath = dbPath;
    }

    private static void setupConnection(String dbPath) {
        try {
            if (connection == null || connection.isClosed()) {
                Class.forName("org.sqlite.JDBC");
                connection = java.sql.DriverManager.getConnection("jdbc:sqlite:" + dbPath);
                System.out.println("SQLite connection established to " + dbPath);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static Field[] getAllFields(Class clazz) {
        List<Field> fields = new ArrayList<>();
        while (clazz != null) {
            fields.addAll(Arrays.asList(clazz.getDeclaredFields()));
            clazz = clazz.getSuperclass();
        }

        // Filter out fields that are not annotated with DaoColumn
        fields.removeIf(field -> !field.isAnnotationPresent(DaoColumn.class));

        return fields.toArray(new Field[0]);
    }

    @Override
    public void initDao(Class[] classes) {
        setupConnection(dbPath);

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
                DaoColumn daoColumn = field.getAnnotation(DaoColumn.class);
                if (daoColumn != null) {
                    if (daoColumn.name().isEmpty()) {
                        query += field.getName() + " ";
                    } else {
                        query += daoColumn.name() + " ";
                    }

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

                    // constrains
                    if (daoColumn.primaryKey()) {
                        query += " PRIMARY KEY";
                    }
                    if (!daoColumn.nullable()) {
                        query += " NOT NULL";
                    }
                    if (daoColumn.unique()) {
                        query += " UNIQUE";
                    }
                    if (daoColumn.autoIncrement()) {
                        query += " AUTOINCREMENT";
                    }
                    if (!daoColumn.defaultValue().isEmpty()) {
                        query += "DEFAULT " + daoColumn.defaultValue() + " ";
                    }
                }
                query += ", ";
            }

            // Remove trailing comma and space
            if (query.endsWith(", ")) {
                query = query.substring(0, query.length() - 2);
            }
            query += ");";
            System.out.println("Executing query: " + query);

            // execute query
            try {
                if (connection != null && !connection.isClosed()) {
                    connection.createStatement().execute(query);
                } else {
                    throw new IllegalStateException("Database connection is not established.");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

    @Override
    public void insert(T t) {
        if (t == null) {
            throw new IllegalArgumentException("Cannot insert null object.");
        }
        String query = "INSERT INTO " + clazz.getAnnotation(DaoTable.class).name() + " (";
        StringBuilder columns = new StringBuilder();
        StringBuilder placeholders = new StringBuilder();

        Field[] fields = getAllFields(clazz);
        for (Field field : fields) {
            DaoColumn daoColumn = field.getAnnotation(DaoColumn.class);
            if (daoColumn != null) {
                columns.append(daoColumn.name().isEmpty() ? field.getName() : daoColumn.name()).append(", ");
                placeholders.append("?, ");
            }
        }

        // Remove trailing comma and space
        if (columns.length() > 0) {
            columns.setLength(columns.length() - 2);
            placeholders.setLength(placeholders.length() - 2);
        }

        query += columns + ") VALUES (" + placeholders + ");";

        try {
            if (connection != null && !connection.isClosed()) {
                var preparedStatement = connection.prepareStatement(query);
                int index = 1;
                for (Field field : fields) {
                    DaoColumn daoColumn = field.getAnnotation(DaoColumn.class);
                    if (daoColumn != null) {
                        field.setAccessible(true);
                        Object value = field.get(t);
                        preparedStatement.setObject(index++, value);
                    }
                }
                System.out.println("Executing query: " + preparedStatement.toString());

                preparedStatement.executeUpdate();
            } else {
                throw new IllegalStateException("Database connection is not established.");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public T read(K id) {
        if (id == null) {
            throw new IllegalArgumentException("Cannot read null object.");
        }

        String query = "SELECT * FROM " + clazz.getAnnotation(DaoTable.class).name() + " WHERE id = ?";
        try {
            if (connection != null && !connection.isClosed()) {
                var preparedStatement = connection.prepareStatement(query);
                preparedStatement.setObject(1, id);
                var resultSet = preparedStatement.executeQuery();
                if (resultSet.next()) {
                    T instance = clazz.getDeclaredConstructor().newInstance();
                    Field[] fields = getAllFields(clazz);
                    for (Field field : fields) {
                        DaoColumn daoColumn = field.getAnnotation(DaoColumn.class);
                        if (daoColumn != null) {
                            field.setAccessible(true);
                            field.set(instance, resultSet.getObject(daoColumn.name().isEmpty() ? field.getName() : daoColumn.name()));
                        }
                    }
                    return instance;
                }
            } else {
                throw new IllegalStateException("Database connection is not established.");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null; // or throw an exception if not found
    }

    @Override
    public List<T> query(String[] column, String[] value) {
        if (column == null || value == null) {
            throw new IllegalArgumentException("Cannot read with null column or value.");
        }

        String query = "SELECT * FROM " + clazz.getAnnotation(DaoTable.class).name() + " WHERE ";
        StringBuilder whereClause = new StringBuilder();
        for (int i = 0; i < column.length; i++) {
            whereClause.append(column[i]).append(" = ?");
            if (i < column.length - 1) {
                whereClause.append(" AND ");
            }
        }
        query += whereClause + ";";

        List<T> results = new ArrayList<>();
        try {
            if (connection != null && !connection.isClosed()) {
                var preparedStatement = connection.prepareStatement(query);
                for (int i = 0; i < value.length; i++) {
                    preparedStatement.setObject(i + 1, value[i]);
                }
                var resultSet = preparedStatement.executeQuery();
                while (resultSet.next()) {
                    T instance = clazz.getDeclaredConstructor().newInstance();
                    Field[] fields = getAllFields(clazz);
                    for (Field field : fields) {
                        DaoColumn daoColumn = field.getAnnotation(DaoColumn.class);
                        if (daoColumn != null) {
                            field.setAccessible(true);
                            field.set(instance, resultSet.getObject(daoColumn.name().isEmpty() ? field.getName() : daoColumn.name()));
                        }
                    }
                    results.add(instance);
                }
                return results;
            } else {
                throw new IllegalStateException("Database connection is not established.");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null; // or throw an exception if not found
    }

    @Override
    public void update(K id, T t) {
        if (id == null || t == null) {
            throw new IllegalArgumentException("Cannot update with null id or object.");
        }

        String query = "UPDATE " + clazz.getAnnotation(DaoTable.class).name() + " SET ";
        StringBuilder setClause = new StringBuilder();

        Field[] fields = getAllFields(clazz);
        for (Field field : fields) {
            DaoColumn daoColumn = field.getAnnotation(DaoColumn.class);
            if (daoColumn != null && !daoColumn.primaryKey()) {
                setClause.append(daoColumn.name().isEmpty() ? field.getName() : daoColumn.name()).append(" = ?, ");
            }
        }

        // Remove trailing comma and space
        if (setClause.length() > 0) {
            setClause.setLength(setClause.length() - 2);
        }

        query += setClause + " WHERE id = ?;";

        try {
            if (connection != null && !connection.isClosed()) {
                var preparedStatement = connection.prepareStatement(query);
                int index = 1;
                for (Field field : fields) {
                    DaoColumn daoColumn = field.getAnnotation(DaoColumn.class);
                    if (daoColumn != null && !daoColumn.primaryKey()) {
                        field.setAccessible(true);
                        Object value = field.get(t);
                        preparedStatement.setObject(index++, value);
                    }
                }
                preparedStatement.setObject(index, id); // set the id at the end
                preparedStatement.executeUpdate();
            } else {
                throw new IllegalStateException("Database connection is not established.");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void delete(K id) {
        if (id == null) {
            throw new IllegalArgumentException("Cannot delete with null id.");
        }

        String query = "DELETE FROM " + clazz.getAnnotation(DaoTable.class).name() + " WHERE id = ?;";
        try {
            if (connection != null && !connection.isClosed()) {
                var preparedStatement = connection.prepareStatement(query);
                preparedStatement.setObject(1, id);
                preparedStatement.executeUpdate();
            } else {
                throw new IllegalStateException("Database connection is not established.");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public List<T> query(String query) {
        List<T> results = new ArrayList<>();
        try {
            if (connection != null && !connection.isClosed()) {
                var preparedStatement = connection.prepareStatement(query);
                var resultSet = preparedStatement.executeQuery();
                while (resultSet.next()) {
                    T instance = clazz.getDeclaredConstructor().newInstance();
                    Field[] fields = getAllFields(clazz);
                    for (Field field : fields) {
                        DaoColumn daoColumn = field.getAnnotation(DaoColumn.class);
                        if (daoColumn != null) {
                            field.setAccessible(true);
                            field.set(instance, resultSet.getObject(daoColumn.name().isEmpty() ? field.getName() : daoColumn.name()));
                        }
                    }
                    results.add(instance);
                }
            } else {
                throw new IllegalStateException("Database connection is not established.");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return results;
    }

}

