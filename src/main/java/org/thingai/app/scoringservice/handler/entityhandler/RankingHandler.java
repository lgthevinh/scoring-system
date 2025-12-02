package org.thingai.app.scoringservice.handler.entityhandler;

import org.thingai.app.scoringservice.callback.RequestCallback;
import org.thingai.app.scoringservice.dto.MatchDetailDto;
import org.thingai.app.scoringservice.entity.ranking.IRankingStrategy;
import org.thingai.app.scoringservice.entity.ranking.RankingEntry;
import org.thingai.app.scoringservice.entity.ranking.RankingStat;
import org.thingai.app.scoringservice.entity.score.Score;
import org.thingai.app.scoringservice.entity.team.Team;
import org.thingai.base.dao.Dao;
import org.thingai.base.log.ILog;

public class RankingHandler {
    private static final String TAG = "RankingHandler";

    private Dao dao;
    private IRankingStrategy rankingStrategy = new DefaultRankingStrategy();

    public RankingHandler(Dao dao) {
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

    public void setRankingStrategy(IRankingStrategy strategy) {
        this.rankingStrategy = strategy;
    }

    public void setDao(Dao dao) {
        this.dao = dao;
    }

    static class DefaultRankingStrategy implements IRankingStrategy {
        @Override
        public RankingEntry[] sortRankingEntries(RankingEntry[] entries) {
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
                stat.setRankingPoints(isWin ? 3 : 0);
                stats[index++] = stat;
            }

            for (Team team : redTeams) {
                RankingStat stat = new RankingStat();
                stat.setTeamId(team.getTeamId());
                stat.setScore(redScore.getTotalScore());
                stat.setPenalties(redScore.getPenaltiesScore());
                boolean isWin = redScore.getTotalScore() > blueScore.getTotalScore();
                stat.setWin(isWin);
                stat.setRankingPoints(isWin ? 3 : 0);
                stats[index++] = stat;
            }

            return stats;
        }
    }
}
