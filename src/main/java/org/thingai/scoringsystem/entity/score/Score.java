package org.thingai.scoringsystem.entity.score;

import org.thingai.base.dao.annotations.DaoColumn;

public abstract class Score {

    @DaoColumn(name = "match_id")
    private String matchId;

    @DaoColumn(name = "alliance_color")
    private int allianceColor; // 0 for red, 1 for blue

    @DaoColumn(name = "total_score")
    protected int totalScore;

    @DaoColumn(name = "penalties_score")
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