package org.thingai.app.scoringservice.handler.entityhandler;

import org.thingai.app.scoringservice.dto.MatchDetailDto;
import org.thingai.app.scoringservice.entity.ranking.IRankingStrategy;
import org.thingai.app.scoringservice.entity.ranking.RankingEntry;
import org.thingai.app.scoringservice.entity.score.Score;
import org.thingai.base.dao.Dao;
import org.thingai.base.dao.DaoFile;

public class RankingHandler {
    private static final String TAG = "RankingHandler";

    private Dao dao;
    private DaoFile daoFile;
    private IRankingStrategy rankingStrategy = new DefaultRankingStrategy();

    public RankingHandler(Dao dao, DaoFile daoFile) {
        this.dao = dao;
        this.daoFile = daoFile;
    }

    public void updateRankingEntry() {

    }

    public void getRankingStatus() {

    }

    public void setRankingStrategy(IRankingStrategy strategy) {
        this.rankingStrategy = strategy;
    }

    class DefaultRankingStrategy implements IRankingStrategy {
        @Override
        public RankingEntry[] sortRankingEntries(RankingEntry[] entries) {
            return entries;
        }

        @Override
        public RankingEntry[] updateRankingEntry(MatchDetailDto matchDetailDto, Score blueScore, Score redScore) {
            return new RankingEntry[0];
        }
    }
}
