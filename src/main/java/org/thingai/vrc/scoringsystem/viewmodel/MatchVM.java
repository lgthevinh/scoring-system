package org.thingai.vrc.scoringsystem.viewmodel;

import org.thingai.vrc.scoringsystem.model.match.Match;
import org.thingai.vrc.scoringsystem.model.match.MatchAlliance;
import org.thingai.vrc.scoringsystem.model.score.Score;

public class MatchVM {
    private Match match;

    private MatchAlliance redAlliance;
    private MatchAlliance blueAlliance;

    private Score redScore;
    private Score blueScore;
}
