package org.thingai.vrc.scoringsystem.service;

import org.thingai.vrc.scoringsystem.model.match.Match;
import org.thingai.vrc.scoringsystem.model.team.Team;
import org.thingai.vrc.scoringsystem.types.MatchType;

import java.util.List;

public class MatchGenerator {
    // Default when start, this will be changed after init in json config
    public static MatchType MATCH_TYPE = MatchType.QUALIFICATION;

    public MatchGenerator() {
        // Constructor logic can be added here if needed
    }

    public static void generateMatches(String matchType, List<Team> teamList, int numberOfMatchesPerTeam, int timeCycle) {

    }

}
