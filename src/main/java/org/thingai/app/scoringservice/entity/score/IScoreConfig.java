package org.thingai.app.scoringservice.entity.score;

import java.util.HashMap;

public interface IScoreConfig {
    void calculateTotalScore();
    void calculatePenalties();
    void fromJson(String json);
    String getRawScoreData();
    HashMap<String, ScoreDefine> getScoreDefinitions();
}
