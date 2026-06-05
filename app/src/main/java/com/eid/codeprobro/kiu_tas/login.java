package com.eid.codeprobro.kiu_tas;

import com.google.firebase.database.PropertyName;

public class login {
    private String namid, puid, id;
    private String email;
    private String password;
//    private String session;

    public login() {
    }

    public login(String name_id, String punch_id, String email, String password, String id) {
        this.namid = name_id;
        this.puid = punch_id;
        this.id = id;
        this.email = email;
        this.password = password;
//        this.session = session;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @PropertyName("name")
    public String getnamid() {
        return namid;
    }

    @PropertyName("name")
    public void setnamid(String name_id) {
        this.namid = name_id;
    }

    @PropertyName("punch_id")
    public String getPunch_id() {
        return puid;
    }

    @PropertyName("punch_id")
    public void setPunch_id(String punch_id) {
        this.puid = punch_id;
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

//    public String getSession() {
//        return session;
//    }
//
//    public void setSession(String session) {
//        this.session = session;
//    }
}