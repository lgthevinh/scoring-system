package org.thingai.app.fanroc;

import com.google.gson.Gson;
import org.thingai.app.scoringservice.entity.score.IScoreConfig;
import org.thingai.app.scoringservice.entity.score.Score;
import org.thingai.app.scoringservice.entity.score.ScoreDefine;

import java.util.HashMap;

public class FanrocScore extends Score implements IScoreConfig {
    private int ballsCollected;
    private int ballsScored;
    private int foulsCommitted;

    @Override
    public void calculateTotalScore() {
        totalScore = (ballsScored * 10) - (foulsCommitted * 5);
    }

    @Override
    public void calculatePenalties() {
        penaltiesScore = foulsCommitted * 5;
    }

    @Override
    public void fromJson(String json) {
        Gson gson = new Gson();
        FanrocScore temp = gson.fromJson(json, FanrocScore.class);
        this.ballsCollected = temp.ballsCollected;
        this.ballsScored = temp.ballsScored;
        this.foulsCommitted = temp.foulsCommitted;
    }

    @Override
    public String getRawScoreData() {
        Gson gson = new Gson();
        return gson.toJson(this);
    }

    @Override
    public HashMap<String, ScoreDefine> getScoreDefinitions() {
        HashMap<String, ScoreDefine> definitions = new HashMap<>();

        definitions.put("ballsCollected", new ScoreDefine("Balls Collected", null, null));
        definitions.put("ballsScored", new ScoreDefine("Balls Scored", null, null));
        definitions.put("foulsCommitted", new ScoreDefine("Fouls Committed", null, null));
        
        return definitions;
    }
}
