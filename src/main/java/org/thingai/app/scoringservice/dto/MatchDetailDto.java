package org.thingai.app.scoringservice.dto;

import org.thingai.app.scoringservice.entity.match.Match;
import org.thingai.app.scoringservice.entity.team.Team;

public class MatchDetailDto {
    private Match match;
    private Team[] redTeams;
    private Team[] blueTeams;

    public MatchDetailDto(Match match, Team[] redTeams, Team[] blueTeams) {
        this.match = match;
        this.redTeams = redTeams;
        this.blueTeams = blueTeams;
    }
}
