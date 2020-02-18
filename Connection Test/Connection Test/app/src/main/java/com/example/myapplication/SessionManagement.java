package com.example.myapplication;

import android.content.SharedPreferences;
import android.content.Context;
public class SessionManagement {
    SharedPreferences sharedPreferences;
    Context context;
    String name;

    public void remove(){
        sharedPreferences.edit().clear().commit();
    }

    public String getName() {
        name = sharedPreferences.getString("userdata","");
        return name;
    }

    public void setName(String name) {
        this.name = name;
        sharedPreferences.edit().putString("userdata", name).commit();
    }

    SessionManagement(Context context){
        this.context = context;
        sharedPreferences = context.getSharedPreferences("userinfo", Context.MODE_PRIVATE);
    }
}