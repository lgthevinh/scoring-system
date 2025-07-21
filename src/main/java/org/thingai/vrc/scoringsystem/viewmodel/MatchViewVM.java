package org.thingai.vrc.scoringsystem.viewmodel;

import org.thingai.vrc.scoringsystem.model.match.Alliance;
import org.thingai.vrc.scoringsystem.model.match.Match;
import org.thingai.vrc.scoringsystem.model.score.Score;

public class MatchViewVM {
    private Match match;

    private Alliance allianceRed;
    private Alliance allianceBlue;

    private Score scoreRed;
    private Score scoreBlue;

    public MatchViewVM(Match match, Alliance allianceRed, Alliance allianceBlue, Score scoreRed, Score scoreBlue) {
        this.match = match;
        this.allianceRed = allianceRed;
        this.allianceBlue = allianceBlue;
        this.scoreRed = scoreRed;
        this.scoreBlue = scoreBlue;
    }

    public Match getMatch() {
        return match;
    }

    public void setMatch(Match match) {
        this.match = match;
    }

    public Alliance getAllianceRed() {
        return allianceRed;
    }

    public void setAllianceRed(Alliance allianceRed) {
        this.allianceRed = allianceRed;
    }

    public Alliance getAllianceBlue() {
        return allianceBlue;
    }

    public void setAllianceBlue(Alliance allianceBlue) {
        this.allianceBlue = allianceBlue;
    }

    public Score getScoreRed() {
        return scoreRed;
    }

    public void setScoreRed(Score scoreRed) {
        this.scoreRed = scoreRed;
    }

    public Score getScoreBlue() {
        return scoreBlue;
    }

    public void setScoreBlue(Score scoreBlue) {
        this.scoreBlue = scoreBlue;
    }
}
