package org.thingai.vrc.scoringsystem.database;

import org.thingai.vrc.scoringsystem.annotations.DaoField;
import org.thingai.vrc.scoringsystem.annotations.DaoName;
import org.thingai.vrc.scoringsystem.model.BaseModel;

import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

public class Database<T extends BaseModel> implements IDatabase<T> {
    private static final String TAG = "DB" + Database.class.getSimpleName(); // Tag for logging
    private final Class<T> modelClass; // Class type of the model

    public static String DB_URL; // Example database URL

    private Database(Class<T> modelClass) {
        this.modelClass = modelClass; // Store the model class type

        try (Connection connection = DriverManager.getConnection(DB_URL)) {
            connection.setAutoCommit(false);
            connection.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);

            // Create the table if it does not exist
            String tableName = modelClass.getAnnotation(DaoName.class).name();
            String createTableQuery = "CREATE TABLE IF NOT EXISTS " + tableName + " (id INT PRIMARY KEY, ";

            Field[] fields = modelClass.getDeclaredFields();
            for (Field field : fields) {
                if (field.isAnnotationPresent(DaoField.class)) {
                    DaoField daoField = field.getAnnotation(DaoField.class);
                    String fieldName = daoField.name();
                    String fieldType;

                    if (field.getType() == String.class) {
                        fieldType = "TEXT"; // Example for String type
                    } else if (field.getType() == int.class || field.getType() == Integer.class) {
                        fieldType = "INT"; // Example for Integer type
                    } else if (field.getType() == boolean.class || field.getType() == Boolean.class) {
                        fieldType = "INT"; // Example for Boolean type
                    } else if (field.getType() == double.class || field.getType() == Double.class) {
                        fieldType = "INT"; // Example for Double type
                    } else {
                        fieldType = "BLOB"; // Default to BLOB for other types
                    }

                    createTableQuery += fieldName + " " + fieldType + ", ";
                }
            }
            // Remove trailing comma and space
            if (createTableQuery.endsWith(", ")) {
                createTableQuery = createTableQuery.substring(0, createTableQuery.length() - 2);
            }
            createTableQuery += ");";

            connection.createStatement().execute(createTableQuery);
            connection.commit();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void insert(T model) {
        String query = "INSERT INTO " + modelClass.getAnnotation(DaoName.class).name() + " (";
        StringBuilder columns = new StringBuilder();
        StringBuilder placeholders = new StringBuilder();

        Map<String, Object> fieldValues = model.toMap(); // Convert model to a map of field values
        for (Map.Entry<String, Object> entry : fieldValues.entrySet()) {
            String fieldName = entry.getKey();
            Object fieldValue = entry.getValue();

            if (fieldValue == null) {
                continue; // Skip null values
            }

            columns.append(fieldName).append(", ");
            if (fieldValue instanceof BaseModel) {
                placeholders.append("'").append(((BaseModel) fieldValue).getId()).append("', ");
            } else {
                placeholders.append("'").append(fieldValue).append("', ");
            }
        }

        try (Connection connection = DriverManager.getConnection(DB_URL)) {
            if (columns.length() > 0) {
                columns.setLength(columns.length() - 2); // Remove trailing comma and space
                placeholders.setLength(placeholders.length() - 2); // Remove trailing comma and space
            }

            query += columns + ") VALUES (" + placeholders + ")";
            connection.createStatement().execute(query);
            connection.commit();
        } catch (SQLException e) {
            throw new RuntimeException("Error inserting record: " + model, e);
        }
    }

    @Override
    public void read(String id) {
        // Implementation for reading a record from the database by ID
        String query = "SELECT * FROM " + modelClass.getAnnotation(DaoName.class).name() + " WHERE id = ?";
        try (Connection connection = DriverManager.getConnection(DB_URL)) {
            // Prepare and execute the query to fetch the record
            // Convert the result set to an instance of T
        } catch (SQLException e) {
            throw new RuntimeException("Error reading record with ID: " + id, e);
        }
    }

    @Override
    public void update(T model) {
        // Implementation for updating a record in the database
    }

    @Override
    public void delete(String id) {
        // Implementation for deleting a record from the database by ID
    }

    @Override
    public List<T> query(String query) {
        // Implementation for querying records from the database based on a query string
        return null; // Placeholder return statement
    }

    public static <T extends BaseModel> IDatabase<T> getInstance(Class<T> modelClass) {
        if (DB_URL == null) {
            throw new IllegalStateException("Database URL is not set. Please set the database URL before accessing the database instance.");
        }
        return new Database<>(modelClass);
    }

    public static void insertQuery(String query) {
        try (Connection connection = DriverManager.getConnection(DB_URL)) {
            connection.createStatement().execute(query);
        } catch (SQLException e) {
            throw new RuntimeException("Error executing insert query: " + query, e);
        }
    }

    @Override
    public PreparedStatement prepareStatement(String sql) {
        try {
            Connection connection = DriverManager.getConnection(DB_URL);
            return connection.prepareStatement(sql);
        } catch (SQLException e) {
            throw new RuntimeException("Error preparing statement: " + sql, e);
        }
    }
}
