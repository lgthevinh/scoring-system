package org.thingai.scoringsystem.entity.match;

import org.thingai.base.dao.annotations.DaoTable;

@DaoTable(name = "match")
public class Match {
    // Base information
    private String matchId; // auto-generate by matchType and mathNumber;
    private int matchType;
    private int matchNumber;

    // Time
    private String matchStartTime;
    private String matchEndTime;
}