package com.example.myapplication;

public class Users {

    public String username;
    public String password;
    public String phNumber;
    public String email;
    public String place;
    public String school;
    public String work;

    Users(){

    }
    Users(String username, String password, String phNumber, String email, String place, String school, String work){
        this.username = username;
        this.password = password;
        this.phNumber = phNumber;
        this.email = email;
        this.place=place;
        this.school=school;
        this.work=work;
    }

}
