package com.thingai.model.score;

import com.thingai.core.DaoName;

@DaoName(name = "season_demo_score")
public class SeasonDemoScore extends AbstractScore {

    private int gateOpened;
    private int ballCollected;
    private boolean parked;

    private int majorFault;
    private int minorFault;

    @Override
    protected int computeScore() {
        return gateOpened * 10 + ballCollected * 5 + (parked ? 20 : 0);
    }

    @Override
    protected int computePenalties() {
        return majorFault * 10 + minorFault * 5;
    }

    @Override
    protected int computeTotalScore() {
        return computeScore() - computePenalties();
    }
}
