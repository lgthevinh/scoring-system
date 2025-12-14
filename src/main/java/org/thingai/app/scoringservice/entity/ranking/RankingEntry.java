package org.thingai.app.scoringservice.entity.ranking;

import org.thingai.base.dao.annotations.DaoColumn;
import org.thingai.base.dao.annotations.DaoTable;

@DaoTable(name = "ranking_entry")
public class RankingEntry {
    @DaoColumn(name = "id", primaryKey = true, autoIncrement = true)
    private int id;

    @DaoColumn(name = "team_id", nullable = false)
    private String teamId;

    @DaoColumn(name = "rank", nullable = false)
    private int rank;

    @DaoColumn(name = "matches_played", nullable = false)
    private int matchesPlayed;

    @DaoColumn(name = "wins", nullable = false)
    private int wins;

    @DaoColumn(name = "total_score", nullable = false)
    private int totalScore;

    @DaoColumn(name = "total_penalties", nullable = false)
    private int totalPenalties;

    @DaoColumn(name = "highest_score", nullable = false)
    private int highestScore;

    @DaoColumn(name = "ranking_points", nullable = false)
    private int rankingPoints;

    public RankingEntry() {
    }

    public String getTeamId() {
        return teamId;
    }

    public void setTeamId(String teamId) {
        this.teamId = teamId;
    }

    public int getRank() {
        return rank;
    }

    public void setRank(int rank) {
        this.rank = rank;
    }

    public int getMatchesPlayed() {
        return matchesPlayed;
    }

    public void setMatchesPlayed(int matchesPlayed) {
        this.matchesPlayed = matchesPlayed;
    }

    public int getWins() {
        return wins;
    }

    public void setWins(int wins) {
        this.wins = wins;
    }

    public int getTotalScore() {
        return totalScore;
    }

    public void setTotalScore(int totalScore) {
        this.totalScore = totalScore;
    }

    public int getTotalPenalties() {
        return totalPenalties;
    }

    public void setTotalPenalties(int totalPenalties) {
        this.totalPenalties = totalPenalties;
    }

    public int getHighestScore() {
        return highestScore;
    }

    public void setHighestScore(int highestScore) {
        this.highestScore = highestScore;
    }

    public int getRankingPoints() {
        return rankingPoints;
    }

    public void setRankingPoints(int rankingPoints) {
        this.rankingPoints = rankingPoints;
    }
}
