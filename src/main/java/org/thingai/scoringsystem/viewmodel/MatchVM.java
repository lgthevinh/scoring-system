package org.thingai.scoringsystem.viewmodel;

import org.thingai.scoringsystem.entity.match.Match;
import org.thingai.scoringsystem.entity.match.MatchAlliance;
import org.thingai.scoringsystem.entity.score.Score;

public class MatchVM {
    private Match match;

    private MatchAlliance redAlliance;
    private MatchAlliance blueAlliance;

    private Score redScore;
    private Score blueScore;
}
