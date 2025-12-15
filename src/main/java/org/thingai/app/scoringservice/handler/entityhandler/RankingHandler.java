package org.thingai.app.scoringservice.handler.entityhandler;

import org.thingai.app.scoringservice.callback.RequestCallback;
import org.thingai.app.scoringservice.define.MatchType;
import org.thingai.app.scoringservice.dto.MatchDetailDto;
import org.thingai.app.scoringservice.entity.ranking.IRankingStrategy;
import org.thingai.app.scoringservice.entity.ranking.RankingEntry;
import org.thingai.app.scoringservice.entity.ranking.RankingStat;
import org.thingai.app.scoringservice.entity.score.Score;
import org.thingai.app.scoringservice.entity.team.Team;
import org.thingai.base.dao.Dao;
import org.thingai.base.log.ILog;

import java.util.Arrays;

/**
 * Manages team ranking calculations for FIRST Robotics Competition (FRC) events.
 *
 * This handler implements the official FRC qualification ranking system where:
 * - Teams earn ranking points based on match performance (win/loss/tie)
 * - Ranking points determine qualification seeding for playoffs
 * - Higher ranking points = better seeding position
 * - Ties broken by total score, then fewest penalty points
 *
 * @author FRC Scoring System Team
 * @since 1.0.0
 */
public class RankingHandler {
    private static final String TAG = "RankingHandler";

    private Dao dao;
    private MatchHandler matchHandler;
    private IRankingStrategy rankingStrategy;

    public RankingHandler(Dao dao, MatchHandler matchHandler) {
        this.dao = dao;
        this.matchHandler = matchHandler;
        this.rankingStrategy = new IndividualTeamRankingStrategy();

        ILog.i(TAG, "RankingHandler initialized with IndividualTeamRankingStrategy");
    }

    /**
     * Updates ranking entries for all teams in a completed match.
     *
     * This method processes the match results and updates each team's ranking statistics:
     * - Increments matches played
     * - Adds ranking points based on win/loss/tie
     * - Accumulates total score and penalties
     * - Tracks highest individual match score
     * - Updates win count
     *
     * @param matchDetailDto Complete match information including teams and scores
     * @param blueScore Final score for blue alliance
     * @param redScore Final score for red alliance
     */
    public void updateRankingEntry(MatchDetailDto matchDetailDto, Score blueScore, Score redScore) {
        // Calculate ranking statistics for each team based on match performance
        RankingStat[] teamStats = rankingStrategy.setRankingStat(matchDetailDto, blueScore, redScore);

        ILog.d(TAG, "Updating rankings for " + teamStats.length + " teams in match " + matchDetailDto.getMatch().getId());

        for (RankingStat teamMatchStats : teamStats) {
            String teamId = teamMatchStats.getTeamId();

            try {
                // Check if this team already has ranking data
                RankingEntry[] existingEntries = dao.query(RankingEntry.class,
                    new String[]{"teamId"}, new String[]{teamId});

                RankingEntry teamRankingEntry;

                if (existingEntries == null || existingEntries.length == 0) {
                    // First match for this team - create new ranking entry
                    teamRankingEntry = createInitialRankingEntry(teamMatchStats);
                    ILog.d(TAG, "Created new ranking entry for team " + teamId);
                } else {
                    // Team has played before - update existing entry
                    teamRankingEntry = existingEntries[0];
                    updateExistingRankingEntry(teamRankingEntry, teamMatchStats);
                    ILog.d(TAG, "Updated existing ranking entry for team " + teamId);
                }

                // Update highest score if this match score is better
                if (teamMatchStats.getScore() > teamRankingEntry.getHighestScore()) {
                    teamRankingEntry.setHighestScore(teamMatchStats.getScore());
                }

                // Save the updated ranking entry
                dao.insertOrUpdate(teamRankingEntry);

            } catch (Exception databaseError) {
                ILog.e(TAG, "Failed to update ranking for team " + teamId +
                    ": " + databaseError.getMessage());
                databaseError.printStackTrace();
            }
        }
    }

