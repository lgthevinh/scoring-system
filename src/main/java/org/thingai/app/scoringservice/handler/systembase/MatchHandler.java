package org.thingai.app.scoringservice.handler.systembase;

import org.thingai.app.scoringservice.callback.RequestCallback;
import org.thingai.app.scoringservice.define.ErrorCode;
import org.thingai.app.scoringservice.define.MatchType;
import org.thingai.app.scoringservice.dto.MatchDetailDto;
import org.thingai.app.scoringservice.entity.match.AllianceTeam;
import org.thingai.app.scoringservice.entity.score.Score;
import org.thingai.app.scoringservice.entity.team.Team;
import org.thingai.app.scoringservice.entity.time.TimeBlock;
import org.thingai.base.cache.LRUCache;
import org.thingai.base.dao.Dao;
import org.thingai.app.scoringservice.entity.match.Match;
import org.thingai.base.log.ILog;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class MatchHandler {
    private final Dao dao;

    // Caches are now injected via the constructor for better management.
    private final LRUCache<String, Match> matchCache;
    private final LRUCache<String, AllianceTeam[]> allianceTeamCache;
    private final LRUCache<String, Team> teamCache;

    // Flag to indicate if the cache is stale and needs to be refreshed from the database.
    private boolean matchUpdateFlag = true; // Start as true to force initial load.

    public MatchHandler(Dao dao, LRUCache<String, Match> matchCache, LRUCache<String, AllianceTeam[]> allianceTeamCache, LRUCache<String, Team> teamCache) {
        this.dao = dao;
        this.matchCache = matchCache;
        this.allianceTeamCache = allianceTeamCache;
        this.teamCache = teamCache;
    }

    public void createMatch(int matchType, int matchNumber, String matchStartTime, String[] redTeamIds, String[] blueTeamIds, RequestCallback<Match> callback) {
        try {
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

            // Create scores (as per original code)
            Score redScore = ScoreHandler.factoryScore();
            redScore.setAllianceId(redAllianceId);

            Score blueScore = ScoreHandler.factoryScore();
            blueScore.setAllianceId(blueAllianceId);

            dao.insert(Match.class, match);
            dao.insert(Score.class, redScore);
            dao.insert(Score.class, blueScore);

            setMatchUpdateFlag(true); // Invalidate cache
            callback.onSuccess(match, "Match created successfully.");
        } catch (Exception e) {
            callback.onFailure(ErrorCode.CREATE_FAILED, "Failed to create match: " + e.getMessage());
        }
    }

    public void getMatch(String matchId, RequestCallback<Match> callback) {
        if (!isMatchUpdateFlag()) {
            Match cachedMatch = matchCache.get(matchId);
            if (cachedMatch != null) {
                callback.onSuccess(cachedMatch, "Match retrieved successfully from cache.");
                return;
            }
        }

        try {
            Match match = dao.read(Match.class, matchId);
            if (match != null) {
                matchCache.put(matchId, match); // Add to cache
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
            matchCache.put(match.getId(), match);
            setMatchUpdateFlag(true);
            callback.onSuccess(match, "Match updated successfully.");
        } catch (Exception e) {
            callback.onFailure(ErrorCode.UPDATE_FAILED, "Failed to update match: " + e.getMessage());
        }
    }

    public void deleteMatch(String matchId, RequestCallback<Void> callback) {
        try {
            dao.delete(Match.class, matchId);
            matchCache.remove(matchId);
            setMatchUpdateFlag(true);
            callback.onSuccess(null, "Match deleted successfully.");
        } catch (Exception e) {
            callback.onFailure(ErrorCode.DELETE_FAILED, "Failed to delete match: " + e.getMessage());
        }
    }

    public void listMatches(RequestCallback<Match[]> callback) {
        try {
            Match[] matches = dao.readAll(Match.class);
            matchCache.clear();
            for (Match match : matches) {
                matchCache.put(match.getId(), match);
            }
            setMatchUpdateFlag(false);
            callback.onSuccess(matches, "Matches retrieved successfully.");
        } catch (Exception e) {
            callback.onFailure(ErrorCode.RETRIEVE_FAILED, "Failed to retrieve matches: " + e.getMessage());
        }
    }

    public void listMatchesByType(int matchType, RequestCallback<Match[]> callback) {
        try {
            Match[] matches = dao.query(Match.class, new String[]{"matchType"}, new String[]{String.valueOf(matchType)});
            for (Match match : matches) {
                matchCache.put(match.getId(), match);
            }
            callback.onSuccess(matches, "Matches retrieved successfully.");
        } catch (Exception e) {
            callback.onFailure(ErrorCode.RETRIEVE_FAILED, "Failed to retrieve matches: " + e.getMessage());
        }
    }

    public void listMatchDetails(int matchType, RequestCallback<MatchDetailDto[]> callback) {
        try {
            Match[] matches = dao.query(Match.class, new String[]{"matchType"}, new String[]{String.valueOf(matchType)});
            List<MatchDetailDto> detailsList = new ArrayList<>();

            for (Match match : matches) {
                matchCache.put(match.getId(), match);

                String redAllianceId = match.getMatchCode() + "_R";
                String blueAllianceId = match.getMatchCode() + "_B";

                // Use cache for alliance teams
                AllianceTeam[] redAllianceTeams = allianceTeamCache.get(redAllianceId);
                if (redAllianceTeams == null) {
                    redAllianceTeams = dao.query(AllianceTeam.class, new String[]{"allianceId"}, new String[]{redAllianceId});
                    allianceTeamCache.put(redAllianceId, redAllianceTeams);
                }

                AllianceTeam[] blueAllianceTeams = allianceTeamCache.get(blueAllianceId);
                if (blueAllianceTeams == null) {
                    blueAllianceTeams = dao.query(AllianceTeam.class, new String[]{"allianceId"}, new String[]{blueAllianceId});
                    allianceTeamCache.put(blueAllianceId, blueAllianceTeams);
                }

                // Use cache for individual teams
                Team[] redTeams = Arrays.stream(redAllianceTeams)
                        .map(at -> {
                            Team team = teamCache.get(at.getTeamId());
                            if (team == null) {
                                team = dao.read(Team.class, at.getTeamId());
                                if (team != null) teamCache.put(at.getTeamId(), team);
                            }
                            return team;
                        })
                        .filter(Objects::nonNull)
                        .toArray(Team[]::new);

                Team[] blueTeams = Arrays.stream(blueAllianceTeams)
                        .map(at -> {
                            Team team = teamCache.get(at.getTeamId());
                            if (team == null) {
                                team = dao.read(Team.class, at.getTeamId());
                                if (team != null) teamCache.put(at.getTeamId(), team);
                            }
                            return team;
                        })
                        .filter(Objects::nonNull)
                        .toArray(Team[]::new);

                detailsList.add(new MatchDetailDto(match, redTeams, blueTeams));
            }

            callback.onSuccess(detailsList.toArray(new MatchDetailDto[0]), "Match details retrieved successfully.");
        } catch (Exception e) {
            callback.onFailure(ErrorCode.RETRIEVE_FAILED, "Failed to retrieve match details: " + e.getMessage());
        }
    }

    public void generateMatchSchedule(int numberOfMatches, String startTime, int matchDuration, TimeBlock[] timeBlocks, RequestCallback<Void> callback) {
        try {
            Team[] allTeams = dao.readAll(Team.class);

            dao.deleteAll(Match.class);
            dao.deleteAll(AllianceTeam.class);
            dao.deleteAll(Score.class);
            matchCache.clear();
            allianceTeamCache.clear();
            teamCache.clear();

            if (allTeams.length < 4) {
                callback.onFailure(ErrorCode.CREATE_FAILED, "Cannot generate schedule with fewer than 4 teams.");
                return;
            }

            shuffleArray(allTeams);
            int teamPoolIndex = 0;

            DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm");
            LocalDateTime currentTime = LocalDateTime.parse(startTime, timeFormatter);

            for (int i = 1; i <= numberOfMatches; i++) {
                for (TimeBlock block : timeBlocks) {
                    LocalDateTime breakStart = LocalDateTime.parse(block.getStartTime(), timeFormatter);
                    long breakDuration = Long.parseLong(block.getDuration());
                    LocalDateTime breakEnd = breakStart.plusMinutes(breakDuration);
                    if (!currentTime.isBefore(breakStart) && currentTime.isBefore(breakEnd)) {
                        currentTime = breakEnd;
                    }
                }

                if (teamPoolIndex > allTeams.length - 4) {
                    shuffleArray(allTeams);
                    teamPoolIndex = 0;
                }

                String[] redTeamIds = {allTeams[teamPoolIndex++].getTeamId(), allTeams[teamPoolIndex++].getTeamId()};
                String[] blueTeamIds = {allTeams[teamPoolIndex++].getTeamId(), allTeams[teamPoolIndex++].getTeamId()};

                ILog.d("MatchHandler", Arrays.toString(redTeamIds) + " vs " + Arrays.toString(blueTeamIds) + " at " + currentTime.format(timeFormatter));

                createMatchInternal(MatchType.QUALIFICATION, i, currentTime.format(timeFormatter), redTeamIds, blueTeamIds);

                currentTime = currentTime.plusMinutes(matchDuration);
            }

            setMatchUpdateFlag(true);
            callback.onSuccess(null, "Match schedule generated successfully.");

        } catch (Exception e) {
            callback.onFailure(ErrorCode.CREATE_FAILED, "Failed to generate match schedule: " + e.getMessage());
        }
    }

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

        Score redScore = ScoreHandler.factoryScore();
        redScore.setAllianceId(redAllianceId);

        Score blueScore = ScoreHandler.factoryScore();
        blueScore.setAllianceId(blueAllianceId);

        dao.insert(Match.class, match);
        dao.insert(Score.class, redScore);
        dao.insert(Score.class, blueScore);
    }

    private <T> void shuffleArray(T[] array) {
        Random rand = new Random();
        for (int i = array.length - 1; i > 0; i--) {
            int index = rand.nextInt(i + 1);
            T a = array[index];
            array[index] = array[i];
            array[i] = a;
        }
    }

    public boolean isMatchUpdateFlag() {
        return matchUpdateFlag;
    }

    public void setMatchUpdateFlag(boolean matchUpdateFlag) {
        this.matchUpdateFlag = matchUpdateFlag;
        if (this.matchUpdateFlag) {
            // If the flag is set to dirty, clear all caches to force reloads.
            matchCache.clear();
            allianceTeamCache.clear();
            teamCache.clear();
        }
    }
}

