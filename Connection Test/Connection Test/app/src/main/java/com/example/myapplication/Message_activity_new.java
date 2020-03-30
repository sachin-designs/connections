package com.example.myapplication;

import android.app.Notification;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class Message_activity_new extends AppCompatActivity {
    TextView msg_id;
    RecyclerView chat_box;
    TextInputEditText msg;
    Button send_btn;
    Users user;
    String Cuser_name,connect_userid,Cuserid,msg_ref;
    private DatabaseReference databaseRef_msg,databaseRef,databaseRef_cuser;
    DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    ArrayList<Message_ref> lmessages = new ArrayList<>();
    private NotificationManagerCompat notificationManager;
    String connect_username;
    String message_ref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent=getIntent();
            setContentView(R.layout.message_activity);
            Log.d("DB", "inside new message activity");
            notificationManager = NotificationManagerCompat.from(this);

            user = (Users) intent.getSerializableExtra("Object");
            Log.d("DB", "Hey connector " + user.username);
            msg_id = (TextView) findViewById(R.id.message_id);
            chat_box = (RecyclerView) findViewById(R.id.message_box);
            msg = (TextInputEditText) findViewById(R.id.edit_message);
            send_btn = (Button) findViewById(R.id.send);
            set_chat_fields(user);
            fetch_user(user);
            check_message(Cuserid, connect_userid);
            send_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!msg.getText().toString().equals(null)) {
                        create_message_ref();
                        checkNotification();
                    }
                }
            });

    }

    public void checkNotification() {
        databaseRef_msg=FirebaseDatabase.getInstance().getReference().child("Messages");
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
        });

    }

    public void fetch_user(final Users user) {
        Log.d("DB","Inside fetch user");
        // message = msg.getText().toString();
        FirebaseUser currentuserinstance= FirebaseAuth.getInstance().getCurrentUser();
        Cuserid=currentuserinstance.getUid();
        connect_userid=user.Uid;
        Log.d("User","User"+Cuserid);
        databaseRef = FirebaseDatabase.getInstance().getReference().child("Register").child(Cuserid);
        // Read from the database
        databaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Users cuser = dataSnapshot.getValue(Users.class);
                Log.d("DB","Sucess"+cuser.username);
                Cuser_name=cuser.username;
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d("DB","Failed");
            }
        });
    }

    public void check_message(String cuserid, String connect_userid) {
        Log.d("DB","Inside check_message");
        databaseRef=FirebaseDatabase.getInstance().getReference().child("Msg_refs").child(cuserid);
        Query query=databaseRef.child(connect_userid).orderByValue();
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    String message_ref = dataSnapshot.getValue(String.class);
                    msg_ref= message_ref;
                    set_chat_box();
                } else {
                    databaseRef_msg=FirebaseDatabase.getInstance().getReference().child("Messages");
                    msg_ref=databaseRef_msg.push().getKey();
                    Log.d("DB","msg_ref"+msg_ref);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d("DB","Cancelled");
            }
        });
        //return msg_ref;
    }

    public void set_chat_box() {
        databaseRef_msg=FirebaseDatabase.getInstance().getReference().child("Messages").child(msg_ref);
        //Query query_msg=databaseRef_msg.orderByKey().equalTo(msg_ref);
        Log.d("DB","inside set chat box");
        Query query=databaseRef_msg.orderByKey();
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if (dataSnapshot.exists()) {
                    lmessages.clear();
                    // dataSnapshot is the "issue" node with all children with id 0
                    for (DataSnapshot data : dataSnapshot.getChildren()) {
                        // do something with the individual "issues"
                        Message_ref msg = data.getValue(Message_ref.class);
                        //connect_view.append(user.username+",");
                        lmessages.add(msg);
                    }
                    message_adapter(lmessages);
                }else{
                    Log.d("DB","datasnapshot doest not exists");
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d("DB","db op cancelled");
            }
        });

    }

    public void message_adapter(ArrayList<Message_ref> lmessages) {
        RecyclerView rv = (RecyclerView) findViewById(R.id.message_box);
         connect_username=user.username;
        MessageAdapter adapter = new MessageAdapter(lmessages,connect_username);
        // Attach the adapter to the recyclerview to populate items
        rv.setAdapter(adapter);
        // Set layout manager to position the items
        rv.setLayoutManager(new LinearLayoutManager(this));
        rv.smoothScrollToPosition(rv.getBottom());
        ((LinearLayoutManager)rv.getLayoutManager()).setStackFromEnd(true);
    }

    public void create_message_ref() {
        Log.d("DB","Inside create_message_ref");
        databaseRef_msg=FirebaseDatabase.getInstance().getReference().child("Messages").child(msg_ref);
        String message=msg.getText().toString();
        Date date = new Date();
        message_ref=dateFormat.format(date);
        String Message_user=Cuser_name;
        Message_ref mess_object= new Message_ref(message_ref,Message_user,message,false, connect_userid);
        databaseRef_msg.child(message_ref).setValue(mess_object);
        Log.d("DB", "message obj:"+msg_ref);
        databaseRef_msg = FirebaseDatabase.getInstance().getReference().child("Msg_refs");
        databaseRef_msg.child(Cuserid).child(connect_userid).setValue(msg_ref);
        //Message_ref message_cuser= new Message_ref(msg_ref,cuserid);
        Log.d("DB", "message obj:"+msg_ref);
        databaseRef_msg.child(connect_userid).child(Cuserid).setValue(msg_ref);
        msg.setText("");
        databaseRef_msg.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                set_chat_box();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            Log.d("DB","no message for this connectors");
            }
        });
    }

    public void set_chat_fields(Users user) {
        msg_id.setText(user.username);
    }


    public void getNotify(String msg){
            Notification notification = new NotificationCompat.Builder(getApplicationContext(), Notification_Class.CHANNEL_1_ID)
                    .setSmallIcon(R.drawable.message)
                    .setContentTitle("Message")
                    .setContentText(msg)
                    .setVibrate(new long[]{2000})
                    .setPriority(NotificationCompat.PRIORITY_HIGH)
                    .setCategory(NotificationCompat.CATEGORY_MESSAGE)
                    .build();
            notificationManager.notify(1, notification);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Toast.makeText(getApplicationContext(),"onDestroy",Toast.LENGTH_SHORT).show();
        Log.d("Activity", "onDestroy: ");
    }

    @Override
    public void onPause(){
        super.onPause();
       //Intent i = new Intent(this,Service_class.class);
       // startService(i);
        //stopService(i);
        Log.d("Activity", "onPause: ");
        Toast.makeText(getApplicationContext(),"onPause",Toast.LENGTH_SHORT).show();
//        databaseRef_msg=FirebaseDatabase.getInstance().getReference().child("Messages");
//        databaseRef_msg.addChildEventListener(new ChildEventListener() {
//            @Override
//            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
//            }
//            @Override
//            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String msg_ref) {
//                Log.d("DB", "key"+" "+dataSnapshot.getKey());
//                String msg_obj ="got a message";
//                Log.d("DB", "Data2"+" "+msg_obj);
//                getNotify(msg_obj);
//            }
//            @Override
//            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
//            }
//            @Override
//            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
//            }
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//            }
//        });
    }

    @Override
    public void onStop(){
        super.onStop();
        Toast.makeText(getApplicationContext(),"onStop",Toast.LENGTH_SHORT).show();
        Log.d("Activity", "onStop: ");
    }

}
