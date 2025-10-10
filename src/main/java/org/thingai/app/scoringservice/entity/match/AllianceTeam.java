package org.thingai.app.scoringservice.entity.match;

import org.thingai.base.dao.annotations.DaoColumn;
import org.thingai.base.dao.annotations.DaoTable;

@DaoTable(name = "alliance_team")
public class AllianceTeam {

    @DaoColumn(name = "id")
    private String allianceId;

    @DaoColumn(name = "teamId")
    private String teamId;

    public String getAllianceId() {
        return allianceId;
    }

    public void setAllianceId(String allianceId) {
        this.allianceId = allianceId;
    }

    public String getTeamId() {
        return teamId;
    }

    public void setTeamId(String teamId) {
        this.teamId = teamId;
    }
}
