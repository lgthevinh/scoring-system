import com.thingai.core.AllianceColor;
import com.thingai.core.MatchType;
import com.thingai.model.match.Alliance;
import com.thingai.model.match.Match;
import com.thingai.model.score.Score;
import com.thingai.model.score.ScoreFactory;
import com.thingai.model.team.Team;
import org.junit.jupiter.api.Test;

import java.util.List;

public class TestCreationFlow {

    private Team team1 = new Team("FU0001", "FARC2025_1", "THPT ABC", "Ha Noi");
    private Team team2 = new Team("FU0002", "FARC2025_2", "THPT ABC", "Hai Phong");
    private Team team3 = new Team("FU0003", "FARC2025_3", "THPT ABC", "Can Tho");
    private Team team4 = new Team("FU0004", "FARC2025_4", "THPT ABC", "Quy Nhon");

    private final Match match = new Match();

    @Test
    public void testMatchCreationFlow() {
        match.setMatchId(1L);
        match.setMatchType(MatchType.QUALIFICATION);
        match.setMatchStartTime("2025-01-01T10:00:00Z");
        match.setMatchName("Q1-HAN-REG");

        Alliance allianceBlue = new Alliance();
        allianceBlue.setAllianceId("A1");
        allianceBlue.setColor(AllianceColor.BLUE);
        allianceBlue.setScore(ScoreFactory.createScore("FARC2025"));
        allianceBlue.addTeam(team1);
        allianceBlue.addTeam(team2);
        match.setAllianceBlue(allianceBlue);

        Alliance allianceRed = new Alliance();
        allianceRed.setAllianceId("A2");
        allianceRed.setColor(AllianceColor.RED);
        allianceRed.setScore(ScoreFactory.createScore("FARC2025"));
        allianceRed.addTeam(team3);
        allianceRed.addTeam(team4);
        match.setAllianceRed(allianceRed);
    }

    @Test
    public void testMatchDetails() {
    }
}
