package org.thingai.app.scoringservice.entity.ranking;

import org.thingai.app.scoringservice.dto.MatchDetailDto;
import org.thingai.app.scoringservice.entity.score.Score;

public interface IRankingStrategy {
    RankingEntry[] sortRankingEntries(RankingEntry[] entries);
    RankingEntry[] updateRankingEntry(MatchDetailDto matchDetailDto, Score blueScore, Score redScore);
}
