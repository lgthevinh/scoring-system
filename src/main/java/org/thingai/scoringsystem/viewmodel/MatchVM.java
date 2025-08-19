package org.thingai.scoringsystem.viewmodel;

import org.thingai.base.dao.Dao;
import org.thingai.base.dao.DaoFactory;
import org.thingai.scoringsystem.entity.match.Match;
import org.thingai.scoringsystem.entity.match.MatchAlliance;
import org.thingai.scoringsystem.entity.score.Score;

public class MatchVM {
    private Match match;

    private MatchAlliance redAlliance;
    private MatchAlliance blueAlliance;

    private Score redScore;
    private Score blueScore;

    public static MatchVMGetter builder() {
        return new MatchVMGetter();
    }

    public static class MatchVMGetter {
        private final MatchVM matchVM;
        private String matchId;
        private boolean withTeamInfos = false;
        private boolean withScores = false;

        public MatchVMGetter() {
            matchVM = new MatchVM();
        }

        public MatchVMGetter withMatch(String id) {
            this.matchId = id;
            return this;
        }

        public MatchVMGetter withTeamInfos() {
            this.withTeamInfos = true;
            return this;
        }

        public MatchVMGetter withScores() {
            this.withScores = true;
            return this;
        }

        public MatchVM build() {
            Dao<Match, String> matchDao = DaoFactory.getDao(Match.class);

            matchVM.match = matchDao.read(matchId);
            if (matchVM.match == null) {
                throw new RuntimeException("Match not found with ID: " + matchId);
            }

            if (withTeamInfos) {
                Dao<MatchAlliance, String> allianceDao = DaoFactory.getDao(MatchAlliance.class);
                matchVM.redAlliance = allianceDao
                        .query("SELECT * FROM match_alliance WHERE match_id = '" + matchId + "' AND color = 0")
                        .get(0);
                matchVM.blueAlliance = allianceDao
                        .query("SELECT * FROM match_alliance WHERE match_id = '" + matchId + "' AND color = 1")
                        .get(0);
            }

            // Implement later due to score abstraction and inheritance
//            if (withScores) {
//                Dao<Score, String> scoreDao = DaoFactory.getDao(Score.class);
//                matchVM.redScore = scoreDao
//                        .query("SELECT * FROM score WHERE match_id = '" + matchId + "' AND alliance_color = 0")
//                        .get(0);
//                matchVM.blueScore = scoreDao
//                        .query("SELECT * FROM score WHERE match_id = '" + matchId + "' AND alliance_color = 1")
//                        .get(0);
//            }

            return matchVM;
        }
    }
}
