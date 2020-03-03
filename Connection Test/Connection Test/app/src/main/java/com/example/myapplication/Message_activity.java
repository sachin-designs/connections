package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class Message_activity extends AppCompatActivity {
    TextView msg_id;
    TextView chat_box;
    TextInputEditText msg;
    Button send_btn;
    Users user;
    String message;
    private DatabaseReference databaseRef_msg,databaseRef;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.message_activity);
        Intent intent=getIntent();
        user= (Users) intent.getSerializableExtra("Object");
        Log.d("DB","Hey connector "+user.username);
        msg_id=(TextView)findViewById(R.id.message_id);
        chat_box=(TextView)findViewById(R.id.message_box);
        msg=(TextInputEditText) findViewById(R.id.edit_message);
        send_btn=(Button)findViewById(R.id.send);

        set_chat_fields(user);
        send_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(msg.getText().toString()!=null) {
                    fetch_msg(user);
                }
            }
        });


    }

    public void fetch_msg(final Users user) {

        message = msg.getText().toString();
        FirebaseUser currentuserinstance= FirebaseAuth.getInstance().getCurrentUser();
        final String Cuserid=currentuserinstance.getUid();

        Log.d("User","User"+Cuserid);
        databaseRef = FirebaseDatabase.getInstance().getReference().child("Register").child(Cuserid);
        // Read from the database
        databaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Users cuser = dataSnapshot.getValue(Users.class);
                Log.d("DB","Sucess"+cuser.username);
                db_feed_message(user,cuser,Cuserid);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d("DB","Failed");
            }
        });

    }

    private void db_feed_message(Users user, Users cuser, final String cuserid) {
        databaseRef=FirebaseDatabase.getInstance().getReference().child("Register");
        Query query=databaseRef.orderByChild("username").equalTo(user.username);
        Log.d("DB","Hey connector "+user.username);
        String userid;
        message = msg.getText().toString();
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    // dataSnapshot is the "issue" node with all children with id 0
                    for (DataSnapshot data : dataSnapshot.getChildren()) {
                        // do something with the individual "issues"
                        String userid=data.getKey();
                        Log.d("DB","Got connector uid"+userid);
                        Users user = data.getValue(Users.class);
                        Log.d("DB","Got my connectors"+user.username);
                        //connect_view.append(user.username+",");
                        databaseRef_msg = FirebaseDatabase.getInstance().getReference().child("Messages").child(cuserid);
                        //databaseRef_msg.setValue(Message);
                    }


                }else{
                    Log.d("DB","datasnapshot doest not exists");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d("DB","Cancelled");
            }
        });


    }

    public void set_chat_fields(Users user) {
        msg_id.setText(user.username);

    }

}
