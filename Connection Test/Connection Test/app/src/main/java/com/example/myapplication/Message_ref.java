package com.example.myapplication;

public class Message_ref {
    public String message_ref;
    public String Message_user;
    public String Message;
    public Boolean status;
    public String CoUser;

    Message_ref(){
    }

    Message_ref(String message_ref,String Message_user,String message, Boolean status, String CoUser){
        this.message_ref=message_ref;
        this.Message_user=Message_user;
        this.Message=message;
        this.CoUser = CoUser;
        this.status = status;
    }
}