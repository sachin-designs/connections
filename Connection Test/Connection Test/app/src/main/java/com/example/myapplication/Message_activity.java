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
    String message,Cuser_name,connect_userid,Cuserid,msg_ref;
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
        set_msg_box();
        send_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(msg.getText().toString()!=null) {
                    fetch_msg(user);
                }
            }
        });


    }

    public void set_msg_box() {
        if(msg_ref!=null){
            databaseRef=FirebaseDatabase.getInstance().getReference().child("Messages");
            Query query=databaseRef.orderByKey().equalTo(msg_ref);
            query.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    Log.d("DB","Heg got the msg."+dataSnapshot);
                    if(dataSnapshot.exists()){
                        String msg= (String) dataSnapshot.getValue();
                        chat_box.append(msg);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Log.d("DB","Failed");
                }
            });
        }
    }

    public void fetch_msg(final Users user) {

        message = msg.getText().toString();
        FirebaseUser currentuserinstance= FirebaseAuth.getInstance().getCurrentUser();
        Cuserid=currentuserinstance.getUid();
        db_feed_message(user,Cuserid);
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

    private void db_feed_message(Users user, final String cuserid) {
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
                        Users user = data.getValue(Users.class);
                        Log.d("DB","Got my connectors"+user.username);
                        //connect_view.append(user.username+",");
                        String msg_ref_cpy=check_message(cuserid,connect_userid);
                        create_message_ref(cuserid,connect_userid,msg_ref_cpy);
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

    public String check_message(String cuserid, String connect_userid) {
        databaseRef=FirebaseDatabase.getInstance().getReference().child("Msg_refs");
        Query query=databaseRef.orderByChild("Message_user").equalTo(cuserid);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    Message_ref message_ref= (Message_ref) dataSnapshot.getChildren();
                    msg_ref= message_ref.message_ref;
                } else {
                    databaseRef_msg=FirebaseDatabase.getInstance().getReference().child("Messages");
                    msg_ref=databaseRef_msg.push().getKey();
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d("DB","Cancelled");
            }
        });
        return msg_ref;
    }

    public void create_message_ref(String cuserid, String connect_userid, String msg_ref_cpy) {
        message=Cuser_name+":"+msg.getText().toString();
        databaseRef_msg.child(msg_ref_cpy).setValue(message);
        Message_ref message_connector= new Message_ref(msg_ref_cpy,connect_userid);
        Log.d("DB", "message obj:"+message_connector.Message_user);
        databaseRef_msg = FirebaseDatabase.getInstance().getReference().child("Msg_refs");
        databaseRef_msg.child(cuserid).setValue(message_connector);
        Message_ref message_cuser= new Message_ref(msg_ref,cuserid);
        Log.d("DB", "message obj:"+message_connector.Message_user);
        databaseRef_msg.child(connect_userid).setValue(message_cuser);
    }

    public void set_chat_fields(Users user) {
        msg_id.setText(user.username);
    }

}
