import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.thingai.vrc.scoringsystem.database.Database;
import org.thingai.vrc.scoringsystem.model.score.ScoreFactory;
import org.thingai.vrc.scoringsystem.service.MatchGenerator;
import org.thingai.vrc.scoringsystem.types.MatchType;

public class TestMatchGenerator {

    @BeforeAll
    public static void setup() {
        // Set the season code for the ScoreFactory
        ScoreFactory.seasonCode = ScoreFactory.DEMO;
        Database.DB_URL = "jdbc:sqlite:src/test/resources/test.db"; // Set the database URL for testing
    }

    @Test
    public void testGenerateMatch() {
        // This test will generate a match and print its details
        // The actual implementation of match generation is not provided in the original code
        // Assuming a MatchGenerator class exists with a method to generate matches

        MatchGenerator.generateTemplateMatches(MatchType.QUALIFICATION, 70, 7, 0);
    }

}
