package org.thingai.vrc.scoringsystem.service;

import org.thingai.vrc.scoringsystem.database.Database;
import org.thingai.vrc.scoringsystem.database.IDatabase;
import org.thingai.vrc.scoringsystem.model.match.Match;
import org.thingai.vrc.scoringsystem.types.AllianceColor;
import org.thingai.vrc.scoringsystem.types.MatchType;

import java.util.List;

public class MatchGenerator {

    public MatchGenerator() {
        // Constructor logic can be added here if needed
    }

    public static void generateTemplateMatches(MatchType matchType, int numberOfMatches, int cycleTime, int startTime) {
        IDatabase<Match> matchDb = Database.getInstance(Match.class);
        int matchId = IdGenerator.generateMatchId(matchType, 1);

        for(int i = 1; i <= numberOfMatches; i++) {
            matchId = matchId + i - 1;

            int allianceRedId = IdGenerator.generateAllianceId(matchId, AllianceColor.RED);
            int allianceBlueId = allianceRedId + 1; // Assuming blue alliance is always the next ID

            Match match = new Match();
            match.setId(matchId);
            match.setMatchName(matchType.toString() + i);;
            match.setAllianceBlue(allianceBlueId);
            match.setAllianceRed(allianceRedId);
            match.setMatchType(matchType);

            matchDb.insert(match);
        }

    }

    public static void fillTeams() {

    }

}
