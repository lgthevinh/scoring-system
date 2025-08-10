package org.thingai.scoringsystem.entity.score;

import org.thingai.base.dao.annotations.DaoColumn;
import org.thingai.base.dao.annotations.DaoTable;

@DaoTable(name = "game_specific_score")
public class ScoreSeasonDemo extends Score {

    @DaoColumn(name = "robot_parked")
    private int robotParked; // 0, 1, 2, 3 for robots parked

    @DaoColumn(name = "robot_hanged")
    private int robotHanged;

    @DaoColumn(name = "ball_entered")
    private int ballEntered; // number of ball entered

    @DaoColumn(name = "minor_fault")
    private int minorFault;

    @DaoColumn(name = "major_fault")
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
