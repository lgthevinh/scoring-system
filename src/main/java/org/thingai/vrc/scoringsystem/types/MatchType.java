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
    /**
     * Converts the MatchType to a byte representation.
     * This is useful for serialization or storage purposes.
     *
     * @return the ordinal value of the MatchType as a byte
     */
    public byte toByte() {
        return switch (this) {
            case QUALIFICATION -> 1;
            case ELIMINATION -> 2;
            case FINAL -> 3;
            case SEMI_FINAL -> 4;
            case QUARTER_FINAL -> 5;
            case LOWER_BRACKET -> 11;
            case UPPER_BRACKET -> 12;
        };
    }
}
