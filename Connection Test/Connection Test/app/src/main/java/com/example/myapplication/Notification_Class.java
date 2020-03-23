package com.example.myapplication;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;

public class Notification_Class extends Application {


    public static final String CHANNEL_1_ID = "Channel 1 ";
    public static final String  CHANNEL_2_ID = "Channel 2";

    @Override
    public void onCreate() {
        super.onCreate();

        createNotificationChannel();
    }

    public void createNotificationChannel(){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            NotificationChannel channel1 = new NotificationChannel(
                    CHANNEL_1_ID, "channel 1", NotificationManager.IMPORTANCE_HIGH);
            channel1.setDescription("From Channel 1");
            channel1.setShowBadge(false);
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel1);

        }
    }
}