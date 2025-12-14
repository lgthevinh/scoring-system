package org.thingai.app.scoringservice.entity.ranking;

public class RankingEntry {
    private String teamId;
    private int rank;
    private int matchesPlayed;
    private int wins;
    private int totalScore;
    private int totalPenalties;
    private int highestScore;
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
