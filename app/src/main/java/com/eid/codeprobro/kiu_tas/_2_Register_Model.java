package com.eid.codeprobro.kiu_tas;

public class _2_Register_Model {
    private String namid,puid, id,name,university,dept;
    private String email;
    private String password;
//    private String session;

    public String getNamid() {
        return namid;
    }

    public void setNamid(String namid) {
        this.namid = namid;
    }

    public String getPuid() {
        return puid;
    }

    public void setPuid(String puid) {
        this.puid = puid;
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

    public String getUniversity() {
        return university;
    }

    public void setUniversity(String university) {
        this.university = university;
    }

    public String getDept() {
        return dept;
    }

    public void setDept(String dept) {
        this.dept = dept;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public _2_Register_Model(String namid, String puid, String id, String name, String university, String dept, String email, String password) {
        this.namid = namid;
        this.puid = puid;
        this.id = id;
        this.name = name;
        this.university = university;
        this.dept = dept;
        this.email = email;
        this.password = password;
    }

    public _2_Register_Model() {
    }
}