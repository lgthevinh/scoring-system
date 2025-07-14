package com.thingai.model.team;

import com.thingai.core.DaoField;
import com.thingai.core.DaoName;
import com.thingai.model.BaseModel;

@DaoName(name = "team")
public class Team extends BaseModel {

    @DaoField(name = "team_id")
    private String teamId;

    @DaoField(name = "team_name")
    private String teamName;

    @DaoField(name = "team_school")
    private String teamSchool;

    @DaoField(name = "team_region")
    private String teamRegion;

    public Team() {
        // Default constructor
    }

    public Team(String teamId, String teamName, String teamSchool, String teamRegion) {
        this.teamId = teamId;
        this.teamName = teamName;
        this.teamSchool = teamSchool;
        this.teamRegion = teamRegion;
    }

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
