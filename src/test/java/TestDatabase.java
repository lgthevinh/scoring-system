import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.thingai.vrc.scoringsystem.database.Database;

import org.thingai.vrc.scoringsystem.model.match.Match;
import org.thingai.vrc.scoringsystem.model.score.ScoreFactory;
import org.thingai.vrc.scoringsystem.service.IdGenerator;
import org.thingai.vrc.scoringsystem.types.MatchType;

public class TestDatabase {

    private static Match match;

    private static void setupMatch() {
        int matchNumber = 1; // Example match number
        MatchType matchType = MatchType.QUALIFICATION; // Example match type

        int matchId = IdGenerator.generateMatchId(matchType, matchNumber); // Example usage of IdGenerator with max match number

        // Initialize a Match object with default values or from a configuration
        match = new Match();
        match.setId(matchId);
        match.setMatchType(matchType);
        match.setMatchName(matchType.toString() + matchNumber);

        match.setMatchStartTime("2023-10-01T10:00:00Z"); // Example start time

        // Set other properties as needed
    }

    @BeforeAll
    public static void setup() {
        // Set the season code for the ScoreFactory
        ScoreFactory.seasonCode = ScoreFactory.DEMO;
        Database.DB_URL = "jdbc:sqlite:src/test/resources/test.db"; // Set the database URL for testing
    }

    @Test
    public void testInsert() {
        System.out.println("Test Insert");

        setupMatch(); // Ensure match is set up before inserting

        Database<Match> database = new Database<>(Match.class, Database.DB_URL);
        database.insert(match); // Insert the match into the database

        System.out.println("Match inserted with ID: " + match.getId());
    }

}
