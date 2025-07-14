package com.thingai.model.score;

public class ScoreFactory {

    public static Score createScore(String seasonCode) {
        return switch (seasonCode) {
            case "FARC2025" -> new SeasonDemoScore();
            default -> throw new IllegalArgumentException("Unsupported season code: " + seasonCode);
        };
    }
}
