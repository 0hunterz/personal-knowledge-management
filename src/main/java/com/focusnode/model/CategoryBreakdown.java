package com.focusnode.model;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import java.util.Objects;

public class CategoryBreakdown {
    private final StringProperty category;
    private final IntegerProperty value;
    private final StringProperty colorHex;

    public CategoryBreakdown(String category, int value, String colorHex) {
        this.category = new SimpleStringProperty(category);
        this.value = new SimpleIntegerProperty(value);
        this.colorHex = new SimpleStringProperty(colorHex);
    }

    public String getCategory() { return category.get(); }
    public void setCategory(String category) { this.category.set(category); }
    public StringProperty categoryProperty() { return category; }

    public int getValue() { return value.get(); }
    public void setValue(int value) { this.value.set(value); }
    public IntegerProperty valueProperty() { return value; }

    public String getColorHex() { return colorHex.get(); }
    public void setColorHex(String colorHex) { this.colorHex.set(colorHex); }
    public StringProperty colorHexProperty() { return colorHex; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CategoryBreakdown that = (CategoryBreakdown) o;
        return getValue() == that.getValue() && Objects.equals(getCategory(), that.getCategory()) && Objects.equals(getColorHex(), that.getColorHex());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getCategory(), getValue(), getColorHex());
    }
}
