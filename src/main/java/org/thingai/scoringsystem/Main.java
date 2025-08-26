package org.thingai.scoringsystem;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.thingai.base.Service;
import org.thingai.base.dao.Dao;
import org.thingai.scoringsystem.entity.AuthData;
import org.thingai.scoringsystem.entity.DbMap;
import org.thingai.scoringsystem.entity.match.Match;
import org.thingai.scoringsystem.entity.match.MatchAlliance;
import org.thingai.scoringsystem.entity.team.Team;

@SpringBootApplication
public class Main {

    public static void main(String[] args) {
        ScoringService.name = "Scoring System";
        ScoringService.appDirName = "scoring_system";
        ScoringService.version = "1.0.0";
        ScoringService.daoType = Dao.SQLITE; // Default DAO type, can be overridden

        ScoringService scoringService = new ScoringService();

        scoringService.init();

        scoringService.run();
        SpringApplication.run(Main.class, args);
    }

}