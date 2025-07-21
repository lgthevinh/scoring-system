package org.thingai.vrc.scoringsystem.model.score;

import org.thingai.base.database.annotations.DaoField;
import org.thingai.base.database.annotations.DaoName;

import java.util.Map;

@DaoName(name = "season_demo_score")
public class DemoScore extends Score {

    @DaoField(name = "gate_opened")
    private int gateOpened;

    @DaoField(name = "ball_collected")
    private int ballCollected;

    @DaoField(name = "parked")
    private boolean parked;

    @DaoField(name = "major_fault")
    private int majorFault;

    @DaoField(name = "minor_fault")
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
