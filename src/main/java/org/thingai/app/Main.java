package org.thingai.app;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.thingai.app.controller.BroadcastController;
import org.thingai.app.scoringservice.ScoringService;

@SpringBootApplication
public class Main {

    public static void main(String[] args) {
        ScoringService.name = "Scoring System";
        ScoringService.appDirName = "scoring_system";
        ScoringService.version = "1.0.0";

        // 1. Start Spring and get its application context
        ConfigurableApplicationContext context = SpringApplication.run(Main.class, args);

        // --- THIS IS THE BRIDGE ---
        // 2. Get the working BroadcastController that Spring created.
        BroadcastController springManagedBroadcaster = context.getBean(BroadcastController.class);

        ScoringService scoringService = new ScoringService();

        scoringService.setBroadcastController(springManagedBroadcaster);
        scoringService.init();
        scoringService.run();

    }

}