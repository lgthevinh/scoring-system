package org.thingai.app.scoringservice.handler;

import org.thingai.app.scoringservice.callback.RequestCallback;
import org.thingai.app.scoringservice.define.ErrorCode;
import org.thingai.app.scoringservice.define.ScoreStatus;
import org.thingai.app.scoringservice.entity.score.Score;
import org.thingai.app.scoringservice.entity.score.ScoreSeasonDemo;
import org.thingai.base.dao.Dao;
import org.thingai.base.dao.DaoFile;

public class ScoreHandler {
    private Dao dao;
    private DaoFile daoFile;

    public ScoreHandler(Dao dao, DaoFile daoFile) {
        this.dao = dao;
        this.daoFile = daoFile;
    }

    /**
     * Factory method to create a Score object. In case of change score according to the season
     * just change the implementation here.
     * @return Score object
     */
    public static Score factoryScore() {
        return new ScoreSeasonDemo();
    }

    public void updateAndSaveScore(Score score, RequestCallback<Void> callback) {
        try {
            String jsonRawScoreData = score.getRawScoreData();
            score.setStatus(ScoreStatus.SCORED);

            dao.update(Score.class, score.getAllianceId(), score);
            daoFile.writeJsonFile("/scores/" + score.getAllianceId() + ".json", jsonRawScoreData);
            callback.onSuccess(null, "Score data saved successfully.");
        } catch (Exception e) {
            callback.onFailure(ErrorCode.UPDATE_FAILED,"Failed to save score data: " + e.getMessage());
        }
    }
}