    /**
     * Creates initial ranking entry for a team playing their first match.
     */
    private RankingEntry createInitialRankingEntry(RankingStat firstMatchStats) {
        RankingEntry entry = new RankingEntry();
        entry.setTeamId(firstMatchStats.getTeamId());
        entry.setRankingPoints(firstMatchStats.getRankingPoints());
        entry.setTotalScore(firstMatchStats.getScore());
        entry.setTotalPenalties(firstMatchStats.getPenalties());
        entry.setMatchesPlayed(1);
        entry.setWins(firstMatchStats.isWin() ? 1 : 0);
        entry.setHighestScore(firstMatchStats.getScore()); // First match is highest so far
        return entry;
    }

    /**
     * Updates existing ranking entry with results from another match.
     */
    private void updateExistingRankingEntry(RankingEntry existingEntry, RankingStat newMatchStats) {
        existingEntry.setMatchesPlayed(existingEntry.getMatchesPlayed() + 1);
        existingEntry.setTotalScore(existingEntry.getTotalScore() + newMatchStats.getScore());
        existingEntry.setTotalPenalties(existingEntry.getTotalPenalties() + newMatchStats.getPenalties());
        existingEntry.setRankingPoints(existingEntry.getRankingPoints() + newMatchStats.getRankingPoints());

        if (newMatchStats.isWin()) {
            existingEntry.setWins(existingEntry.getWins() + 1);
        }
    }

    /**
     * Retrieves the current qualification rankings for all teams.
     *
     * @param callback Optional callback for async processing
     * @return Array of ranking entries sorted by official FRC ranking rules
     */
    public RankingEntry[] getRankingStatus(RequestCallback<RankingEntry[]> callback) {
        try {
            RankingEntry[] allEntries = dao.readAll(RankingEntry.class);
            RankingEntry[] sortedEntries = rankingStrategy.sortRankingEntries(allEntries);

            ILog.d(TAG, "Fetched " + sortedEntries.length + " ranking entries");

            if (callback != null) {
                callback.onSuccess(sortedEntries, "Successfully retrieved and sorted team rankings");
            }

            return sortedEntries;

        } catch (Exception databaseError) {
            ILog.e(TAG, "Failed to retrieve ranking status: " + databaseError.getMessage());
            databaseError.printStackTrace();

            if (callback != null) {
                callback.onFailure(-1, "Database error while fetching rankings: " + databaseError.getMessage());
            }

            return new RankingEntry[0]; // Return empty array on error
        }
    }
    /**
     * Recalculates rankings from scratch using all qualification match data.
     *
     * This operation:
     * 1. Clears all existing ranking data
     * 2. Re-processes every completed qualification match
     * 3. Rebuilds rankings from historical data
     *
     * Useful when match scores have been corrected or ranking logic has changed.
     *
     * @param callback Optional callback for completion notification
     */
    public void recalculateRankings(RequestCallback<Boolean> callback) {
        // Clear all existing ranking data
        try {
            dao.deleteAll(RankingEntry.class);
            ILog.i(TAG, "Cleared all existing ranking entries for recalculation");
        } catch (Exception clearError) {
            ILog.e(TAG, "Failed to clear ranking entries: " + clearError.getMessage());
            if (callback != null) {
                callback.onFailure(-1, "Failed to clear existing rankings: " + clearError.getMessage());
            }
            return;
        }

        // Run recalculation in background thread to avoid blocking
        new Thread(() -> {
            try {
                ILog.d(TAG, "Starting ranking recalculation for qualification matches");

                // Fetch all qualification matches with their details
                matchHandler.listMatchDetails(MatchType.QUALIFICATION, true,
                    new RequestCallback<MatchDetailDto[]>() {
                        @Override
                        public void onSuccess(MatchDetailDto[] allMatches, String message) {
                            int processedMatches = 0;
                            int skippedMatches = 0;

                            for (MatchDetailDto matchDetail : allMatches) {
                                try {
                                    Score blueScore = matchDetail.getBlueScore();
                                    Score redScore = matchDetail.getRedScore();

                                    // Only process completed matches with final scores
                                    if (matchDetail.getMatch().getActualStartTime() == null) {
                                        skippedMatches++;
                                        continue;
                                    }

                                    // Recalculate rankings for this match
                                    updateRankingEntry(matchDetail, blueScore, redScore);
                                    processedMatches++;

                                    ILog.d(TAG, "Recalculated rankings for match " +
                                        matchDetail.getMatch().getId() + " (" + processedMatches + " processed)");

                                } catch (Exception matchError) {
                                    ILog.w(TAG, "Failed to recalculate rankings for match " +
                                        matchDetail.getMatch().getId() + ": " + matchError.getMessage());
                                }
                            }

                            ILog.i(TAG, "Ranking recalculation completed: " + processedMatches +
                                " matches processed, " + skippedMatches + " matches skipped");

                            if (callback != null) {
                                callback.onSuccess(true, "Successfully recalculated rankings from " +
                                    processedMatches + " qualification matches");
                            }
                        }

                        @Override
                        public void onFailure(int errorCode, String errorMessage) {
                            ILog.e(TAG, "Failed to fetch match details for recalculation: " + errorMessage);
                            if (callback != null) {
                                callback.onFailure(errorCode, "Failed to fetch match data: " + errorMessage);
                            }
                        }
                    });

            } catch (Exception threadError) {
                ILog.e(TAG, "Unexpected error during ranking recalculation: " + threadError.getMessage());
                threadError.printStackTrace();

                if (callback != null) {
                    callback.onFailure(-1, "Unexpected error during recalculation: " + threadError.getMessage());
                }
            }
        }, "RankingRecalculationThread").start();
    }

