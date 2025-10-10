package org.thingai.app.scoringservice.handler;

import org.thingai.base.dao.Dao;
import org.thingai.base.dao.DaoFile;

public class ScoreHandler {
    private Dao dao;
    private DaoFile daoFile;

    public ScoreHandler(Dao dao, DaoFile daoFile) {
        this.dao = dao;
        this.daoFile = daoFile;
    }
}
