package org.thingai.app.scoringservice.entity.score;

import java.util.HashMap;

public interface IScoreConfig {
    void calculateTotalScore(); // return as int
    void calculatePenalties(); // return as int
    void fromJson(String json);
    String getRawScoreData();
    HashMap<String, ScoreDefine> getScoreDefinitions();
}
