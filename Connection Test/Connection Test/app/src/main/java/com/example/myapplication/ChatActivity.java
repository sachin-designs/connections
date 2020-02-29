package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ChatActivity extends AppCompatActivity {
    Button b;
    private DatabaseReference databaseRef;
    GoogleSignInClient mGoogleSignInClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chat_activity);

        b = (Button)findViewById(R.id.button1);

        FirebaseUser user= FirebaseAuth.getInstance().getCurrentUser();
        final String userid=user.getUid();
        final TextView textView = (TextView) findViewById(R.id.textView);
        Log.d("User","User"+userid);
        databaseRef = FirebaseDatabase.getInstance().getReference().child("Register").child(userid);
        // Read from the database
        databaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Users user = dataSnapshot.getValue(Users.class);
                Log.d("DB","Sucess"+user.username);
//                Iterator<DataSnapshot> name =dataSnapshot.getChildren().iterator();
//                for (Iterator<DataSnapshot> it = name; it.hasNext(); ) {
//                    DataSnapshot n = it.next();
//                    Users user=n.getValue(Users.class);
//                    Log.d("DB","Sucess"+user.username);
//                }
                textView.setText(user.username);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d("DB","Failed");
            }
        });
    }

    public void connectivity(View view){
        Intent intent = new Intent(ChatActivity.this, Connectivity.class);
        Log.d("DB","inside connectiviy method");
//       // intent.putExtra("name", sEmail);
        finish();
        Log.d("DB","inside connectiviy method2");
        startActivity(intent);

    }

    public  void logout(View view){
// Configure Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        // Build a GoogleSignInClient with the options specified by gso.
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        new SessionManagement(ChatActivity.this).remove();
        mGoogleSignInClient.signOut()
                .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()) {
                            Log.d("DB", "Logged out Gaccnt");
                        }else{
                            Log.d("DB", "No Gaccount");
                        }
                    }
                });


        Intent intent = new Intent(ChatActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }


}