    public void setRankingStrategy(IRankingStrategy strategy) {
        this.rankingStrategy = strategy;
    }

    public void setDao(Dao dao) {
        this.dao = dao;
    }

    static class DefaultRankingStrategy implements IRankingStrategy {
        @Override
        public RankingEntry[] sortRankingEntries(RankingEntry[] entries) {
            // Sort by ranking points descending, then by total score descending, then by penalties ascending
            Arrays.sort(entries, (a, b) -> {
                if (b.getRankingPoints() != a.getRankingPoints()) {
                    return Integer.compare(b.getRankingPoints(), a.getRankingPoints());
                } else if (b.getTotalScore() != a.getTotalScore()) {
                    return Integer.compare(b.getTotalScore(), a.getTotalScore());
                } else {
                    return Integer.compare(a.getTotalPenalties(), b.getTotalPenalties());
                }
            });
            return entries;
        }

        @Override
        public RankingStat[] setRankingStat(MatchDetailDto matchDetailDto, Score blueScore, Score redScore) {
            Team[] redTeams = matchDetailDto.getRedTeams();
            Team[] blueTeams = matchDetailDto.getBlueTeams();
            RankingStat[] stats = new RankingStat[redTeams.length + blueTeams.length];

            int index = 0;
            for (Team team : blueTeams) {
                RankingStat stat = new RankingStat();
                stat.setTeamId(team.getTeamId());
                stat.setScore(blueScore.getTotalScore());
                stat.setPenalties(blueScore.getPenaltiesScore());
                boolean isWin = blueScore.getTotalScore() > redScore.getTotalScore();
                stat.setWin(isWin);
                stat.setRankingPoints(isWin ? 3 : 1);
                stats[index++] = stat;
            }

            for (Team team : redTeams) {
                RankingStat stat = new RankingStat();
                stat.setTeamId(team.getTeamId());
                stat.setScore(redScore.getTotalScore());
                stat.setPenalties(redScore.getPenaltiesScore());
                boolean isWin = redScore.getTotalScore() > blueScore.getTotalScore();
                stat.setWin(isWin);
                stat.setRankingPoints(isWin ? 3 : 1);
                stats[index++] = stat;
            }

            return stats;
        }
    }

    /**
     * Implements individual team ranking strategy for FRC events.
     *
     * Key features:
     * - Each team gets their proportional share of alliance score
     * - Win/loss/tie determined at alliance level but applied to all alliance members
     * - Supports variable alliance sizes (typically 2-3 teams)
     * - Accumulates statistics across all qualification matches
     */
    static class IndividualTeamRankingStrategy implements IRankingStrategy {
        // FRC Official Ranking Points System:
        // - Win: 3 points (beat opponent)
        // - Tie: 2 points (tied with opponent)
        // - Loss: 1 point (lost to opponent)
        private static final int WIN_POINTS = 3;
        private static final int TIE_POINTS = 2;
        private static final int LOSS_POINTS = 1;


