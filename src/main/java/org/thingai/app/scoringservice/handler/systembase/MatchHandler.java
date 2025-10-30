package org.thingai.app.scoringservice.handler.systembase;

import org.thingai.app.scoringservice.callback.RequestCallback;
import org.thingai.app.scoringservice.define.ErrorCode;
import org.thingai.app.scoringservice.define.MatchType;
import org.thingai.app.scoringservice.dto.MatchDetailDto;
import org.thingai.app.scoringservice.entity.match.AllianceTeam;
import org.thingai.app.scoringservice.entity.score.Score;
import org.thingai.app.scoringservice.entity.team.Team;
import org.thingai.app.scoringservice.entity.time.TimeBlock;
import org.thingai.app.scoringservice.handler.MatchMakerHandler;
import org.thingai.base.cache.LRUCache;
import org.thingai.base.dao.Dao;
import org.thingai.app.scoringservice.entity.match.Match;
import org.thingai.base.log.ILog;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MatchHandler {
    private final Dao dao;

    private final MatchMakerHandler matchMakerHandler = new MatchMakerHandler();

    // Caches are now injected via the constructor for better management.
    private LRUCache<String, Match> matchCache;
    private final LRUCache<String, AllianceTeam[]> allianceTeamCache;
    private final LRUCache<String, Team> teamCache;

    // Flag to indicate if the cache is stale and needs to be refreshed from the database.
    private boolean matchUpdateFlag = true; // Start as true to force initial loads.

    public MatchHandler(Dao dao, LRUCache<String, Match> matchCache, LRUCache<String, AllianceTeam[]> allianceTeamCache, LRUCache<String, Team> teamCache) {
        this.dao = dao;
        this.matchCache = matchCache;
        this.allianceTeamCache = allianceTeamCache;
        this.teamCache = teamCache;

        this.matchMakerHandler.setBinPath(Paths.get("bin").toAbsolutePath().toString() + "/MatchMaker.exe");

        Path outPath = Paths.get("data");
        if (!Files.exists(outPath)) {
            try {
                Files.createDirectories(outPath.getParent());
                Files.createFile(outPath);
                ILog.d("MatchHandler", "Created match schedule output file at: " + outPath.toAbsolutePath());
            } catch (Exception e) {
                ILog.e("MatchHandler", "Error creating match schedule output file: " + e.getMessage());
            }
        }

        String outDir = outPath.toAbsolutePath() + "/match_schedule.txt";
        ILog.d("MatchHandler", "Match schedule output path set to: " + outDir);
        this.matchMakerHandler.setOutPath(outDir);
    }

    // Methods use outside system implementation
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
        if (isMatchUpdateFlag()) {
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

    public void getMatchDetail(String matchId, RequestCallback<MatchDetailDto> callback) {
        try {
            Match match = dao.read(Match.class, matchId);
            if (match == null) {
                callback.onFailure(ErrorCode.NOT_FOUND, "Match not found.");
                return;
            }

            String redAllianceId = match.getMatchCode() + "_R";
            String blueAllianceId = match.getMatchCode() + "_B";

            AllianceTeam[] redAllianceTeams = dao.query(AllianceTeam.class, new String[]{"allianceId"}, new String[]{redAllianceId});
            AllianceTeam[] blueAllianceTeams = dao.query(AllianceTeam.class, new String[]{"allianceId"}, new String[]{blueAllianceId});
            Team[] redTeams = Arrays.stream(redAllianceTeams)
                    .map(at -> {
                        try {
                            return dao.read(Team.class, at.getTeamId());
                        } catch (Exception e) {
                            return null;
                        }
                    })
                    .filter(Objects::nonNull)
                    .toArray(Team[]::new);
            Team[] blueTeams = Arrays.stream(blueAllianceTeams)
                    .map(at -> {
                        try {
                            return dao.read(Team.class, at.getTeamId());
                        } catch (Exception e) {
                            return null;
                        }
                    })
                    .filter(Objects::nonNull)
                    .toArray(Team[]::new);
            MatchDetailDto detailDto = new MatchDetailDto(match, redTeams, blueTeams);
            callback.onSuccess(detailDto, "Match detail retrieved successfully.");
        } catch (Exception e) {
            callback.onFailure(ErrorCode.RETRIEVE_FAILED, "Failed to retrieve match detail: " + e.getMessage());
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

    /**
     * V2 schedule generator:
     * - Uses external MatchMakerHandler to produce a schedule file.
     * - Parses "Match Schedule" lines into 2v2 team pairings.
     * - Keeps existing time generation (start time, duration, TimeBlocks).
     */
    public void generateMatchScheduleV2(int rounds, String startTime, int matchDuration, TimeBlock[] timeBlocks, RequestCallback<Void> callback) {
        ILog.d("MatchHandler", "Generating match schedule V2 with rounds=" + rounds + ", start=" + startTime + ", duration=" + matchDuration + " min");
        try {
            // 1) Load teams and reset schedule-related tables and caches
            Team[] allTeams = dao.readAll(Team.class);

            dao.deleteAll(Match.class);
            dao.deleteAll(AllianceTeam.class);
            dao.deleteAll(Score.class);

            matchCache.clear();
            allianceTeamCache.clear();
            teamCache.clear();

            if (allTeams == null || allTeams.length < 4) {
                callback.onFailure(ErrorCode.CREATE_FAILED, "Cannot generate schedule with fewer than 4 teams.");
                return;
            }

            // 2) Deterministic team index mapping 1..N -> teamId
            //    Sort by numeric teamId if possible, otherwise lexicographic.
            List<Team> orderedTeams = new ArrayList<>(Arrays.asList(allTeams));
            orderedTeams.sort((a, b) -> {
                String as = a.getTeamId();
                String bs = b.getTeamId();
                try {
                    int ai = Integer.parseInt(as.replaceAll("\\D", ""));
                    int bi = Integer.parseInt(bs.replaceAll("\\D", ""));
                    return Integer.compare(ai, bi);
                } catch (NumberFormatException nfe) {
                    return as.compareTo(bs);
                }
            });

            // 3) Run external generator (2 teams per alliance by default)
            //    Ensure MatchMakerHandler is configured with bin/out paths before calling this method.
            int exitCode = matchMakerHandler.generateMatchSchedule(rounds, orderedTeams.size(), 2);
            if (exitCode != 0) {
                callback.onFailure(ErrorCode.CREATE_FAILED, "MatchMaker.exe failed (exitCode=" + exitCode + "). Check matchmaker.log for details.");
                return;
            }

            // 4) Read generated schedule file (MatchMakerHandler.getOutPath() can be a file or directory)
            Path schedulePath = Paths.get(matchMakerHandler.getOutPath()).toAbsolutePath().normalize();
            if (Files.isDirectory(schedulePath)) {
                callback.onFailure(ErrorCode.RETRIEVE_FAILED, "OutPath is a directory. Please point MatchMakerHandler.outPath to the schedule file to parse.");
                return;
            }
            if (!Files.exists(schedulePath)) {
                callback.onFailure(ErrorCode.RETRIEVE_FAILED, "Schedule file not found at: " + schedulePath);
                return;
            }

            List<String> lines = Files.readAllLines(schedulePath);
            List<ParsedMatch> parsedMatches = parseMatchMakerSchedule(lines);
            if (parsedMatches.isEmpty()) {
                callback.onFailure(ErrorCode.RETRIEVE_FAILED, "No matches parsed from schedule file. Ensure the output format contains a 'Match Schedule' section.");
                return;
            }

            // 5) Time keeping (same as V1): parse start time, for each match advance time with breaks
            DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm");
            LocalDateTime currentTime = LocalDateTime.parse(startTime, timeFormatter);

            int matchNumber = 1;
            for (ParsedMatch pm : parsedMatches) {
                // Respect time blocks by skipping breaks
                if (timeBlocks != null) {
                    for (TimeBlock block : timeBlocks) {
                        LocalDateTime breakStart = LocalDateTime.parse(block.getStartTime(), timeFormatter);
                        long breakDuration = Long.parseLong(block.getDuration());
                        LocalDateTime breakEnd = breakStart.plusMinutes(breakDuration);

                        // If currentTime is within a break, jump to breakEnd
                        if (!currentTime.isBefore(breakStart) && currentTime.isBefore(breakEnd)) {
                            currentTime = breakEnd;
                        }
                    }
                }

                // Map team indexes to team IDs (1-based index in file -> 0-based list index)
                if (pm.red.length < 2 || pm.blue.length < 2) {
                    ILog.w("MatchHandler", "Skipping malformed match line: not enough teams. " + Arrays.toString(pm.red) + " vs " + Arrays.toString(pm.blue));
                    continue;
                }

                String[] redTeamIds = new String[] {
                        mapTeamIndexToId(orderedTeams, pm.red[0]),
                        mapTeamIndexToId(orderedTeams, pm.red[1])
                };
                String[] blueTeamIds = new String[] {
                        mapTeamIndexToId(orderedTeams, pm.blue[0]),
                        mapTeamIndexToId(orderedTeams, pm.blue[1])
                };

                ILog.d("MatchHandler", Arrays.toString(redTeamIds) + " vs " + Arrays.toString(blueTeamIds) + " at " + currentTime.format(timeFormatter));

                // Create match
                createMatchInternal(MatchType.QUALIFICATION, matchNumber, currentTime.format(timeFormatter), redTeamIds, blueTeamIds);

                // Advance time to next slot
                currentTime = currentTime.plusMinutes(matchDuration);
                matchNumber++;
            }

            setMatchUpdateFlag(true);
            callback.onSuccess(null, "Match schedule generated successfully by MatchMaker and times assigned.");
        } catch (Exception e) {
            callback.onFailure(ErrorCode.CREATE_FAILED, "Failed to generate match schedule: " + e.getMessage());
        }
    }

    /**
     * Parse the "Match Schedule" section from the MatchMaker .txt output.
     * Expected lines like:
     *   "  1:    4     7     5     8 "
     * Optional '*' after a team number denotes surrogate; ignored for now.
     */
    private List<ParsedMatch> parseMatchMakerSchedule(List<String> lines) {
        List<ParsedMatch> matches = new ArrayList<>();
        boolean inSchedule = false;
        Pattern linePattern = Pattern.compile("^\\s*(\\d+)\\s*:\\s*(\\d+\\*?)\\s+(\\d+\\*?)\\s+(\\d+\\*?)\\s+(\\d+\\*?).*$");

        for (String raw : lines) {
            String line = raw == null ? "" : raw.trim();

            if (!inSchedule) {
                // Enter section after encountering "Match Schedule"
                if (line.equalsIgnoreCase("Match Schedule")) {
                    inSchedule = true;
                }
                continue;
            }

            // Skip header separators or blank lines
            if (line.isEmpty() || line.startsWith("------")) {
                continue;
            }

            // Stop at "Schedule Statistics" or another section
            if (line.toLowerCase().startsWith("schedule statistics")) {
                break;
            }

            Matcher m = linePattern.matcher(raw);
            if (m.matches()) {
                // m.group(1) = match number (unused)
                int t1 = parseTeamIndex(m.group(2));
                int t2 = parseTeamIndex(m.group(3));
                int t3 = parseTeamIndex(m.group(4));
                int t4 = parseTeamIndex(m.group(5));

                ParsedMatch pm = new ParsedMatch();
                pm.red = new int[]{t1, t2};
                pm.blue = new int[]{t3, t4};
                matches.add(pm);
            }
        }
        return matches;
    }

    private int parseTeamIndex(String token) {
        // Remove any trailing '*' (surrogate marker)
        String digits = token.replace("*", "").trim();
        try {
            return Integer.parseInt(digits);
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    private String mapTeamIndexToId(List<Team> orderedTeams, int idx1Based) throws Exception {
        if (idx1Based < 1 || idx1Based > orderedTeams.size()) {
            throw new Exception("Team index out of bounds in schedule: " + idx1Based);
        }
        return orderedTeams.get(idx1Based - 1).getTeamId();
    }

    // Simple holder for a parsed 2v2 match
    private static class ParsedMatch {
        int[] red;
        int[] blue;
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

    // Methods use inside system implementation
    public Match getMatchSync(String matchId) throws Exception {
        if (isMatchUpdateFlag()) {
            Match cachedMatch = matchCache.get(matchId);
            if (cachedMatch != null) {
                return cachedMatch;
            }
        }

        Match match = dao.read(Match.class, matchId);
        if (match != null) {
            matchCache.put(matchId, match); // Add to cache
            return match;
        } else {
            throw new Exception("Match not found.");
        }
    }

    public MatchDetailDto getMatchDetailSync(String matchId) throws Exception {
        Match match = dao.read(Match.class, matchId);
        if (match == null) {
            throw new Exception("Match not found.");
        }

        String redAllianceId = match.getMatchCode() + "_R";
        String blueAllianceId = match.getMatchCode() + "_B";

        AllianceTeam[] redAllianceTeams = dao.query(AllianceTeam.class, new String[]{"allianceId"}, new String[]{redAllianceId});
        AllianceTeam[] blueAllianceTeams = dao.query(AllianceTeam.class, new String[]{"allianceId"}, new String[]{blueAllianceId});
        Team[] redTeams = Arrays.stream(redAllianceTeams)
                .map(at -> {
                    try {
                        return dao.read(Team.class, at.getTeamId());
                    } catch (Exception e) {
                        return null;
                    }
                })
                .filter(Objects::nonNull)
                .toArray(Team[]::new);
        Team[] blueTeams = Arrays.stream(blueAllianceTeams)
                .map(at -> {
                    try {
                        return dao.read(Team.class, at.getTeamId());
                    } catch (Exception e) {
                        return null;
                    }
                })
                .filter(Objects::nonNull)
                .toArray(Team[]::new);
        return new MatchDetailDto(match, redTeams, blueTeams);
    }

    // Utility methods
    private <T> void shuffleArray(T[] array) {
        Random rand = new Random();
        for (int i = array.length - 1; i > 0; i--) {
            int index = rand.nextInt(i + 1);
            T a = array[index];
            array[index] = array[i];
            array[i] = a;
        }
    }

    private boolean isMatchUpdateFlag() {
        return !matchUpdateFlag;
    }

    private void setMatchUpdateFlag(boolean matchUpdateFlag) {
        this.matchUpdateFlag = matchUpdateFlag;
        if (this.matchUpdateFlag) {
            // If the flag is set to dirty, clear all caches to force reloads.
            matchCache.clear();
            allianceTeamCache.clear();
            teamCache.clear();
        }
    }

    public void setMatchCache(LRUCache<String, Match> matchCache) {
        this.matchCache = matchCache;
    }
}

