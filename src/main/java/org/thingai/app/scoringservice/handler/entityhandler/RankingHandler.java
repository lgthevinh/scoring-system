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

public class RankingHandler {
    private static final String TAG = "RankingHandler";

    private Dao dao;
    private MatchHandler matchHandler;
    private IRankingStrategy rankingStrategy = new DefaultRankingStrategy();

    public RankingHandler(Dao dao, MatchHandler matchHandler) {
        this.matchHandler = matchHandler;
        this.dao = dao;
    }

    public void updateRankingEntry(MatchDetailDto matchDetailDto, Score blueScore, Score redScore) {
        RankingStat[] stats = rankingStrategy.setRankingStat(matchDetailDto, blueScore, redScore);
        for (RankingStat stat : stats) {
            // Fetch existing ranking entry
            RankingEntry entry = null;
            try{
                RankingEntry[] entries = dao.query(RankingEntry.class, new String[]{"teamId"}, new String[]{stat.getTeamId()});
                if (entries != null && entries.length > 0) {
                    entry = entries[0];
                }
            } catch (Exception e){
                e.printStackTrace();
                ILog.e(TAG, "Error fetching ranking entry: " + e.getMessage());
            }

            if (entry == null) {
                entry = new RankingEntry();
                entry.setTeamId(stat.getTeamId());
                entry.setRankingPoints(stat.getRankingPoints());
                entry.setTotalScore(stat.getScore());
                entry.setTotalPenalties(stat.getPenalties());
                entry.setMatchesPlayed(1);
                entry.setWins(stat.isWin() ? 1 : 0);
            } else {
                entry.setMatchesPlayed(entry.getMatchesPlayed() + 1);
                entry.setTotalScore(entry.getTotalScore() + stat.getScore());
                entry.setTotalPenalties(entry.getTotalPenalties() + stat.getPenalties());
                entry.setRankingPoints(entry.getRankingPoints() + stat.getRankingPoints());
                if (stat.isWin()) {
                    entry.setWins(entry.getWins() + 1);
                }
            }

            if (stat.getScore() > entry.getHighestScore()) {
                entry.setHighestScore(stat.getScore());
            }

            dao.insertOrUpdate(entry);
        }
    }

    public RankingEntry[] getRankingStatus(RequestCallback<RankingEntry[]> callback) {
        RankingEntry[] entries = dao.readAll(RankingEntry.class);
        RankingEntry[] sortedEntries = rankingStrategy.sortRankingEntries(entries);
        if (callback != null) {
            callback.onSuccess(sortedEntries, "All ranking entries fetched and sorted.");
        }
        return sortedEntries;
    }
    public void recalculateRankings(RequestCallback<Boolean> callback) {
        dao.deleteAll(RankingEntry.class);
        new Thread(() -> {
            try {
                matchHandler.listMatchDetails(MatchType.QUALIFICATION, true, new RequestCallback<MatchDetailDto[]>() {
                    @Override
                    public void onSuccess(MatchDetailDto[] result, String message) {
                        for (MatchDetailDto matchDetail : result) {
                            Score blueScore = matchDetail.getBlueScore();
                            Score redScore = matchDetail.getRedScore();

                            if (matchDetail.getMatch().getActualStartTime() == null) {
                                continue; // Skip matches that haven't started or have no scores
                            }

                            updateRankingEntry(matchDetail, blueScore, redScore);
                        }
                        ILog.i(TAG, "Recalculated rankings for all qualification matches.");
                    }

                    @Override
                    public void onFailure(int errorCode, String errorMessage) {
                        ILog.e(TAG, "Failed to fetch match details for recalculating rankings: " + errorMessage);
                    }
                });
                if (callback != null) {
                    callback.onSuccess(true, "Recalculated rankings successfully.");
                }
            } catch (Exception e) {
                e.printStackTrace();
                if (callback != null) {
                    callback.onFailure(-1, "Error during recalculating rankings: " + e.getMessage());
                }
                ILog.e(TAG, "Error during recalculating rankings: " + e.getMessage());
            }
        }).start();
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
}
