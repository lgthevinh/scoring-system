package com.thingai.model.match;

import com.thingai.core.AllianceColor;
import com.thingai.core.DaoField;
import com.thingai.core.DaoName;
import com.thingai.model.score.Score;
import com.thingai.model.team.Team;

import java.util.ArrayList;
import java.util.List;

@DaoName(name = "alliance")
public class Alliance {
    @DaoField(name = "alliance_id")
    private String allianceId;

    @DaoField(name = "match_id")
    private long matchId;

    @DaoField(name = "alliance_color")
    private AllianceColor color;

    @DaoField(name = "teams")
    private List<Team> teams = new ArrayList<>();

    @DaoField(name = "score_id")
    private Score score;

    public Alliance(String allianceId, long matchId, AllianceColor color, List<Team> teams, Score score) {
        this.allianceId = allianceId;
        this.matchId = matchId;
        this.color = color;
        this.teams = teams;
        this.score = score;
    }

    public Alliance() {
        // Default constructor for serialization/deserialization
    }


    public String getAllianceId() {
        return allianceId;
    }

    public void setAllianceId(String allianceId) {
        this.allianceId = allianceId;
    }

    public long getMatchId() {
        return matchId;
    }

    public void setMatchId(long matchId) {
        this.matchId = matchId;
    }

    public AllianceColor getColor() {
        return color;
    }

    public void setColor(AllianceColor color) {
        this.color = color;
    }

    public List<Team> getTeams() {
        return teams;
    }

    public void setTeams(List<Team> teams) {
        this.teams = teams;
    }

    public Score getScore() {
        return score;
    }

    public void setScore(Score score) {
        this.score = score;
    }

    public void addTeam(Team team) {
        if (team != null && !teams.contains(team) && teams.size() < 3) {
            teams.add(team);
        }
    }
}
