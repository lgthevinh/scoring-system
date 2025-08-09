import org.junit.jupiter.api.Test;
import org.thingai.base.dao.DaoSqlite;
import org.thingai.scoringsystem.entity.match.Match;
import org.thingai.scoringsystem.entity.score.ScoreSeasonDemo;
import org.thingai.scoringsystem.entity.team.Team;

public class TestDao {

    @Test
    public void testCreateTables() {
        DaoSqlite.createTables(new Class[] {
                Match.class,
                ScoreSeasonDemo.class,
                Team.class
        });
    }
}
