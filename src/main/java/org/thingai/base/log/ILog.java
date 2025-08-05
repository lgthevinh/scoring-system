package org.thingai.base.log;

public abstract class ILog {
    public static final int DEBUG = 1;
    public static final int INFO = 2;
    public static final int WARN = 3;
    public static final int ERROR = 4;

    public static int logLevel = DEBUG; // Default log level
    public static boolean ENABLE_LOGGING = false; // Flag to enable or disable logging

    protected static ILog instance;

    public static void d(String tag, String message) {
        if (ENABLE_LOGGING && logLevel <= DEBUG) {
            System.out.println("DEBUG: " + tag + ": " + message);
        }
    }

    public static void i(String tag, String message) {
        if (ENABLE_LOGGING && logLevel <= INFO) {
            System.out.println("INFO: " + tag + ": " + message);
        }
    }

    public static void w(String tag, String message) {
        if (ENABLE_LOGGING && logLevel <= WARN) {
            System.out.println("WARN: " + tag + ": " + message);
        }
    }

    public static void e(String tag, String message) {
        if (ENABLE_LOGGING && logLevel <= ERROR) {
            System.err.println("ERROR: " + tag + ": " + message);
        }
    }
}
