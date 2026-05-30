package com.focusnode.model;

import java.util.Objects;

public class TagUsageItem {
    private final String name;
    private final int value;
    private final String colorHex;

    public TagUsageItem(String name, int value, String colorHex) {
        this.name = name;
        this.value = value;
        this.colorHex = colorHex;
    }

    public String getName() {
        return name;
    }

    public int getValue() {
        return value;
    }

    public String getColorHex() {
        return colorHex;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TagUsageItem that = (TagUsageItem) o;
        return value == that.value && Objects.equals(name, that.name) && Objects.equals(colorHex, that.colorHex);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, value, colorHex);
    }
}
