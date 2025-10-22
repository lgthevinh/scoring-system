package org.thingai.app.scoringservice.dto;

public class MatchTimeStatusDto {
    private String matchId;
    private int remainingSeconds;
    // private String phase;

    public MatchTimeStatusDto(String matchId, int remainingSeconds) {
        this.matchId = matchId;
        this.remainingSeconds = remainingSeconds;
    }

    public String getMatchId() {
        return matchId;
    }

    public void setMatchId(String matchId) {
        this.matchId = matchId;
    }

    public int getRemainingSeconds() {
        return remainingSeconds;
    }

    public void setRemainingSeconds(int remainingSeconds) {
        this.remainingSeconds = remainingSeconds;
    }
}
