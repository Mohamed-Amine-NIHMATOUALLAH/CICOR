package com.example.cicor.models;

import javafx.beans.property.*;

public class Category {
    private final IntegerProperty id;
    private final StringProperty name;
    private final StringProperty hardwareVersion;
    private final StringProperty softwareVersion;

    // Constructor
    public Category() {
        this.id = new SimpleIntegerProperty();
        this.name = new SimpleStringProperty();
        this.hardwareVersion = new SimpleStringProperty();
        this.softwareVersion = new SimpleStringProperty();
    }

    public Category(int id, String name, String hardwareVersion,
                    String softwareVersion) {
        this.id = new SimpleIntegerProperty(id);
        this.name = new SimpleStringProperty(name);
        this.hardwareVersion = new SimpleStringProperty(hardwareVersion);
        this.softwareVersion = new SimpleStringProperty(softwareVersion);

    }

    // Getters for properties
    public IntegerProperty idProperty() { return id; }
    public StringProperty nameProperty() { return name; }
    public StringProperty hardwareVersionProperty() { return hardwareVersion; }
    public StringProperty softwareVersionProperty() { return softwareVersion; }


    // Normal getters and setters
    public int getId() { return id.get(); }
    public void setId(int id) { this.id.set(id); }

    public String getName() { return name.get(); }
    public void setName(String name) { this.name.set(name); }

    public String getHardwareVersion() { return hardwareVersion.get(); }
    public void setHardwareVersion(String version) { this.hardwareVersion.set(version); }

    public String getSoftwareVersion() { return softwareVersion.get(); }
    public void setSoftwareVersion(String version) { this.softwareVersion.set(version); }



    public String toString() {
        return getName();}
}