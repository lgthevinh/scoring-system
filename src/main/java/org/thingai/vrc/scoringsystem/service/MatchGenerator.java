package org.thingai.vrc.scoringsystem.service;

import org.thingai.vrc.scoringsystem.database.Database;
import org.thingai.vrc.scoringsystem.database.IDatabase;
import org.thingai.vrc.scoringsystem.model.match.Alliance;
import org.thingai.vrc.scoringsystem.model.match.Match;
import org.thingai.vrc.scoringsystem.model.score.Score;
import org.thingai.vrc.scoringsystem.model.score.ScoreFactory;
import org.thingai.vrc.scoringsystem.types.AllianceColor;
import org.thingai.vrc.scoringsystem.types.MatchType;

import java.sql.PreparedStatement;

public class MatchGenerator {

    public MatchGenerator() {
        // Constructor logic can be added here if needed
    }

    private void generateTemplateMatches(MatchType matchType, int numberOfMatches, int cycleTime, int startTime) {
        IDatabase<Match> matchDb = Database.getInstance(Match.class);
        IDatabase<Alliance> allianceDb = Database.getInstance(Alliance.class);
        IDatabase<? extends Score> scoreDb = Database.getInstance(ScoreFactory.getScoreClass());

        int matchId = IdGenerator.generateMatchId(matchType, 1);

        String matchStatement = "INSERT INTO match (id, match_name, alliance_blue_id, alliance_red_id, match_type) VALUES (?, ?, ?, ?, ?)";
        PreparedStatement stmt = matchDb.prepareStatement(matchStatement);

        String allianceStatement = "INSERT INTO alliance (id, match_id, alliance_color) VALUES (?, ?, ?)";
        PreparedStatement allianceStmt = allianceDb.prepareStatement(allianceStatement);

        for(int i = 1; i <= numberOfMatches; i++) {
            matchId = matchId + i - 1;

            int allianceRedId = IdGenerator.generateAllianceId(matchId, AllianceColor.RED);
            int allianceBlueId = allianceRedId + 1; // Assuming blue alliance is always the next ID

            try {
                stmt.setInt(1, matchId);
                stmt.setString(2, matchType.toString() + i);
                stmt.setInt(3, allianceBlueId);
                stmt.setInt(4, allianceRedId);
                stmt.setString(5, matchType.toString());

                stmt.addBatch();
            } catch (Exception e) {
                e.printStackTrace();
            }

            System.out.println("Generating match: " + matchType.toString() + i + " with ID: " + matchId);

            try {
                allianceStmt.setInt(1, allianceRedId);
                allianceStmt.setInt(2, matchId);
                allianceStmt.setString(3, AllianceColor.RED.toString());

                allianceStmt.addBatch();

                allianceStmt.setInt(1, allianceBlueId);
                allianceStmt.setInt(2, matchId);
                allianceStmt.setString(3, AllianceColor.BLUE.toString());

                allianceStmt.addBatch();
            } catch (Exception e) {
                e.printStackTrace();
            }

            System.out.println("Generating alliance for match: " + matchType.toString() + i + " with alliance Id: " + allianceRedId + " and " + allianceBlueId);

        }

        try {
            stmt.executeBatch();
            allianceStmt.executeBatch();

            stmt.close();
            allianceStmt.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void fillTeams() {

    }

    public void generateMatches() {
        generateTemplateMatches(MatchType.QUALIFICATION, 30, 120, 0);
        generateTemplateMatches(MatchType.ELIMINATION, 12, 180, 0);
        generateTemplateMatches(MatchType.QUARTER_FINAL, 4, 300, 0);
        generateTemplateMatches(MatchType.SEMI_FINAL, 2, 300, 0);
        generateTemplateMatches(MatchType.FINAL, 1, 300, 0);


        fillTeams();
    }

}
