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

import java.nio.file.Path;

public class ScoringService extends Service {
    private final Dao daoSqlite = new DaoSqlite();
    private final Dao daoFile = new DaoFile(Path.of(appDir, "datafile"));

    @Override
    public void onServiceInit() {
        this.daoSqlite.initDao(new Class[]{
                Match.class,
                MatchAlliance.class,
                // ScoreSeasonDemo.class,
                Team.class,
                AuthData.class,
                DbMap.class
        });

        this.daoFile.initDao(new Class[]{

        });

        System.out.println("Service initialized with app directory: " + appDir);
    }

    @Override
    public void run() {
    }
}
