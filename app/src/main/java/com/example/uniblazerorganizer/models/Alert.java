package com.example.uniblazerorganizer.models;

public class Alert {
    private int id;
    private String objectType;
    private int objectId;
    private boolean startToggle;
    private boolean endToggle;

    public Alert(String objectType, int objectId) {
        this.objectType = objectType;
        this.objectId = objectId;
    }

    public Alert() {
    }

    // Getters and setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getObjectType() {
        return objectType;
    }

    public void setObjectType(String objectType) {
        this.objectType = objectType;
    }

    public int getObjectId() {
        return objectId;
    }

    public void setObjectId(int objectId) {
        this.objectId = objectId;
    }

    public boolean isStartToggle() {
        return startToggle;
    }

    public void setStartToggle(boolean startToggle) {
        this.startToggle = startToggle;
    }

    public boolean isEndToggle() {
        return endToggle;
    }

    public void setEndToggle(boolean endToggle) {
        this.endToggle = endToggle;
    }
}
