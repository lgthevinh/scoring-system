package org.thingai.app.scoringservice.entity.team;

import org.thingai.base.dao.annotations.DaoColumn;
import org.thingai.base.dao.annotations.DaoTable;

@DaoTable(name = "team")
public class Team {
    @DaoColumn(name = "id", primaryKey = true)
    private String teamId;

    @DaoColumn(name = "name")
    private String teamName;

    @DaoColumn(name = "number")
    private String teamSchool;

    @DaoColumn(name = "region")
    private String teamRegion;

    public String getTeamId() {
        return teamId;
    }

    public void setTeamId(String teamId) {
        this.teamId = teamId;
    }

    public String getTeamName() {
        return teamName;
    }

    public void setTeamName(String teamName) {
        this.teamName = teamName;
    }

    public String getTeamSchool() {
        return teamSchool;
    }

    public void setTeamSchool(String teamSchool) {
        this.teamSchool = teamSchool;
    }

    public String getTeamRegion() {
        return teamRegion;
    }

    public void setTeamRegion(String teamRegion) {
        this.teamRegion = teamRegion;
    }
}
