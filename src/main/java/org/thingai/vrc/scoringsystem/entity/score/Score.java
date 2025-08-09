package org.thingai.vrc.scoringsystem.entity.score;

public abstract class Score {
    private String matchId;
    private int allianceId;

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

    public int getAllianceId() {
        return allianceId;
    }

    public void setAllianceId(int allianceId) {
        this.allianceId = allianceId;
    }
}