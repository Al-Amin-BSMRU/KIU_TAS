package com.eid.codeprobro.kiu_tas;

public class _5_StudentModel {
    private String id;
    private String name;
    private boolean isPresent;

    public _5_StudentModel() {
    }

    public _5_StudentModel(String id, String name) {
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
