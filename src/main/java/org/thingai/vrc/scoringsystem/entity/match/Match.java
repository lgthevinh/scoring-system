package org.thingai.vrc.scoringsystem.entity.match;

public class Match {
    // Base information
    private String matchId; // auto-generate by matchType and mathNumber;
    private int matchType;
    private int matchNumber;

    // Time
    private String matchStartTime;
    private String matchEndTime;
}