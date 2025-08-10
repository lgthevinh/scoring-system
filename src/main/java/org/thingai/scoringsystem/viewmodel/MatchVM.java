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

    public static class MatchVMBuilder {
        private final MatchVM matchVM;
        private String matchId;
        private boolean withTeamInfos = false;
        private boolean withScores = false;

        public MatchVMBuilder() {
            matchVM = new MatchVM();
        }

        public MatchVMBuilder withMatch(String id) {
            this.matchId = id;
            return this;
        }

        public MatchVMBuilder withTeamInfos() {
            this.withTeamInfos = true;
            return this;
        }

        public MatchVMBuilder withScores() {
            this.withScores = true;
            return this;
        }

        public MatchVM build() {
            Dao<Match, String> matchDao = DaoFactory.getDao();

            matchVM.match = matchDao.read(matchId);
            if (matchVM.match == null) {
                throw new RuntimeException("Match not found with ID: " + matchId);
            }

            if (withTeamInfos) {
                Dao<MatchAlliance, String> allianceDao = DaoFactory.getDao();
                matchVM.redAlliance = allianceDao
                        .query("SELECT * FROM match_alliance WHERE match_id = '" + matchId + "' AND color = 0")
                        .get(0);
                matchVM.blueAlliance = allianceDao
                        .query("SELECT * FROM match_alliance WHERE match_id = '" + matchId + "' AND color = 1")
                        .get(0);
            }

            return matchVM;
        }
    }
}
