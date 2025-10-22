package org.thingai.app.scoringservice.define;

public class ErrorCode {
    // DAO ERROR CODE
    public static final int CREATE_FAILED = 101;
    public static final int UPDATE_FAILED = 102;
    public static final int DELETE_FAILED = 103;
    public static final int RETRIEVE_FAILED = 104;
    public static final int NOT_FOUND = 105;

    // SYSTEM CODE
    public static final int FAILED_TO_START = 111;

    public static final int CUSTOM_ERR = 255;
}
