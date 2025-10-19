package org.thingai.app.scoringservice;

import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.thingai.app.scoringservice.entity.match.AllianceTeam;
import org.thingai.app.scoringservice.entity.score.Score;
import org.thingai.app.scoringservice.handler.systembase.*;
import org.thingai.base.Service;
import org.thingai.base.cache.LRUCache;
import org.thingai.base.dao.Dao;
import org.thingai.base.dao.DaoFile;
import org.thingai.base.dao.DaoSqlite;
import org.thingai.app.scoringservice.entity.event.Event;
import org.thingai.app.scoringservice.entity.team.Team;
import org.thingai.app.scoringservice.entity.config.AuthData;
import org.thingai.app.scoringservice.entity.config.DbMapEntity;
import org.thingai.app.scoringservice.entity.match.Match;
import org.thingai.base.log.ILog;

import java.util.HashMap;

public class ScoringService extends Service {
    private static final String SERVICE_NAME = "ScoringService";

    private final LRUCache<String, Match> matchCache = new LRUCache<>(50, new HashMap<>());
    private final LRUCache<String, AllianceTeam[]> allianceTeamCache = new LRUCache<>(100, new HashMap<>());
    private final LRUCache<String, Team> teamCache = new LRUCache<>(30, new HashMap<>());

    private static AuthHandler authHandler;
    private static TeamHandler teamHandler;
    private static ScoreHandler scoreHandler;
    private static MatchHandler matchHandler;
    private static BroadcastHandler broadcastHandler;

    public ScoringService() {

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
        teamHandler = new TeamHandler(daoSqlite, teamCache);
        matchHandler = new MatchHandler(daoSqlite, matchCache, allianceTeamCache, teamCache);
        scoreHandler = new ScoreHandler(daoSqlite, daoFile);

        scoreHandler.setBroadcastHandler(broadcastHandler);
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

    public static BroadcastHandler broadcastHandler() {
        return broadcastHandler;
    }

    public void setSimpMessagingTemplate(SimpMessagingTemplate simpMessagingTemplate) {
        broadcastHandler = new BroadcastHandler(simpMessagingTemplate);
    }
}
