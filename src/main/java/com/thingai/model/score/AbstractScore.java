package com.thingai.model.score;

import com.thingai.model.BaseModel;

public abstract class AbstractScore extends BaseModel implements Score {
    protected long scoreId;
    protected long allianceId;

    public int getScore() {
        return computeScore();
    }

    public int getPenalties() {
        return computePenalties();
    }

    public int getTotalScore() {
        return computeTotalScore();
    }

    protected abstract int computeScore();
    protected abstract int computePenalties();
    protected abstract int computeTotalScore();

}
