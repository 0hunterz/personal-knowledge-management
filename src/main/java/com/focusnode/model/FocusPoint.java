package com.focusnode.model;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import java.util.Objects;

public class FocusPoint {
    private final StringProperty label;
    private final IntegerProperty value;

    public FocusPoint(String label, int value) {
        this.label = new SimpleStringProperty(label);
        this.value = new SimpleIntegerProperty(value);
    }

    public String getLabel() { return label.get(); }
    public void setLabel(String label) { this.label.set(label); }
    public StringProperty labelProperty() { return label; }

    public int getValue() { return value.get(); }
    public void setValue(int value) { this.value.set(value); }
    public IntegerProperty valueProperty() { return value; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FocusPoint that = (FocusPoint) o;
        return getValue() == that.getValue() && Objects.equals(getLabel(), that.getLabel());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getLabel(), getValue());
    }
}
