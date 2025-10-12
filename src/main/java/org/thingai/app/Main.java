package org.thingai.app;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.thingai.app.scoringservice.ScoringService;

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