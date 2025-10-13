package org.thingai.app.scoringservice.dto;

import org.thingai.app.scoringservice.entity.match.Match;
import org.thingai.app.scoringservice.entity.team.Team;

public class MatchDetailDto {
    private Match match;
    private Team[] redTeams;
    private Team[] blueTeams;

    public MatchDetailDto() {
    }

    public MatchDetailDto(Match match, Team[] redTeams, Team[] blueTeams) {
        this.match = match;
        this.redTeams = redTeams;
        this.blueTeams = blueTeams;
    }

    public Match getMatch() {
        return match;
    }

    public void setMatch(Match match) {
        this.match = match;
    }

    public Team[] getRedTeams() {
        return redTeams;
    }

    public void setRedTeams(Team[] redTeams) {
        this.redTeams = redTeams;
    }

    public Team[] getBlueTeams() {
        return blueTeams;
    }

    public void setBlueTeams(Team[] blueTeams) {
        this.blueTeams = blueTeams;
    }
}
