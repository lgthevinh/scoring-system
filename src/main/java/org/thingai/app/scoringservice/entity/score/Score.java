package org.thingai.app.scoringservice.entity.score;

import org.thingai.app.scoringservice.define.ScoreStatus;
import org.thingai.base.dao.annotations.DaoColumn;
import org.thingai.base.dao.annotations.DaoTable;

@DaoTable(name = "score")
public class Score {
    // As alliance ID
    @DaoColumn(name = "id", primaryKey = true)
    private String id;

    @DaoColumn(name = "status")
    private int status;

    @DaoColumn(name = "totalScore")
    protected int totalScore;

    @DaoColumn(name = "penaltiesScore")
    protected int penaltiesScore;

    protected String rawScoreData;

    public Score() {
        this.status = ScoreStatus.NOT_SCORED;
        this.totalScore = 0;
        this.penaltiesScore = 0;
    }

    public void calculateTotalScore() {

    }
    public void calculatePenalties() {

    }
    public void fromJson(String json) {

    }
    public String getRawScoreData() {
        return rawScoreData;
    }

    public String getAllianceId() {
        return id;
    }

    public void setAllianceId(String allianceId) {
        this.id = allianceId;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }
}