package org.thingai.app.scoringservice.entity.score;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import org.thingai.base.log.ILog;

public class ScoreSeasonDemo extends Score {
    private int robotParked = 0; // 0, 1, 2, 3 for robots parked
    private int robotHanged = 0;
    private int ballEntered = 0; // number of ball entered
    private int minorFault = 0;
    private int majorFault = 0;

    public ScoreSeasonDemo() {
        super();
    }

    @Override
    public void calculateTotalScore() {
        totalScore = robotParked * 10 + robotHanged * 20 + ballEntered * 5;
    }

    @Override
    public void calculatePenalties() {
        penaltiesScore = minorFault * 5 + majorFault * 10;
    }

    @Override
    public void fromJson(String json) {
        Gson gson = new Gson();

        JsonObject jsonObject = gson.fromJson(json, JsonObject.class);

        ILog.d("ScoreSeasonDemo", "fromJson: " + jsonObject.toString());

        if (jsonObject.get("robotParked") != null) {
            this.robotParked = jsonObject.get("robotParked").getAsInt();
        }

        if (jsonObject.get("robotHanged") != null) {
            this.robotHanged = jsonObject.get("robotHanged").getAsInt();
        }

        if (jsonObject.get("ballEntered") != null) {
            this.ballEntered = jsonObject.get("ballEntered").getAsInt();
        }

        if (jsonObject.get("minorFault") != null) {
            this.minorFault = jsonObject.get("minorFault").getAsInt();
        }

        if (jsonObject.get("majorFault") != null) {
            this.majorFault = jsonObject.get("majorFault").getAsInt();
        }
    }

    @Override
    public String getRawScoreData() {
        Gson gson = new Gson();
        return gson.toJson(this);
    }
}
