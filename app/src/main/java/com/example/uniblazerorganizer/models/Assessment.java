package com.example.uniblazerorganizer.models;

import java.io.Serializable;

public class Assessment implements Serializable {
    private int id;
    private int courseId;
    private String type;       // "Performance" or "Objective"
    private String title;
    private String startDate;
    private String endDate;

    // Constructors
    public Assessment() {
    }

    public Assessment(int courseId, String type, String title, String startDate, String endDate) {
        this.courseId = courseId;
        this.type = type;
        this.title = title;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    public Assessment(int id, int courseId, String type, String title, String startDate, String endDate) {
        this.id = id;
        this.courseId = courseId;
        this.type = type;
        this.title = title;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    // Getters & Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getCourseId() {
        return courseId;
    }

    public void setCourseId(int courseId) {
        this.courseId = courseId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }
}
