package org.thingai.base.dao;

public class DaoFactory {
    public static String type;

    public static <T, ID> Dao<T, ID> getDao() {
        return switch (type) {
            case "sqlite" -> new DaoSqlite<>();
            default -> throw new IllegalArgumentException("Unsupported DAO type: " + type);
        };
    }
}
