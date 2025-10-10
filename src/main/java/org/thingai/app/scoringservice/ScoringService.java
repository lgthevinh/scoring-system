package org.thingai.app.scoringservice;

import org.thingai.app.scoringservice.entity.score.Score;
import org.thingai.base.Service;
import org.thingai.base.dao.Dao;
import org.thingai.base.dao.DaoFile;
import org.thingai.base.dao.DaoSqlite;
import org.thingai.app.scoringservice.entity.event.Event;
import org.thingai.app.scoringservice.entity.team.Team;
import org.thingai.app.scoringservice.entity.config.AuthData;
import org.thingai.app.scoringservice.entity.config.DbMapEntity;
import org.thingai.app.scoringservice.entity.match.Match;
import org.thingai.app.scoringservice.handler.AuthHandler;
import org.thingai.app.scoringservice.handler.ScoreHandler;

public class ScoringService extends Service {
    private final Dao daoSqlite;
    private final DaoFile daoFile;
    private static AuthHandler authHandler;
    private static ScoreHandler scoreHandler;

    public ScoringService() {
        // Initialize dao
        this.daoSqlite = new DaoSqlite(appDir + "/scoring_system.db");
        this.daoFile = new DaoFile(appDir + "/files");

        System.out.println("Service initialized with app directory: " + appDir);
    }

    @Override
    protected void onServiceInit() {
        daoSqlite.initDao(new Class[]{
                Event.class,
                Match.class,
                Team.class,
                Score.class,

                // System entities
                AuthData.class,
                DbMapEntity.class
        });
        // Initialize handler
        authHandler = new AuthHandler(daoSqlite);
        scoreHandler = new ScoreHandler(daoSqlite, daoFile);
    }

    @Override
    protected void onServiceRun() {

    }

    public static AuthHandler authHandler() {
        return authHandler;
    }

    public static ScoreHandler scoreHandler() {
        return scoreHandler;
    }
}
