package org.thingai.scoringsystem;

import org.thingai.base.Service;
import org.thingai.base.dao.Dao;
import org.thingai.base.dao.DaoFile;
import org.thingai.base.dao.DaoSqlite;
import org.thingai.scoringsystem.entity.AuthData;
import org.thingai.scoringsystem.entity.DbMap;
import org.thingai.scoringsystem.entity.match.Match;
import org.thingai.scoringsystem.entity.match.MatchAlliance;
import org.thingai.scoringsystem.entity.team.Team;

public class ScoringService extends Service {

    @Override
    protected void onServiceInit() {
        Dao daoSqlite = new DaoSqlite(appDir + "/scoring_system.db");
        Dao daoFile = new DaoFile(appDir + "/data");

        daoSqlite.initDao(new Class[]{
            Match.class,
            MatchAlliance.class,
            // ScoreSeasonDemo.class,
            Team.class,
            AuthData.class,
            DbMap.class
        });

        daoFile.initDao(new Class[]{
            // Match.class,
        });

        System.out.println("Service initialized with app directory: " + appDir);
    }

    @Override
    protected void onServiceRun() {
    }
}
