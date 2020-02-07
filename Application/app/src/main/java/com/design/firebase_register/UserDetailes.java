package com.design.firebase_register;

public class UserDetailes {

    private String Username;
    private String Email;
    private String Pass;
    private String Place;
    private String work;
    private String Uid;

    UserDetailes() {
    }

    UserDetailes(String username, String email, String pass, String place, String work, String Uid) {
        this.Username = username;
        this.Email = email;
        this.Pass = pass;
        this.Place = place;
        this.work = this.work;
        this.Uid=Uid;
    }

    public String getUsername() {
        return this.Username;
    }

    public String getEmail() {
        return this.Email;
    }

    public String getPass() {
        return this.Pass;
    }

    public String getUid() {
        return this.Uid;
    }

    public String getPlace() {
        return this.Place;
    }

    public void setUid(String uid) {
        this.Uid = uid;
    }

    public void setUsername(String username) {
        this.Username = username;
    }

    public void setEmail(String email) {
        this.Email = email;
    }

    public void setPass(String pass) {
        this.Pass = pass;
    }

    public void setPlace(String place) {
        this.Place = place;
    }

    public void setWork(String work) {
        this.work = work;
    }

    public String getWork() {
        return this.work;
    }
}
