package org.thingai.app.scoringservice.entity.match;

import org.thingai.base.dao.annotations.DaoColumn;
import org.thingai.base.dao.annotations.DaoTable;

@DaoTable(name = "match_alliance")
public class MatchAlliance {
    @DaoColumn(name = "id", primaryKey = true)
    private String id;

    @DaoColumn(name = "matchId")
    private String matchId;

    // 0 for red, 1 for blue;
    @DaoColumn(name = "allianceColor")
    private int allianceColor;

    @DaoColumn(name = "scoreId")
    private String scoreId;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getMatchId() {
        return matchId;
    }

    public void setMatchId(String matchId) {
        this.matchId = matchId;
    }

    public int getAllianceColor() {
        return allianceColor;
    }

    public void setAllianceColor(int allianceColor) {
        this.allianceColor = allianceColor;
    }

    public String getScoreId() {
        return scoreId;
    }

    public void setScoreId(String scoreId) {
        this.scoreId = scoreId;
    }
}
