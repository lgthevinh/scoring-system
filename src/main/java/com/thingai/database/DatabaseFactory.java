package com.thingai.database;

import com.thingai.model.BaseModel;

public class DatabaseFactory {
    private static IDatabase<?> databaseInstance;

    private DatabaseFactory() {
        // Private constructor to prevent instantiation
    }

    public static <T extends BaseModel> IDatabase<T> getDatabaseInstance(Class<T> modelClass) {
        if (databaseInstance == null) {
            synchronized (DatabaseFactory.class) {
                if (databaseInstance == null) {
                    databaseInstance = new Database<>(modelClass);
                }
            }
        }
        return (IDatabase<T>) databaseInstance;
    }
}
