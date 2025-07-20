package org.thingai.vrc.scoringsystem.model.score;

import org.thingai.vrc.scoringsystem.annotations.DaoField;
import org.thingai.vrc.scoringsystem.annotations.DaoName;

import java.util.Map;

@DaoName(name = "season_demo_score")
public class DemoScore extends Score {

    private int gateOpened;
    private int ballCollected;
    private boolean parked;

    private int majorFault;
    private int minorFault;

    public DemoScore() {
        // Default constructor for serialization/deserialization
    }

    @Override
    public int getScore() {
        return 0;
    }

    @Override
    public int getPenalties() {
        return 0;
    }

    @Override
    public int getTotalScore() {
        return 0;
    }

    @Override
    public DemoScore fromMap(Map<String, Object> map) {
        DemoScore score = new DemoScore();
        score.setAllianceId((int) map.get("alliance_id"));
        return score;
    }
}
