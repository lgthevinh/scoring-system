package org.thingai.scoringsystem;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Main {
    public static void main(String[] args) {
        String home = System.getProperty("user.home");
        Path configDir = Paths.get(home, ".thingai", "scoringsystem");
        Path databaseFile = configDir.resolve("database.db");

        // Ensure the configuration directory exists
        try {
            if (Files.notExists(configDir)) {
                Files.createDirectories(configDir);
            }

            if (Files.notExists(databaseFile)) {
                Files.createFile(databaseFile);
            }


        } catch (Exception e) {
            System.err.println("Could not insert configuration directory: " + e.getMessage());
            return;
        }

    }
}