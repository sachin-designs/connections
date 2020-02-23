package com.example.myapplication;

import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.android.material.chip.Chip;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.Map;

public class Connectivity extends AppCompatActivity {

    private DatabaseReference databaseRef;
    private DatabaseReference dbref_cuser;
    private Chip chip1, chip2;
    ObjectMapper oMapper = new ObjectMapper();
    Map<String, Object> Muser;
    String Schip1,Schip2;
    String cuser;
    String cplace,cschool;
    TextView connect_view;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.connectivity);
        Log.d("DB","inside connectiviy class");
        databaseRef= FirebaseDatabase.getInstance().getReference().child("Register");
        chip1 = (Chip) findViewById(R.id.chip1);
        chip2 = (Chip) findViewById(R.id.chip2);
        FirebaseUser user= FirebaseAuth.getInstance().getCurrentUser();
        final String userid=user.getUid();
        connect_view=(TextView)findViewById(R.id.Connect_view);
        Schip1=chip1.getText().toString();
        Schip2=chip2.getText().toString();
        dbref_cuser=databaseRef.child(userid);

        Query query = databaseRef.orderByChild(Schip1);
        dbref_cuser.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    Users user = dataSnapshot.getValue(Users.class);
                    Muser=oMapper.convertValue(user, Map.class);
                    cschool= (String) Muser.get(Schip1);
                    cplace=(String)Muser.get(Schip2);
                    Log.d("DB","Data exists "+cplace);

                }else {
                    Log.d("DB","Data not exists for current user");
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d("DB","Data for cuser cancelled");

            }
        });

        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Schip1=chip1.getText().toString();
                Schip2=chip2.getText().toString();
                if (dataSnapshot.exists()) {

                    // dataSnapshot is the "issue" node with all children with id 0
                    for (DataSnapshot data : dataSnapshot.getChildren()) {
                        // do something with the individual "issues"

                        Users user = data.getValue(Users.class);
                        Muser = oMapper.convertValue(user, Map.class);
                        if (cplace.equals(Muser.get(Schip2)) && cschool.equals(Muser.get(Schip1))) {
                            String connectors=user.username + " " + user.place + " " + user.school;
                            connect_view.append(connectors+" ");
                            Log.d("DB", "Datasnapshot " +user.username + " " +user.place + " " +user.school);
                        }
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
        // enter logic to filter out connectors.

    }
}
