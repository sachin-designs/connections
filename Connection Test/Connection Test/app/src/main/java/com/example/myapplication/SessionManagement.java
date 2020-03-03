package com.example.myapplication;

import android.content.SharedPreferences;
import android.content.Context;

import com.google.android.gms.auth.api.signin.GoogleSignInAccount;

public class SessionManagement {
    SharedPreferences sharedPreferences;
    Context context;
    String name,gname,gemail;
    GoogleSignInAccount Gacct;

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

    public void setGaccnt(GoogleSignInAccount Gacct) {
        this.Gacct = Gacct;
        this.gname=Gacct.getDisplayName();
        this.gemail=Gacct.getEmail();
        sharedPreferences.edit().putString("Gacct_name", gname).commit();
        sharedPreferences.edit().putString("Gacct_mail", gemail).commit();
    }

    public String getGaccnt_name() {
        gname = sharedPreferences.getString("Gacct_name","");

        return gname;
    }
    public String getGaccnt_gmail() {
        gemail = sharedPreferences.getString("Gacct_mail","");
        return gemail;
    }

    SessionManagement(Context context){
        this.context = context;
        sharedPreferences = context.getSharedPreferences("userinfo", Context.MODE_PRIVATE);
    }
}