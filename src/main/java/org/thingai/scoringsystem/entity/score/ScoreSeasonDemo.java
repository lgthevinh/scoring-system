package org.thingai.scoringsystem.entity.score;

import org.thingai.base.dao.annotations.DaoTable;

@DaoTable(name = "game_specific_score")
public class ScoreSeasonDemo extends Score {
    private int robotParked; // 0, 1, 2, 3 for robots parked
    private int robotHanged;

    private int ballEntered; // number of ball entered

    private int minorFault;
    private int majorFault;

    @Override
    public void calculateTotalScore() {
        totalScore = robotParked * 10 + robotHanged * 20 + ballEntered * 5;
    }

    @Override
    public void calculatePenalties() {
        penaltiesScore = minorFault * 5 + majorFault * 10;
    }
}
