package org.thingai.vrc.scoringsystem.types;

public enum MatchType {
    QUALIFICATION("Q"),
    ELIMINATION("E"),
    FINAL("F"),
    SEMI_FINAL("SF"),
    QUARTER_FINAL("QF"),
    LOWER_BRACKET("L"),
    UPPER_BRACKET("U");

    private final String type;

    MatchType(String type) {
        this.type = type;
    }

    public String getPrefix() {
        return type;
    }

    @Override
    public String toString() {
        return type;
    }
}
