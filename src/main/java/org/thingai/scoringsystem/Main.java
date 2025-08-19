package org.thingai.scoringsystem;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.thingai.base.Application;
import org.thingai.base.dao.Dao;
import org.thingai.scoringsystem.entity.AuthData;
import org.thingai.scoringsystem.entity.DbMap;
import org.thingai.scoringsystem.entity.match.Match;
import org.thingai.scoringsystem.entity.match.MatchAlliance;
import org.thingai.scoringsystem.entity.score.ScoreSeasonDemo;
import org.thingai.scoringsystem.entity.team.Team;

@SpringBootApplication
public class Main {
    private static final Application scoringApplication = new Application();

    public static void main(String[] args) {
        Application.name = "Scoring System";
        Application.appDirName = "scoring_system";
        Application.version = "1.0.0";
        Application.daoType = Dao.SQLITE; // Default DAO type, can be overridden

        scoringApplication.init();
        scoringApplication.start();

        scoringApplication.facDao(new Class[] {
                Match.class,
                MatchAlliance.class,
//                ScoreSeasonDemo.class,
                Team.class,
                AuthData.class,
                DbMap.class
        });

        SpringApplication.run(Main.class, args);
    }
}