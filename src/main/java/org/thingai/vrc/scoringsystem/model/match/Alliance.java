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
    private String matchId;

    @DaoField(name = "alliance_color")
    private AllianceColor color;

    @DaoField(name = "score_id")
    private Score score;

    private List<Team> teams;

    public Alliance() {
        // Default constructor for serialization/deserialization
    }

    public String getMatchId() {
        return matchId;
    }

    public void setMatchId(String matchId) {
        this.matchId = matchId;
    }

    public AllianceColor getColor() {
        return color;
    }

    public void setColor(AllianceColor color) {
        this.color = color;
    }


    public Score getScore() {
        return score;
    }

    public void setScore(Score score) {
        this.score = score;
    }

    @Override
    public Alliance fromMap(Map<String, Object> map) {
        Alliance alliance = new Alliance();
        alliance.setId((String) map.get("id"));
        alliance.setMatchId((String) map.get("match_id"));
        alliance.setColor(AllianceColor.valueOf((String) map.get("alliance_color")));

        List<Team> teams = new ArrayList<>();
        List<Map<String, Object>> teamMaps = (List<Map<String, Object>>) map.get("teams");
        if (teamMaps != null) {
            for (Map<String, Object> teamMap : teamMaps) {
                Team team = new Team();
                team.fromMap(teamMap);
                teams.add(team);
            }
        }

        Score score = ScoreFactory.createScore();
        score.fromMap((Map<String, Object>) map.get("score"));
        alliance.setScore(score);

        return alliance;
    }

    public List<Team> getTeams() {
        return teams;
    }

    public void setTeams(List<Team> teams) {
        this.teams = teams;
    }
}
