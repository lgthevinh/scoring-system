package org.thingai.app.scoringservice;

import org.thingai.app.scoringservice.entity.match.AllianceTeam;
import org.thingai.app.scoringservice.entity.score.Score;
import org.thingai.app.scoringservice.handler.MatchHandler;
import org.thingai.app.scoringservice.handler.TeamHandler;
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
import org.thingai.base.log.ILog;

public class ScoringService extends Service {
    private static AuthHandler authHandler;
    private static TeamHandler teamHandler;
    private static ScoreHandler scoreHandler;
    private static MatchHandler matchHandler;

    public ScoringService() {
        // Initialize dao
    }

    @Override
    protected void onServiceInit() {
        ILog.ENABLE_LOGGING = true;
        ILog.logLevel = ILog.DEBUG;
        Dao daoSqlite = new DaoSqlite(appDir + "/scoring_system.db");
        DaoFile daoFile = new DaoFile(appDir + "/files");

        System.out.println("Service initialized with app directory: " + appDir);

        daoSqlite.initDao(new Class[]{
                Event.class,
                Match.class,
                AllianceTeam.class,
                Team.class,
                Score.class,

                // System entities
                AuthData.class,
                DbMapEntity.class
        });
        // Initialize handler
        authHandler = new AuthHandler(daoSqlite);
        scoreHandler = new ScoreHandler(daoSqlite, daoFile);
        matchHandler = new MatchHandler(daoSqlite);
    }

    @Override
    protected void onServiceRun() {

    }

    public static AuthHandler authHandler() {
        return authHandler;
    }

    public static TeamHandler teamHandler() {
        return teamHandler;
    }

    public static ScoreHandler scoreHandler() {
        return scoreHandler;
    }

    public static MatchHandler matchHandler() {
        return matchHandler;
    }
}
