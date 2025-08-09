package org.thingai.vrc.scoringsystem.model.score;

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
