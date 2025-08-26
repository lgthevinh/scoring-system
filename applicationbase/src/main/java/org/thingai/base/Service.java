package org.thingai.base;

import org.thingai.base.dao.Dao;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public abstract class Service {
    public static String name;
    public static String version;
    public static String appDirName;
    public static String configFile;
    public static String logFile;
    public static String daoType;

    protected String appDir;

    private Thread serviceThread;

    protected Service() {

    }

    public void init() {
        // Default values for the application properties
        version = version != null ? version : "1.0.0";
        appDirName = appDirName != null ? appDirName : "default_app";
        configFile = configFile != null ? configFile : "config.properties";
        logFile = logFile != null ? logFile : "application.log";

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

        } catch (Exception e) {
            System.err.println("Error creating application directories or files: " + e.getMessage());
        }

        onServiceInit();
    }

    public void run() {
        serviceThread = new Thread(this::onServiceRun);
        serviceThread.start();
    }

    public void shutdown() {
        if (serviceThread != null && serviceThread.isAlive()) {
            serviceThread.interrupt();
            try {
                serviceThread.join();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }

    protected abstract void onServiceInit();
    protected abstract void onServiceRun();
}
