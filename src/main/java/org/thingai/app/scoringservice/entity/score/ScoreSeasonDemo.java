package org.thingai.app.scoringservice.entity.score;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

public class ScoreSeasonDemo extends Score {
    private int robotParked; // 0, 1, 2, 3 for robots parked
    private int robotHanged;
    private int ballEntered; // number of ball entered
    private int minorFault;
    private int majorFault;

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

        this.robotParked = jsonObject.get("robotParked").getAsInt();
        this.robotHanged = jsonObject.get("robotHanged").getAsInt();
        this.ballEntered = jsonObject.get("ballEntered").getAsInt();
        this.minorFault = jsonObject.get("minorFault").getAsInt();
        this.majorFault = jsonObject.get("majorFault").getAsInt();
    }

    @Override
    public String getRawScoreData() {
        Gson gson = new Gson();
        return gson.toJson(this);
    }
}
