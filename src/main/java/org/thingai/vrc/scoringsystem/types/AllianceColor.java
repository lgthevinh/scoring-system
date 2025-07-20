package org.thingai.vrc.scoringsystem.types;

public enum AllianceColor {
    RED("Red"),
    BLUE("Blue");

    private final String colorName;

    AllianceColor(String colorName) {
        this.colorName = colorName;
    }

    public String getColorName() {
        return colorName;
    }

    @Override
    public String toString() {
        return colorName;
    }
}
