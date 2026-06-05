package com.eid.codeprobro.kiu_tas;

public class StudentModel {
    private String id;
    private String name;
    private boolean isPresent;

    public StudentModel() {
    }

    public StudentModel(String id, String name) {
        this.id = id;
        this.name = name;
        this.isPresent = false;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isPresent() {
        return isPresent;
    }

    public void setPresent(boolean present) {
        isPresent = present;
    }
}
