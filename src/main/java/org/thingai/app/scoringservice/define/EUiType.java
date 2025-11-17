package org.thingai.app.scoringservice.define;

public enum EUiType {
    COUNTER(1),
    TOGGLE(2);

    private final int value;

    EUiType(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}