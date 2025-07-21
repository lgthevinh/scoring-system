import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.thingai.vrc.scoringsystem.annotations.DaoField;
import org.thingai.vrc.scoringsystem.database.Database;
import org.thingai.vrc.scoringsystem.model.score.Score;
import org.thingai.vrc.scoringsystem.model.score.ScoreFactory;
import org.thingai.vrc.scoringsystem.service.MatchGenerator;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TestMatchGenerator {

    private static final MatchGenerator matchGenerator = new MatchGenerator();

    private Field[] getAllFields(Class<?> clazz) {
        List<Field> fields = new ArrayList<>();
        while (clazz != null) {
            fields.addAll(Arrays.asList(clazz.getDeclaredFields()));
            clazz = clazz.getSuperclass();
        }
        return fields.toArray(new Field[0]);
    }

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

        matchGenerator.generateMatches();
    }

    @Test
    public void test() {
        Score score = ScoreFactory.createScore();

        Field[] fields = getAllFields(score.getClass());

        System.out.println("Fields in " + score.getClass().getSimpleName() + ":");
        for (Field field : fields) {
            if (field.isAnnotationPresent(DaoField.class)) {
                DaoField daoField = field.getAnnotation(DaoField.class);
                System.out.println("Field: " + field.getName() + ", DaoField name: " + daoField.name());
            } else {
                System.out.println("Field: " + field.getName() + " (no DaoField annotation)");
            }
        }
    }

}
