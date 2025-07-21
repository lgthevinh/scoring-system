package org.thingai.vrc.scoringsystem.model.match;

import org.thingai.base.database.annotations.DaoField;
import org.thingai.base.database.annotations.DaoName;
import org.thingai.vrc.scoringsystem.types.MatchStatus;
import org.thingai.vrc.scoringsystem.types.MatchType;
import org.thingai.base.model.BaseModel;

import java.util.Map;

@DaoName(name = "match")
public class Match extends BaseModel<Match> {

    @DaoField(name = "match_name")
    private String matchName;

    @DaoField(name = "match_start_time")
    private String matchStartTime;

    @DaoField(name = "match_type")
    private MatchType matchType;

    @DaoField(name = "match_status")
    private MatchStatus matchStatus = MatchStatus.NOT_STARTED;

    @DaoField(name = "alliance_red_id")
    private int allianceRedId;

    @DaoField(name = "alliance_blue_id")
    private int allianceBlueId;

    public Match() {
        // Default constructor for serialization/deserialization
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

    public int getAllianceRed() {
        return allianceRedId;
    }

    public void setAllianceRed(int allianceRed) {
        this.allianceRedId = allianceRed;
    }

    public int getAllianceBlue() {
        return allianceBlueId;
    }

    public void setAllianceBlue(int allianceBlue) {
        this.allianceBlueId = allianceBlue;
    }

    @Override
    public Match fromMap(Map<String, Object> map) {
        Match match = new Match();
        match.setId((int) map.get("id"));
        match.setMatchName((String) map.get("match_name"));
        match.setMatchStartTime((String) map.get("match_start_time"));
        match.setMatchType(MatchType.valueOf((String) map.get("match_type")));
        match.setMatchStatus(MatchStatus.valueOf((String) map.get("match_status")));
        match.setAllianceRed((int) map.get("alliance_red_id"));
        match.setAllianceBlue((int) map.get("alliance_blue_id"));

        return match;
    }
}