        @Override
        public RankingEntry[] sortRankingEntries(RankingEntry[] entries) {
            // Official FRC ranking sort order:
            // 1. Ranking Points (descending)
            // 2. Total Qualification Score (descending)
            // 3. Penalty Points (ascending - fewer penalties rank higher)
            Arrays.sort(entries, (teamA, teamB) -> {
                // Primary sort: ranking points
                if (teamB.getRankingPoints() != teamA.getRankingPoints()) {
                    return Integer.compare(teamB.getRankingPoints(), teamA.getRankingPoints());
                }
                // Secondary sort: total score across all matches
                if (teamB.getTotalScore() != teamA.getTotalScore()) {
                    return Integer.compare(teamB.getTotalScore(), teamA.getTotalScore());
                }
                // Tertiary sort: fewer penalties rank higher
                return Integer.compare(teamA.getTotalPenalties(), teamB.getTotalPenalties());
            });
            return entries;
        }

        @Override
        public RankingStat[] setRankingStat(MatchDetailDto matchDetailDto, Score blueScore, Score redScore) {
            Team[] redAllianceTeams = matchDetailDto.getRedTeams();
            Team[] blueAllianceTeams = matchDetailDto.getBlueTeams();

            // Calculate total teams participating in this match
            int totalTeamsInMatch = redAllianceTeams.length + blueAllianceTeams.length;
            RankingStat[] teamMatchResults = new RankingStat[totalTeamsInMatch];

            // Determine match outcome for alliance-based ranking
            int blueAllianceScore = blueScore.getTotalScore();
            int redAllianceScore = redScore.getTotalScore();

            boolean blueAllianceWins = blueAllianceScore > redAllianceScore;
            boolean redAllianceWins = redAllianceScore > blueAllianceScore;
            boolean matchIsTied = blueAllianceScore == redAllianceScore;

            // Distribute alliance score equally among team members
            int blueScorePerTeam = blueAllianceTeams.length > 0 ? blueAllianceScore / blueAllianceTeams.length : 0;
            int redScorePerTeam = redAllianceTeams.length > 0 ? redAllianceScore / redAllianceTeams.length : 0;

            // Distribute penalties equally among alliance members
            int bluePenaltiesPerTeam = blueAllianceTeams.length > 0 ? blueScore.getPenaltiesScore() / blueAllianceTeams.length : 0;
            int redPenaltiesPerTeam = redAllianceTeams.length > 0 ? redScore.getPenaltiesScore() / redAllianceTeams.length : 0;

            int resultIndex = 0;

            // Award ranking points to blue alliance teams
            for (Team team : blueAllianceTeams) {
                RankingStat teamResult = new RankingStat();
                teamResult.setTeamId(team.getTeamId());
                teamResult.setScore(blueScorePerTeam);
                teamResult.setPenalties(bluePenaltiesPerTeam);

                // Determine ranking points based on alliance performance
                if (blueAllianceWins) {
                    teamResult.setWin(true);
                    teamResult.setRankingPoints(WIN_POINTS);
                } else if (matchIsTied) {
                    teamResult.setWin(false);
                    teamResult.setRankingPoints(TIE_POINTS);
                } else {
                    teamResult.setWin(false);
                    teamResult.setRankingPoints(LOSS_POINTS);
                }

                teamMatchResults[resultIndex++] = teamResult;
            }

            // Award ranking points to red alliance teams
            for (Team team : redAllianceTeams) {
                RankingStat teamResult = new RankingStat();
                teamResult.setTeamId(team.getTeamId());
                teamResult.setScore(redScorePerTeam);
                teamResult.setPenalties(redPenaltiesPerTeam);

                // Determine ranking points based on alliance performance
                if (redAllianceWins) {
                    teamResult.setWin(true);
                    teamResult.setRankingPoints(WIN_POINTS);
                } else if (matchIsTied) {
                    teamResult.setWin(false);
                    teamResult.setRankingPoints(TIE_POINTS);
                } else {
                    teamResult.setWin(false);
                    teamResult.setRankingPoints(LOSS_POINTS);
                }

                teamMatchResults[resultIndex++] = teamResult;
            }

            return teamMatchResults;
        }
    }
}
