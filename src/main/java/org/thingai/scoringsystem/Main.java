package org.thingai.scoringsystem;

import org.thingai.base.dao.Dao;
import org.thingai.scoringsystem.entity.match.Match;
import org.thingai.scoringsystem.entity.match.MatchAlliance;
import org.thingai.scoringsystem.entity.score.ScoreSeasonDemo;
import org.thingai.scoringsystem.entity.team.Team;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Main {
    private static ScoringApplication scoringApplication = new ScoringApplication();

    public static void main(String[] args) {
        scoringApplication.name = "Scoring System";
        scoringApplication.appDirName = "scoring_system";
        scoringApplication.version = "1.0.0";
        scoringApplication.daoType = Dao.SQLITE; // Default DAO type, can be overridden

        scoringApplication.init();
        scoringApplication.start();

        scoringApplication.dao.facDao(new Class[] {
                Match.class,
                MatchAlliance.class,
                ScoreSeasonDemo.class,
                Team.class,
        });
    }
}