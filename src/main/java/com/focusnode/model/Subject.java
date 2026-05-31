package com.focusnode.model;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import java.time.LocalDateTime;

public class Subject {
    private final IntegerProperty id;
    private final IntegerProperty userId;
    private final StringProperty name;
    private final StringProperty colorHex;
    private final LocalDateTime createdAt;

    public Subject(int id, int userId, String name, String colorHex, LocalDateTime createdAt) {
        this.id = new SimpleIntegerProperty(id);
        this.userId = new SimpleIntegerProperty(userId);
        this.name = new SimpleStringProperty(name);
        this.colorHex = new SimpleStringProperty(colorHex);
        this.createdAt = createdAt;
    }

    public int getId() { return id.get(); }
    public void setId(int value) { id.set(value); }
    public IntegerProperty idProperty() { return id; }

    public int getUserId() { return userId.get(); }
    public void setUserId(int value) { userId.set(value); }
    public IntegerProperty userIdProperty() { return userId; }

    public String getName() { return name.get(); }
    public void setName(String value) { name.set(value); }
    public StringProperty nameProperty() { return name; }

    public String getColorHex() { return colorHex.get(); }
    public void setColorHex(String value) { colorHex.set(value); }
    public StringProperty colorHexProperty() { return colorHex; }

    public LocalDateTime getCreatedAt() { return createdAt; }
}
