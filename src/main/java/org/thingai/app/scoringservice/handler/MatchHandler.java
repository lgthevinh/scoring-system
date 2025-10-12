package org.thingai.app.scoringservice.handler;

import org.thingai.app.scoringservice.callback.RequestCallback;
import org.thingai.app.scoringservice.define.ErrorCode;
import org.thingai.app.scoringservice.define.MatchType;
import org.thingai.app.scoringservice.entity.match.AllianceTeam;
import org.thingai.app.scoringservice.entity.score.Score;
import org.thingai.app.scoringservice.entity.team.Team;
import org.thingai.app.scoringservice.entity.time.TimeBlock;
import org.thingai.base.dao.Dao;
import org.thingai.app.scoringservice.entity.match.Match;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Random;

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

    public void listMatches(RequestCallback<Match[]> callback) {
        try {
            Match[] matches = dao.readAll(Match.class);
            callback.onSuccess(matches, "Matches retrieved successfully.");
        } catch (Exception e) {
            callback.onFailure(ErrorCode.RETRIEVE_FAILED, "Failed to retrieve matches: " + e.getMessage());
        }
    }

    /**
     * Generates a qualification match schedule.
     * @param numberOfMatches The total number of qualification matches to generate.
     * @param startTime       The start time of the event in "HH:mm" format.
     * @param matchDuration   The duration of each match in minutes.
     * @param timeBlocks      An array of break periods.
     * @param callback        Callback to signal completion or failure.
     */
    public void generateMatchSchedule(int numberOfMatches, String startTime, int matchDuration, TimeBlock[] timeBlocks, RequestCallback<Void> callback) {
        try {
            // NOTE: Assumes your DAO returns an array from readAll.
            Team[] allTeams = dao.readAll(Team.class);
            if (allTeams.length < 4) {
                callback.onFailure(ErrorCode.RETRIEVE_FAILED, "Cannot generate schedule with fewer than 4 teams.");
                return;
            }

            shuffleArray(allTeams);
            int teamPoolIndex = 0;

            DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");
            LocalTime currentTime = LocalTime.parse(startTime, timeFormatter);

            for (int i = 1; i <= numberOfMatches; i++) {
                // Check if the current time falls within a break
                for (TimeBlock block : timeBlocks) {
                    LocalTime breakStart = LocalTime.parse(block.getStartTime(), timeFormatter);
                    long breakDuration = Long.parseLong(block.getDuration());
                    LocalTime breakEnd = breakStart.plusMinutes(breakDuration);
                    if (!currentTime.isBefore(breakStart) && currentTime.isBefore(breakEnd)) {
                        currentTime = breakEnd; // Skip to the end of the break
                    }
                }

                // If the team pool is exhausted, reshuffle and start over
                if (teamPoolIndex > allTeams.length - 4) {
                    shuffleArray(allTeams);
                    teamPoolIndex = 0;
                }

                // Create alliances by picking from the shuffled array
                String[] redTeamIds = {allTeams[teamPoolIndex++].getTeamId(), allTeams[teamPoolIndex++].getTeamId()};
                String[] blueTeamIds = {allTeams[teamPoolIndex++].getTeamId(), allTeams[teamPoolIndex++].getTeamId()};

                // Create the match using the existing createMatch logic but with a direct call
                createMatchInternal(MatchType.QUALIFICATION, i, currentTime.format(timeFormatter), redTeamIds, blueTeamIds);

                // Advance time for the next match
                currentTime = currentTime.plusMinutes(matchDuration);
            }

            callback.onSuccess(null, "Match schedule generated successfully.");

        } catch (Exception e) {
            callback.onFailure(ErrorCode.CREATE_FAILED, "Failed to generate match schedule: " + e.getMessage());
        }
    }

    // Internal version of createMatch to be used by the scheduler, without the callback.
    private void createMatchInternal(int matchType, int matchNumber, String matchStartTime, String[] redTeamIds, String[] blueTeamIds) throws Exception {
        Match match = new Match();
        match.setMatchType(matchType);
        match.setMatchNumber(matchNumber);
        match.setMatchStartTime(matchStartTime);

        String matchCode = "Q" + matchNumber;
        match.setMatchCode(matchCode);
        match.setId(matchCode);

        String blueAllianceId = matchCode + "_B";
        String redAllianceId = matchCode + "_R";

        for (String teamId : redTeamIds) {
            AllianceTeam team = new AllianceTeam();
            team.setTeamId(teamId);
            team.setAllianceId(redAllianceId);
            dao.insert(AllianceTeam.class, team);
        }

        for (String teamId : blueTeamIds) {
            AllianceTeam team = new AllianceTeam();
            team.setTeamId(teamId);
            team.setAllianceId(blueAllianceId);
            dao.insert(AllianceTeam.class, team);
        }

        dao.insert(Match.class, match);
    }

    /**
     * Helper method to shuffle an array in-place using the Fisher-Yates algorithm.
     * @param array The array to be shuffled.
     */
    private <T> void shuffleArray(T[] array) {
        Random rand = new Random();
        for (int i = array.length - 1; i > 0; i--) {
            int index = rand.nextInt(i + 1);
            // Simple swap
            T a = array[index];
            array[index] = array[i];
            array[i] = a;
        }
    }
}
