package org.thingai.base.dao;

import java.sql.Connection;

public class DaoFactory {
    public static String type;
    public static String url;

    public static <T, ID> Dao<T, ID> getDao() {
        return switch (type) {
            case "sqlite" -> new DaoSqlite<>(getSqliteConnection());
            default -> throw new IllegalArgumentException("Unsupported DAO type: " + type);
        };
    }

    private static Connection getSqliteConnection() {
        try {
            Class.forName("org.sqlite.JDBC");
            return java.sql.DriverManager.getConnection(url);
        } catch (Exception e) {
            throw new RuntimeException("Failed to connect to SQLite database", e);
        }
    }
}
