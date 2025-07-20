package org.thingai.vrc.scoringsystem.model.score;

import org.thingai.vrc.scoringsystem.annotations.DaoField;
import org.thingai.vrc.scoringsystem.model.BaseModel;

import java.util.Map;

public abstract class Score extends BaseModel<Score> {

    @DaoField(name = "score_id")
    protected String allianceId;

    public abstract int getScore();
    public abstract int getPenalties();
    public abstract int getTotalScore();

    public String getAllianceId() {
        return allianceId;
    }

    public void setAllianceId(String allianceId) {
        this.allianceId = allianceId;
    }
}