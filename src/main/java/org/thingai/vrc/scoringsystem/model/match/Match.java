package org.thingai.vrc.scoringsystem.model.match;

import org.thingai.vrc.scoringsystem.annotations.DaoField;
import org.thingai.vrc.scoringsystem.annotations.DaoName;
import org.thingai.vrc.scoringsystem.types.MatchStatus;
import org.thingai.vrc.scoringsystem.types.MatchType;
import org.thingai.vrc.scoringsystem.model.BaseModel;

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
    private Alliance allianceRed;

    @DaoField(name = "alliance_blue_id")
    private Alliance allianceBlue;

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

    @Override
    public Match fromMap(Map<String, Object> map) {
        Match match = new Match();
        match.setId((String) map.get("id"));
        match.setMatchName((String) map.get("match_name"));
        match.setMatchStartTime((String) map.get("match_start_time"));
        match.setMatchType(MatchType.valueOf((String) map.get("match_type")));
        match.setMatchStatus(MatchStatus.valueOf((String) map.get("match_status")));

        // Deserialize alliances
        if (map.containsKey("alliance_red_id")) {
            Alliance allianceRed = new Alliance();
            allianceRed.fromMap((Map<String, Object>) map.get("alliance_red_id"));
            match.setAllianceRed(allianceRed);
        }

        if (map.containsKey("alliance_blue_id")) {
            Alliance allianceBlue = new Alliance();
            allianceBlue.fromMap((Map<String, Object>) map.get("alliance_blue_id"));
            match.setAllianceBlue(allianceBlue);
        }

        return match;
    }
}
