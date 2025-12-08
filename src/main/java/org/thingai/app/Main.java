package org.thingai.app;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.thingai.app.scoringservice.ScoringService;
import org.thingai.app.seasondemo.ScoreSeasonDemo;
import org.thingai.base.log.ILog;

@SpringBootApplication
public class Main {

    public static void main(String[] args) {
        ScoringService scoringService = new ScoringService();
        scoringService.name = "Scoring System";
        scoringService.appDirName = "scoring_system";
        scoringService.version = "1.0.0";

        ILog.ENABLE_LOGGING = true;
        ILog.logLevel = ILog.DEBUG;

        // 1. Start Spring and get its application context
        ConfigurableApplicationContext context = SpringApplication.run(Main.class, args);

        // --- THIS IS THE BRIDGE ---
        // 2. Get the working BroadcastController that Spring created.
        SimpMessagingTemplate simpMessagingTemplate = context.getBean(SimpMessagingTemplate.class);

        scoringService.setSimpMessagingTemplate(simpMessagingTemplate);
        scoringService.init();

        scoringService.registerScoreClass(ScoreSeasonDemo.class); // Register the scoring class for the season specific logic

    }

}