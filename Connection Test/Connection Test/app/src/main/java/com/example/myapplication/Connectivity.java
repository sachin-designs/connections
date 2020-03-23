package com.example.myapplication;

import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Map;

public class Connectivity extends AppCompatActivity {

    private DatabaseReference databaseRef;
    private DatabaseReference dbref_cuser;
    private Chip chip;
    ObjectMapper oMapper = new ObjectMapper();
    Map<String, Object> Muser;
    String Schip1,Schip2;
    ChipGroup Gchip;
    String search, Sdata;
    TextView connect_view;
    ArrayList<Users> Luser=new ArrayList<Users>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.connectivity);
        Log.d("DB", "inside connectiviy class");
        databaseRef = FirebaseDatabase.getInstance().getReference().child("Register");
        chip = (Chip) findViewById(R.id.chip1);
        Gchip = (ChipGroup) findViewById(R.id.filter_chip_group);
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        final String userid = user.getUid();
        connect_view = (TextView) findViewById(R.id.Connect_view);
        dbref_cuser = databaseRef.child(userid);

        Gchip.setOnCheckedChangeListener(new ChipGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(ChipGroup group, int checkedId) {
                chip=group.findViewById(checkedId);
                if(chip!=null){
                    search=chip.getText().toString();
                    Log.d("DB","Got the text!!"+ search);
                    connect_view.setText(null);
                    get_cuser_data();
                } else{
                    connect_view.setText(null);
                }
            }
        });
    }

    public  void get_cuser_data() {
        dbref_cuser.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    Users user = dataSnapshot.getValue(Users.class);
                    Muser=oMapper.convertValue(user, Map.class);
                    Sdata= (String) Muser.get(search);
                    Log.d("DB","Data exists "+Sdata);
                    get_connectors();

                }else {
                    Log.d("DB","Data not exists for current user");
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d("DB","Data for cuser cancelled");

            }
        });
    }

    public void get_connectors() {
        Query query = databaseRef.orderByChild(search).equalTo(Sdata);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    Luser.clear();
                    // dataSnapshot is the "issue" node with all children with id 0
                    for (DataSnapshot data : dataSnapshot.getChildren()) {
                        // do something with the individual "issues"
                        Users user = data.getValue(Users.class);
                        Log.d("DB","Got my connectors"+user.username);
                        //connect_view.append(user.username+",");
                        Luser.add(user);
                    }
                    list_connectors(Luser);

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

    public void list_connectors(ArrayList<Users> lusers) {
        RecyclerView rv = (RecyclerView) findViewById(R.id.rv);
        ContactsAdapter adapter = new ContactsAdapter(lusers);
        // Attach the adapter to the recyclerview to populate items
        rv.setAdapter(adapter);
        // Set layout manager to position the items
        rv.setLayoutManager(new LinearLayoutManager(this));
    }

}