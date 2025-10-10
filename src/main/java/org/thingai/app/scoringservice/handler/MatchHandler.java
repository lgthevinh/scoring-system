package org.thingai.app.scoringservice.handler;

import org.thingai.app.scoringservice.callback.RequestCallback;
import org.thingai.app.scoringservice.define.ErrorCode;
import org.thingai.app.scoringservice.define.MatchType;
import org.thingai.app.scoringservice.entity.match.AllianceTeam;
import org.thingai.app.scoringservice.entity.score.Score;
import org.thingai.base.dao.Dao;
import org.thingai.app.scoringservice.entity.match.Match;

public class MatchHandler {
    private final Dao dao;

    public MatchHandler(Dao dao) {
        this.dao = dao;
    }

    public void createMatch(int matchType, int matchNumber, String matchStartTime, String[] redTeamIds, String[] blueTeamIds, RequestCallback<Match> callback) {
        try {
            // Create match entity
            Match match = new Match();
            match.setMatchType(matchType);
            match.setMatchNumber(matchNumber);
            match.setMatchStartTime(matchStartTime);

            String matchCode = switch (matchType) {
                case MatchType.QUALIFICATION -> "Q" + matchNumber;
                case MatchType.SEMIFINAL -> "SF" + matchNumber;
                case MatchType.FINAL -> "F" + matchNumber;
                default -> "";
            };
            match.setMatchCode(matchCode);
            match.setId(matchCode);

            String blueAllianceId = matchCode + "_B";
            String redAllianceId = matchCode + "_R";
            
            // Create team alliances
            AllianceTeam[] blueTeams = new AllianceTeam[blueTeamIds.length];
            for (int i = 0; i < blueTeamIds.length; i++) {
                AllianceTeam team = new AllianceTeam();
                team.setTeamId(blueTeamIds[i]);
                team.setAllianceId(blueAllianceId);
                blueTeams[i] = team;
            }

            AllianceTeam[] redTeams = new AllianceTeam[redTeamIds.length];
            for (int i = 0; i < redTeamIds.length; i++) {
                AllianceTeam team = new AllianceTeam();
                team.setTeamId(redTeamIds[i]);
                team.setAllianceId(redAllianceId);
                redTeams[i] = team;
            }


            // Create scores
            Score redScore = ScoreHandler.factoryScore();
            redScore.setAllianceId(redAllianceId);

            Score blueScore = ScoreHandler.factoryScore();
            blueScore.setAllianceId(blueAllianceId);

            dao.insert(Match.class, match);
            for (AllianceTeam team : redTeams) {
                dao.insert(AllianceTeam.class, team);
            }
            for (AllianceTeam team : blueTeams) {
                dao.insert(AllianceTeam.class, team);
            }
            dao.insert(Score.class, redScore);
            dao.insert(Score.class, blueScore);

            callback.onSuccess(match, "Match created successfully.");
        } catch (Exception e) {
            callback.onFailure(ErrorCode.CREATE_FAILED, "Failed to create match: " + e.getMessage());
        }
    }

    public void getMatch(String matchId, RequestCallback<Match> callback) {
        try {
            Match match = dao.read(Match.class, matchId);
            if (match != null) {
                callback.onSuccess(match, "Match retrieved successfully.");
            } else {
                callback.onFailure(ErrorCode.NOT_FOUND, "Match not found.");
            }
        } catch (Exception e) {
            callback.onFailure(ErrorCode.RETRIEVE_FAILED, "Failed to retrieve match: " + e.getMessage());
        }
    }

    public void updateMatch(Match match, RequestCallback<Match> callback) {
        try {
            dao.update(Match.class, match.getId(), match);
            callback.onSuccess(match, "Match updated successfully.");
        } catch (Exception e) {
            callback.onFailure(ErrorCode.UPDATE_FAILED, "Failed to update match: " + e.getMessage());
        }
    }

    public void deleteMatch(String matchId, RequestCallback<Void> callback) {
        try {
            dao.delete(Match.class, matchId);
            callback.onSuccess(null, "Match deleted successfully.");
        } catch (Exception e) {
            callback.onFailure(ErrorCode.DELETE_FAILED, "Failed to delete match: " + e.getMessage());
        }
    }

    public void generateMatchSchedule(int numberOfMatches) {

    }
}
