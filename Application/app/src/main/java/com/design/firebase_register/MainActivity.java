package com.design.firebase_register;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private EditText mEmailView;
    private EditText mUsernameView;
    private EditText mPasswordView;
    private EditText mPlace;
    private EditText mWork;
    private DatabaseReference Db_ref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mEmailView=(EditText) findViewById(R.id.Email);
        mUsernameView=(EditText)findViewById(R.id.Username);
        mPasswordView=(EditText) findViewById(R.id.Password);
        mPlace=(EditText) findViewById(R.id.Place);
        mWork=(EditText) findViewById(R.id.work);
        mAuth = FirebaseAuth.getInstance();



        }

    public void register(View v){
        final String Email=mEmailView.getText().toString();
        final String Pass=mPasswordView.getText().toString();
        final String username=mUsernameView.getText().toString();
        mAuth.createUserWithEmailAndPassword(Email,Pass).addOnCompleteListener(this,
                                                                                 new OnCompleteListener<AuthResult>() {
        @Override
        public void onComplete(@NonNull Task<AuthResult> task) {
            Log.d("Reg","Create user"+task.isSuccessful()+username);
            if(task.isSuccessful()){
                Log.d("Reg","Create user"+task.isSuccessful()+Db_ref);
                Log.d("Reg","Registering user");
                reg_details(task.isSuccessful());
            }
            else{
                Log.d("Reg","Register user unsuccessfully");
                reg_details(task.isSuccessful());
            }
        }
    });
}


    public void reg_details(boolean task) {
        if (task) {
            Toast.makeText(this, "Registeration successfull for user ", Toast.LENGTH_SHORT).show();
            final String username = mUsernameView.getText().toString();
            final String Email = mEmailView.getText().toString();
            final String Pass = mPasswordView.getText().toString();
            final String Place = mPlace.getText().toString();
            final String work = mWork.getText().toString();
            Db_ref = FirebaseDatabase.getInstance().getReference().child("Register");
            final String Uid = Db_ref.push().getKey();
            Log.d("Reg", "inside if reg_details");
            UserDetailes user = new UserDetailes(username, Email, Pass, Place, work, Uid);
            Log.d("Reg", "Register user successfully" + user);
            Db_ref.child(Uid).setValue(user);
            Log.d("Reg", "Register user successfully");
        } else {
            Toast.makeText(this, "Registeration unsuccessfull for user ", Toast.LENGTH_SHORT).show();
        }

    }



}
//commented for git commit.