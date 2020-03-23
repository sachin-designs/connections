package com.example.myapplication;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public  class Service_class extends Service {
    DatabaseReference databaseRef_msg;
    NotificationManagerCompat notificationManager;


    public Service_class(){

    }

    @SuppressLint("ServiceCast")
    public void getNotify(String msg){
//        Intent intent=new Intent(this,Message_activity_new.class);
//        intent.putExtra("Message",msg);
//        startActivity(intent);
        notificationManager= (NotificationManagerCompat) getSystemService(NOTIFICATION_SERVICE);
        Intent notificationIntent = new Intent(this, Message_activity_new.class);
        PendingIntent pendingIntent =
                PendingIntent.getActivity(this, 0, notificationIntent, 0);
        Notification notification = new NotificationCompat.Builder(getApplicationContext(), Notification_Class.CHANNEL_1_ID)
                .setSmallIcon(R.drawable.message)
                .setContentTitle("Message")
                .setContentText(msg)
                .setVibrate(new long[]{2000})
                .setContentIntent(pendingIntent)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setCategory(NotificationCompat.CATEGORY_MESSAGE)
                .setAutoCancel(true)
                .setOngoing(false)
                .build();
        notificationManager.notify(1,notification);
    }


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {

       throw new UnsupportedOperationException("Not yet implemented");
    }


    @Override
    public void onCreate() {
        super.onCreate();
        Toast.makeText(getApplicationContext(),"on service create",Toast.LENGTH_SHORT).show();
        Log.d("Service", "onCreate: ");
        databaseRef_msg= FirebaseDatabase.getInstance().getReference().child("Messages");

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Toast.makeText(getApplicationContext(),"on service start command",Toast.LENGTH_SHORT).show();
        Log.d("Service", "on start command: ");
        databaseRef_msg.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
            }
            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String msg_ref) {
                Log.d("DB", "key"+" "+dataSnapshot.getKey());
                String msg_obj ="got a message";
                Log.d("DB", "Data2"+" "+msg_obj);
                //getNotify(msg_obj);
            }
            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
            }
            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        }
        );
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Toast.makeText(getApplicationContext(),"on service destroy",Toast.LENGTH_SHORT).show();
        Log.d("Service", "on destroy: ");

    }
}
