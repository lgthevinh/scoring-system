package org.thingai.vrc.scoringsystem.model.team;

import org.thingai.vrc.scoringsystem.annotations.DaoField;
import org.thingai.vrc.scoringsystem.annotations.DaoName;
import org.thingai.vrc.scoringsystem.model.BaseModel;

import java.util.Map;

@DaoName(name = "team_stats")
public class TeamStats extends BaseModel<TeamStats> {

    @DaoField(name = "team_id")
    private String teamId;

    @DaoField(name = "team_name")
    private int totalMatchesPlayed;

    @DaoField(name = "total_wins")
    private int totalWins;

    @DaoField(name = "total_score")
    private int totalScore;

    public TeamStats() {
        // Default constructor
    }

    public TeamStats(String teamId, int totalMatchesPlayed, int totalWins, int totalScore) {
        this.teamId = teamId;
        this.totalMatchesPlayed = totalMatchesPlayed;
        this.totalWins = totalWins;
        this.totalScore = totalScore;
    }

    @Override
    public TeamStats fromMap(Map<String, Object> map) {
        TeamStats teamStats = new TeamStats();
        teamStats.setId((int) map.get("team_id"));
        teamStats.setTotalMatchesPlayed((Integer) map.get("total_matches_played"));
        teamStats.setTotalWins((Integer) map.get("total_wins"));
        teamStats.setTotalScore((Integer) map.get("total_score"));
        return teamStats;
    }

    public String getTeamId() {
        return teamId;
    }

    public void setTeamId(String teamId) {
        this.teamId = teamId;
    }

    public int getTotalMatchesPlayed() {
        return totalMatchesPlayed;
    }

    public void setTotalMatchesPlayed(int totalMatchesPlayed) {
        this.totalMatchesPlayed = totalMatchesPlayed;
    }

    public int getTotalWins() {
        return totalWins;
    }

    public void setTotalWins(int totalWins) {
        this.totalWins = totalWins;
    }

    public int getTotalScore() {
        return totalScore;
    }

    public void setTotalScore(int totalScore) {
        this.totalScore = totalScore;
    }
}
