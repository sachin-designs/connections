package com.example.myapplication;

import java.io.Serializable;

public class Users implements Serializable {

    public String username;
    public String password;
    public String phNumber;
    public String email;
    public String place;
    public String school;
    public String work;
    public String Uid;

    Users(){

    }
    Users(String username, String password, String phNumber, String email, String place, String school, String work, String uid){
        this.username = username;
        this.password = password;
        this.phNumber = phNumber;
        this.email = email;
        this.place=place;
        this.school=school;
        this.work=work;
        this.Uid = uid;
    }
    Users(String username, String phNumber, String email, String place, String school, String work, String uid){
        this.username = username;
        this.phNumber = phNumber;
        this.email = email;
        this.place=place;
        this.school=school;
        this.work=work;
        this.Uid=uid;
    }
}
