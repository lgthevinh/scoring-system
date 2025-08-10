package org.thingai.scoringsystem.entity.match;

import org.thingai.base.dao.annotations.DaoColumn;
import org.thingai.base.dao.annotations.DaoTable;

@DaoTable(name = "match_alliance")
public class MatchAlliance {
    @DaoColumn(name = "match_id")
    private String matchId;

    @DaoColumn(name = "alliance_color")
    private int allianceColor; // 0 for red, 1 for blue;

    @DaoColumn(name = "team1_id")
    private int team1Id;

    @DaoColumn(name = "team2_id")
    private int team2Id;

    @DaoColumn(name = "team3_id")
    private int team3Id;
}
