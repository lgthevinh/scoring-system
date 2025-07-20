package org.thingai.vrc.scoringsystem.model.score;

public class ScoreFactory {
    public static final int DEMO = 0;

    public static int seasonCode;

    public static Score createScore() {
        return switch (seasonCode) {
            case DEMO -> new DemoScore();
            default -> throw new IllegalArgumentException("Unsupported score type: " + seasonCode);
        };
    }

}
