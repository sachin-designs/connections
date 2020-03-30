package com.example.myapplication;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public  class Service_class extends Service {
    DatabaseReference databaseRef_msg;
    NotificationManagerCompat notificationManager;
    String msgKey;
    String timeKey;
    String cuserid;

    public Service_class(){

    }

    //@SuppressLint("ServiceCast")
    public void getNotify(String msg, String user){
//        Intent intent=new Intent(this,Message_activity_new.class);
//        intent.putExtra("Message",msg);
//        startActivity(intent);
       // notificationManager= (NotificationManagerCompat) getSystemService(NOTIFICATION_SERVICE);
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        Intent notificationIntent = new Intent(this, ChatActivity.class);
        PendingIntent pendingIntent =
                PendingIntent.getActivity(this, 0, notificationIntent, 0);
        Notification notification = new NotificationCompat.Builder(getApplicationContext(), Notification_Class.CHANNEL_1_ID)
                .setSmallIcon(R.drawable.message)
                .setContentTitle(user)
                .setContentText(msg)
                .setVibrate(new long[]{2000})
                .setContentIntent(pendingIntent)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setCategory(NotificationCompat.CATEGORY_MESSAGE)
                .setAutoCancel(true)
                .setOngoing(false)
                .build();
       notificationManager.notify(0,notification);

//        startForeground(1, notification);
//        Timer timer = new Timer();
//        timer.schedule(new TimerTask() {
//            @Override
//            public void run() {
//        stopForeground(1);
//            }
//        },3000);

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
                Log.d("DB", "key"+" "+dataSnapshot.getKey());
                msgKey = dataSnapshot.getKey();
                Log.d("Servie", msgKey);
                databaseRef_msg.child(msgKey).addChildEventListener(new ChildEventListener() {

                    @Override
                    public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                        Log.d("DB", "childchanged"+dataSnapshot.getValue());
                        timeKey = dataSnapshot.getKey();
                        String msgKey =dataSnapshot.getRef().getParent().getKey();
                        Log.d("Servie", timeKey);
                        Message_ref msg_obj=dataSnapshot.getValue(Message_ref.class);
                        Log.d("DB", "childchanged"+msg_obj.Message);
                        FirebaseUser currentuserinstance= FirebaseAuth.getInstance().getCurrentUser();
                        cuserid=currentuserinstance.getUid();
                        if(!msg_obj.status && msg_obj.CoUser.equals(cuserid)){
                        getNotify(msg_obj.Message, msg_obj.Message_user);
                        databaseRef_msg.child(msgKey).child(timeKey).child("status").setValue(true);
                        }
                    }
                    @Override
                    public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                        Log.d("DB", "childchanged"+dataSnapshot.getValue());
//                        String msg_obj="Got a message";
//                        getNotify(msg_obj);
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
                });
            }
            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String msg_ref) {
                Log.d("DB", "key"+" "+dataSnapshot.getKey());
                msgKey = dataSnapshot.getKey();
                Log.d("Servie", msgKey);
                databaseRef_msg.child(msgKey).addChildEventListener(new ChildEventListener() {

                    @Override
                    public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                        Log.d("DB", "childchanged"+dataSnapshot.getValue());
                        timeKey = dataSnapshot.getKey();
                        String msgKey =dataSnapshot.getRef().getParent().getKey();
                        Log.d("Servie", timeKey);
                        Message_ref msg_obj=dataSnapshot.getValue(Message_ref.class);
                        Log.d("DB", "childchanged"+msg_obj.Message);
                        FirebaseUser currentuserinstance= FirebaseAuth.getInstance().getCurrentUser();
                        cuserid=currentuserinstance.getUid();
                        if(!msg_obj.status && msg_obj.CoUser.equals(cuserid)){
                            getNotify(msg_obj.Message, msg_obj.Message_user);
                            databaseRef_msg.child(msgKey).child(timeKey).child("status").setValue(true);
                        }
                    }
                    @Override
                    public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                        Log.d("DB", "childchanged"+dataSnapshot.getValue());
//                        String msg_obj="Got a message";
//                        getNotify(msg_obj);
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
                });
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
        return Service.START_STICKY;
    }



    @Override
    public void onDestroy() {
        super.onDestroy();
        Toast.makeText(getApplicationContext(),"on destroy command",Toast.LENGTH_SHORT).show();
        Log.d("Service", "on destroy: ");
        databaseRef_msg.addChildEventListener(new ChildEventListener() {
                                                  @Override
                                                  public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                                                      Log.d("DB", "key"+" "+dataSnapshot.getKey());
                                                      msgKey = dataSnapshot.getKey();
                                                      Log.d("Servie", msgKey);
                                                      databaseRef_msg.child(msgKey).addChildEventListener(new ChildEventListener() {

                                                          @Override
                                                          public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                                                              Log.d("DB", "childchanged"+dataSnapshot.getValue());
                                                              timeKey = dataSnapshot.getKey();
                                                              String msgKey =dataSnapshot.getRef().getParent().getKey();
                                                              Log.d("Servie", timeKey);
                                                              Message_ref msg_obj=dataSnapshot.getValue(Message_ref.class);
                                                              Log.d("DB", "childchanged"+msg_obj.Message);
                                                              FirebaseUser currentuserinstance= FirebaseAuth.getInstance().getCurrentUser();
                                                              cuserid=currentuserinstance.getUid();
                                                              if(!msg_obj.status && msg_obj.CoUser.equals(cuserid)){
                                                                  getNotify(msg_obj.Message, msg_obj.Message_user);
                                                                  databaseRef_msg.child(msgKey).child(timeKey).child("status").setValue(true);
                                                              }
                                                          }
                                                          @Override
                                                          public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                                                              Log.d("DB", "childchanged"+dataSnapshot.getValue());

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
                                                      });
                                                  }
                                                  @Override
                                                  public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String msg_ref) {
                                                      Log.d("DB", "key"+" "+dataSnapshot.getKey());
                                                      msgKey = dataSnapshot.getKey();
                                                      Log.d("Servie", msgKey);
                                                      databaseRef_msg.child(msgKey).addChildEventListener(new ChildEventListener() {

                                                          @Override
                                                          public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                                                              Log.d("DB", "childchanged"+dataSnapshot.getValue());
                                                              timeKey = dataSnapshot.getKey();
                                                              String msgKey =dataSnapshot.getRef().getParent().getKey();
                                                              Log.d("Servie", timeKey);
                                                              Message_ref msg_obj=dataSnapshot.getValue(Message_ref.class);
                                                              Log.d("DB", "childchanged"+msg_obj.Message);
                                                              FirebaseUser currentuserinstance= FirebaseAuth.getInstance().getCurrentUser();
                                                              cuserid=currentuserinstance.getUid();
                                                              if(!msg_obj.status && msg_obj.CoUser.equals(cuserid)){
                                                                  getNotify(msg_obj.Message, msg_obj.Message_user);
                                                                  databaseRef_msg.child(msgKey).child(timeKey).child("status").setValue(true);
                                                              }
                                                          }
                                                          @Override
                                                          public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                                                              Log.d("DB", "childchanged"+dataSnapshot.getValue());

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
                                                      });
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

    }
}
