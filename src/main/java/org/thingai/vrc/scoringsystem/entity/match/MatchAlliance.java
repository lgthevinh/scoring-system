package org.thingai.vrc.scoringsystem.entity.match;

import org.thingai.base.dao.annotations.DaoTable;

@DaoTable(name = "match_alliance")
public class MatchAlliance {
    private String matchId;
    private int allianceId; // 0 for red, 1 for blue;

    private int redTeam1Id;
    private int redTeam2Id;
    private int redTeam3Id;
}
