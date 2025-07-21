package org.thingai.vrc.scoringsystem.model.team;

import org.thingai.base.database.annotations.DaoField;
import org.thingai.base.database.annotations.DaoName;
import org.thingai.base.model.BaseModel;

import java.util.Map;

@DaoName(name = "team")
public class Team extends BaseModel<Team> {

    @DaoField(name = "team_name")
    private String teamName;

    @DaoField(name = "team_school")
    private String teamSchool;

    @DaoField(name = "team_region")
    private String teamRegion;

    public Team() {
        // Default constructor
    }

    @Override
    public Team fromMap(Map<String, Object> map) {
        Team team = new Team();
        team.setId((int) map.get("team_id"));
        team.setTeamName((String) map.get("team_name"));
        team.setTeamSchool((String) map.get("team_school"));
        team.setTeamRegion((String) map.get("team_region"));
        return team;
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
