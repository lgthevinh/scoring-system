package org.thingai.vrc.scoringsystem.viewmodel;

import org.thingai.vrc.scoringsystem.entity.match.Match;
import org.thingai.vrc.scoringsystem.entity.match.MatchAlliance;
import org.thingai.vrc.scoringsystem.entity.score.Score;

public class MatchVM {
    private Match match;

    private MatchAlliance redAlliance;
    private MatchAlliance blueAlliance;

    private Score redScore;
    private Score blueScore;
}
