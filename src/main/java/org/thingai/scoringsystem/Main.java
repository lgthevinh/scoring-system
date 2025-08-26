package org.thingai.scoringsystem;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.thingai.base.dao.Dao;

@SpringBootApplication
public class Main {

    public static void main(String[] args) {
        ScoringService.name = "Scoring System";
        ScoringService.appDirName = "scoring_system";
        ScoringService.version = "1.0.0";

        ScoringService scoringService = new ScoringService();

        scoringService.init();

        scoringService.run();
        SpringApplication.run(Main.class, args);
    }

}