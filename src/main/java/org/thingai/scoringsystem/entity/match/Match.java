package org.thingai.scoringsystem.entity.match;

import org.thingai.base.dao.annotations.DaoColumn;
import org.thingai.base.dao.annotations.DaoTable;

@DaoTable(name = "match")
public class Match {
    // Base information
    @DaoColumn(name = "id", primaryKey = true)
    private String matchId; // auto-generate by matchType and mathNumber;

    @DaoColumn(name = "match_type")
    private int matchType;

    @DaoColumn(name = "match_number")
    private int matchNumber;

    // Time
    @DaoColumn(name = "match_start_time")
    private String matchStartTime;

    @DaoColumn(name = "match_end_time")
    private String matchEndTime;
}