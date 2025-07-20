package org.thingai.vrc.scoringsystem.database;

import org.thingai.vrc.scoringsystem.annotations.DaoName;
import org.thingai.vrc.scoringsystem.model.BaseModel;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

public class Database<T extends BaseModel> implements IDatabase<T> {
    private static final String TAG = "Database";
    private final Class<T> modelClass; // Class type of the model

    public static String DB_URL; // Example database URL

    public Database(Class<T> modelClass, String dbUrl) {
        this.modelClass = modelClass; // Store the model class type
        DB_URL = dbUrl; // Set the database URL
        try (Connection connection = DriverManager.getConnection(DB_URL)) {
            ; // Initialize connection
            connection.setAutoCommit(false); // Set auto-commit to false for transaction management
            connection.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE); // Set transaction isolation level
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

            if (fieldValue instanceof BaseModel) {
                columns.append(fieldName).append(", ");
                placeholders.append("'").append(((BaseModel) fieldValue).getId()).append("', ");
            } else {
                columns.append(fieldName).append(", ");
                placeholders.append("'").append(fieldValue).append("', ");
            }
        }

        System.out.println(query + columns.substring(0, columns.length() - 2) + ") VALUES ("
                + placeholders.substring(0, placeholders.length() - 2) + ")");
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
        return new Database<>(modelClass, DB_URL);
    }
}
