package org.thingai.scoringsystem.entity.score;

public abstract class Score {
    private String matchId;
    private int allianceColor; // 0 for red, 1 for blue

    protected int totalScore;
    protected int penaltiesScore;

    public abstract void calculateTotalScore();
    public abstract void calculatePenalties();

    public String getMatchId() {
        return matchId;
    }

    public void setMatchId(String matchId) {
        this.matchId = matchId;
    }

    public int setAllianceColor() {
        return allianceColor;
    }

    public void setAllianceColor(int allianceColor) {
        this.allianceColor = allianceColor;
    }
}