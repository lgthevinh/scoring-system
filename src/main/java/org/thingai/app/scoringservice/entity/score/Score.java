package org.thingai.app.scoringservice.entity.score;

import org.thingai.base.dao.annotations.DaoColumn;

public abstract class Score {

    @DaoColumn(name = "id", primaryKey = true, autoIncrement = true)
    private String id;

    @DaoColumn(name = "allianceId")
    private String allianceId;

    @DaoColumn(name = "totalScore")
    protected int totalScore;

    @DaoColumn(name = "penaltiesScore")
    protected int penaltiesScore;

    public abstract void calculateTotalScore();
    public abstract void calculatePenalties();

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAllianceId() {
        return allianceId;
    }

    public void setAllianceId(String allianceId) {
        this.allianceId = allianceId;
    }
}