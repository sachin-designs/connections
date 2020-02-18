package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Timer;
import java.util.TimerTask;

public class FirstActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.first_layout);
        getSupportActionBar().hide();
        final SessionManagement sessionManagement = new SessionManagement(FirstActivity.this);
        Timer timer = new Timer();

        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                if(!sessionManagement.getName().isEmpty()){
                    Intent intent = new Intent(FirstActivity.this, ChatActivity.class);
                    intent.putExtra("name", sessionManagement.getName());
                    startActivity(intent);
                    finish();
                }else{
                    Intent intent = new Intent(FirstActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                }
            }
        },2000);

    }

}
