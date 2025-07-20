package org.thingai.vrc.scoringsystem.model.match;

import org.thingai.vrc.scoringsystem.annotations.DaoField;
import org.thingai.vrc.scoringsystem.model.BaseModel;

import java.util.Map;

public class AllianceTeam extends BaseModel<AllianceTeam> {

    @DaoField(name = "team_id")
    private int teamId;

    @DaoField(name = "alliance_id")
    private int allianceId;

    public AllianceTeam() {
        // Default constructor for serialization/deserialization
    }

    public AllianceTeam(int teamId, int allianceId) {
        this.teamId = teamId;
        this.allianceId = allianceId;
    }

    public int getTeamId() {
        return teamId;
    }

    public void setTeamId(int teamId) {
        this.teamId = teamId;
    }

    public int getAllianceId() {
        return allianceId;
    }

    public void setAllianceId(int allianceId) {
        this.allianceId = allianceId;
    }

    @Override
    public AllianceTeam fromMap(Map<String, Object> map) {
        AllianceTeam allianceTeam = new AllianceTeam(
            (int) map.get("team_id"),
            (int) map.get("alliance_id")
        );
        allianceTeam.setId((int) map.get("id")); // Assuming 'id' is a field in the map
        return allianceTeam;
    }
}
