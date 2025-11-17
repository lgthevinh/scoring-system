package org.thingai.app.scoringservice.define;

public enum EValueType {
    INTEGER(1),
    BOOLEAN(2);

    private final int value;

    EValueType(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}