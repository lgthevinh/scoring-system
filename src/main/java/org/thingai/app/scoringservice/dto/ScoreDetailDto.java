package org.thingai.app.scoringservice.dto;

import com.google.gson.JsonObject;
import org.thingai.app.scoringservice.entity.score.Score;
import org.thingai.app.scoringservice.handler.systembase.ScoreHandler;

public class ScoreDetailDto {
    private Score baseScore;
    private JsonObject detailScore;

    public ScoreDetailDto() {
        baseScore = ScoreHandler.factoryScore();
    }

    public void setScore(String allianceId) {

    }

    public Score getScore() {
        return baseScore;
    }
}
