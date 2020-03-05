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

import java.util.HashMap;

public class Message_activity extends AppCompatActivity {
    TextView msg_id;
    TextView chat_box;
    TextInputEditText msg;
    Button send_btn;
    Users user;
    String Cuser_name,connect_userid,Cuserid,msg_ref;
    private DatabaseReference databaseRef_msg,databaseRef,databaseRef_cuser;

    StringBuilder message = new StringBuilder();


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
        fetch_user(user);
        send_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(msg.getText().toString()!=null) {
                    create_message_ref(Cuserid,connect_userid,msg_ref);
                }
            }
        });

    }
//    public void set_msg_box() {
//        if(msg_ref!=null){
//            databaseRef=FirebaseDatabase.getInstance().getReference().child("Messages");
//            Query query=databaseRef.orderByKey().equalTo(msg_ref);
//            query.addValueEventListener(new ValueEventListener() {
//                @Override
//                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                    Log.d("DB","Heg got the msg."+dataSnapshot);
//                    if(dataSnapshot.exists()){
//                        String msg= (String) dataSnapshot.getValue();
//                        chat_box.append(msg);
//                    }
//                }
//
//                @Override
//                public void onCancelled(@NonNull DatabaseError databaseError) {
//                    Log.d("DB","Failed");
//                }
//            });
//        }
//    }
    public void fetch_user(final Users user) {
        Log.d("DB","Inside fetch user");
       // message = msg.getText().toString();
        FirebaseUser currentuserinstance= FirebaseAuth.getInstance().getCurrentUser();
        Cuserid=currentuserinstance.getUid();
        db_get_ids(user,Cuserid);
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

    private void db_get_ids(Users user, final String cuserid) {
        Log.d("DB","Inside db_get_ids");
        databaseRef=FirebaseDatabase.getInstance().getReference().child("Register");
        Query query=databaseRef.orderByChild("username").equalTo(user.username);
        Log.d("DB","Hey connector "+user.username);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    // dataSnapshot is the "issue" node with all children with id 0
                    for (DataSnapshot data : dataSnapshot.getChildren()) {
                        // do something with the individual "issues"
                        connect_userid=data.getKey();
                        Log.d("DB","Got connector uid"+connect_userid);
                        final Users user = data.getValue(Users.class);
                        Log.d("DB","Got my connectors"+user.username);
                        //connect_view.append(user.username+",");
                            check_message(cuserid,connect_userid);
                        //databaseRef_msg.setValue(Message);
                    }
                }else{
                    Log.d("DB","datasnapshot doest not exists for connector.");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d("DB","Cancelled");
            }
        });
    }

    public void check_message(String cuserid, String connect_userid) {
        Log.d("DB","Inside check_message");
        databaseRef=FirebaseDatabase.getInstance().getReference().child("Msg_refs");
        Query query=databaseRef.orderByChild(cuserid);
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
        databaseRef_msg=FirebaseDatabase.getInstance().getReference().child("Messages");
        Query query_msg=databaseRef_msg.orderByKey().equalTo(msg_ref);
        query_msg.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    HashMap msg = (HashMap) dataSnapshot.getValue();
                    Log.d("DB","Hey got msg."+msg.get(msg_ref));
                    chat_box.setText(" ");
                    message.append(msg.get(msg_ref)+",");
                    chat_box.append(msg.get(msg_ref)+",");
                } else {
                    chat_box.setText(" ");
                    Log.d("DB","Hey no msgs yet.");
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d("DB","Cancelled");
            }
        });
    }

    public void create_message_ref(String cuserid, String connect_userid, String msg_ref_cpy) {
        Log.d("DB","Inside create_message_ref");
        message.append(Cuser_name+":"+msg.getText().toString());
        Log.d("DB","message"+message);
        databaseRef_msg=FirebaseDatabase.getInstance().getReference().child("Messages");
        databaseRef_msg.child(msg_ref_cpy).setValue(message.toString());
        //Message_ref message_connector= new Message_ref(msg_ref_cpy,connect_userid);
        Log.d("DB", "message obj:"+msg_ref_cpy);
        databaseRef_msg = FirebaseDatabase.getInstance().getReference().child("Msg_refs");
        databaseRef_msg.child(cuserid).child(connect_userid).setValue(msg_ref_cpy);
        //Message_ref message_cuser= new Message_ref(msg_ref,cuserid);
        Log.d("DB", "message obj:"+msg_ref_cpy);
        databaseRef_msg.child(connect_userid).child(cuserid).setValue(msg_ref_cpy);
        msg.clearComposingText();
        fetch_user(user);
    }

    public void set_chat_fields(Users user) {
        msg_id.setText(user.username);
    }

}
