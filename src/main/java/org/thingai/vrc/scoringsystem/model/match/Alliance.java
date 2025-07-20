package org.thingai.vrc.scoringsystem.model.match;

import org.thingai.vrc.scoringsystem.model.BaseModel;
import org.thingai.vrc.scoringsystem.model.score.ScoreFactory;
import org.thingai.vrc.scoringsystem.types.AllianceColor;
import org.thingai.vrc.scoringsystem.annotations.DaoField;
import org.thingai.vrc.scoringsystem.annotations.DaoName;
import org.thingai.vrc.scoringsystem.model.score.Score;
import org.thingai.vrc.scoringsystem.model.team.Team;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@DaoName(name = "alliance")
public class Alliance extends BaseModel<Alliance> {

    @DaoField(name = "match_id")
    private int matchId;

    @DaoField(name = "alliance_color")
    private AllianceColor color;

    @DaoField(name = "score_id")
    private int scoreId;

    private List<Team> teams;

    public Alliance() {
        // Default constructor for serialization/deserialization
    }

    public int getMatchId() {
        return matchId;
    }

    public void setMatchId(int matchId) {
        this.matchId = matchId;
    }

    public AllianceColor getColor() {
        return color;
    }

    public void setColor(AllianceColor color) {
        this.color = color;
    }


    public int getScore() {
        return scoreId;
    }

    public void setScore(int score) {
        this.scoreId = score;
    }

    @Override
    public Alliance fromMap(Map<String, Object> map) {
        Alliance alliance = new Alliance();
        alliance.setId((int) map.get("id"));
        alliance.setMatchId((int) map.get("match_id"));
        alliance.setColor(AllianceColor.valueOf((String) map.get("alliance_color")));

        return alliance;
    }

    public List<Team> getTeams() {
        return teams;
    }

    public void setTeams(List<Team> teams) {
        this.teams = teams;
    }
}
