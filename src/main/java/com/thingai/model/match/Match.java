package com.thingai.model.match;

import com.thingai.core.*;
import com.thingai.model.BaseModel;

@DaoName(name = "match")
public class Match extends BaseModel {

    @DaoField(name = "match_id")
    private long matchId;

    @DaoField(name = "match_name")
    private String matchName;

    @DaoField(name = "match_start_time")
    private String matchStartTime;

    @DaoField(name = "match_type")
    private MatchType matchType;

    @DaoField(name = "match_status")
    private MatchStatus matchStatus = MatchStatus.NOT_STARTED;

    @DaoField(name = "alliance_red_id")
    private Alliance allianceRed;

    @DaoField(name = "alliance_blue_id")
    private Alliance allianceBlue;

    public Match() {
        // Default constructor for serialization/deserialization
    }

    public long getMatchId() {
        return matchId;
    }

    public void setMatchId(long matchId) {
        this.matchId = matchId;
    }

    public String getMatchStartTime() {
        return matchStartTime;
    }

    public void setMatchStartTime(String matchStartTime) {
        this.matchStartTime = matchStartTime;
    }

    public MatchType getMatchType() {
        return matchType;
    }

    public void setMatchType(MatchType matchType) {
        this.matchType = matchType;
    }

    public MatchStatus getMatchStatus() {
        return matchStatus;
    }

    public void setMatchStatus(MatchStatus matchStatus) {
        this.matchStatus = matchStatus;
    }

    public String getMatchName() {
        return matchName;
    }

    public void setMatchName(String matchName) {
        this.matchName = matchName;
    }

    public Alliance getAllianceRed() {
        return allianceRed;
    }

    public void setAllianceRed(Alliance allianceRed) {
        this.allianceRed = allianceRed;
    }

    public Alliance getAllianceBlue() {
        return allianceBlue;
    }

    public void setAllianceBlue(Alliance allianceBlue) {
        this.allianceBlue = allianceBlue;
    }
}
