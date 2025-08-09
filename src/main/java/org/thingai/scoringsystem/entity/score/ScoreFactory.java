package org.thingai.scoringsystem.entity.score;

public class ScoreFactory {
    public static String scoreType;

    public static Score createScore() {
        switch (scoreType) {
            case "SeasonDemo" -> new ScoreSeasonDemo();
            default -> throw new RuntimeException("Unknown score type: " + scoreType);
        }
        return null;
    }

}
