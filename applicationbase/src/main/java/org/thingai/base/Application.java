package org.thingai.base;

import org.thingai.base.dao.Dao;
import org.thingai.base.dao.DaoFactory;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Application {
    public static String name;
    public static String version;
    public static String appDirName;
    public static String configFile;
    public static String logFile;
    public static String daoType;

    protected String appDir;
    protected static Dao dao;

    public void init() {
        // Default values for the application properties
        version = version != null ? version : "1.0.0";
        appDirName = appDirName != null ? appDirName : "default_app";
        configFile = configFile != null ? configFile : "config.properties";
        logFile = logFile != null ? logFile : "application.log";
        daoType = daoType != null ? daoType : Dao.SQLITE;

        String home = System.getProperty("user.home");
        appDir = Paths.get(home, ".thingai", appDirName).toString();

        Path appDirPath = Paths.get(appDir);
        Path configFilePath = Paths.get(appDir, "config.properties");
        Path logFilePath = Paths.get(appDir, "application.log");
        try {
            if (!Files.exists(appDirPath)) {
                Files.createDirectories(appDirPath);
            }
            if (!Files.exists(configFilePath)) {
                Files.createFile(configFilePath);
            }
            if (!Files.exists(logFilePath)) {
                Files.createFile(logFilePath);
            }
            if (daoType.equals(Dao.SQLITE)) {
                Path dbFile = Paths.get(appDir, appDirName + ".db");

                DaoFactory.type = daoType; // Set the DAO type for the factory
                DaoFactory.url = "jdbc:sqlite:" + Paths.get(appDir, appDirName + ".db"); // Set the URL for SQLite
                dao = DaoFactory.getDao();

                if (!Files.exists(dbFile)) {
                    Files.createFile(dbFile);
                }
            } else {
                throw new IllegalArgumentException("Unsupported DAO type: " + daoType);
            }

        } catch (Exception e) {
            System.err.println("Error creating application directories or files: " + e.getMessage());
        }
    }

    public void start() {
        System.out.println("Starting application: " + name + " (Version: " + version + ")");
        // Additional startup logic can be added here

        System.out.println("Application directory: " + appDir);
        System.out.println("Configuration file: " + configFile);
        System.out.println("Log file: " + logFile);
        System.out.println("DAO Type: " + daoType);
    }

    public void facDao(Class<?>[] entityClasses) {
        if (dao == null) {
            throw new IllegalStateException("DAO is not initialized. Call init() before accessing the DAO.");
        }
        dao.facDao(entityClasses);
    }
}
