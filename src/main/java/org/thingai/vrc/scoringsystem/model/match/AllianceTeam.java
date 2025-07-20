package org.thingai.vrc.scoringsystem.model.match;

import org.thingai.vrc.scoringsystem.annotations.DaoField;
import org.thingai.vrc.scoringsystem.model.BaseModel;

import java.util.Map;

public class AllianceTeam extends BaseModel<AllianceTeam> {

    @DaoField(name = "team_id")
    private String teamId;

    @DaoField(name = "alliance_id")
    private String allianceId;

    public AllianceTeam() {
        // Default constructor for serialization/deserialization
    }

    public AllianceTeam(String teamId, String allianceId) {
        this.teamId = teamId;
        this.allianceId = allianceId;
    }

    public String getTeamId() {
        return teamId;
    }

    public void setTeamId(String teamId) {
        this.teamId = teamId;
    }

    public String getAllianceId() {
        return allianceId;
    }

    public void setAllianceId(String allianceId) {
        this.allianceId = allianceId;
    }

    @Override
    public AllianceTeam fromMap(Map<String, Object> map) {
        AllianceTeam allianceTeam = new AllianceTeam(
            (String) map.get("team_id"),
            (String) map.get("alliance_id")
        );
        allianceTeam.setId((String) map.get("id")); // Assuming 'id' is a field in the map
        return allianceTeam;
    }
}
