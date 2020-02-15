package com.design.firebase_register;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class MainActivity extends AppCompatActivity {

    // Declare your layout objects(view,buttons) here to interact with Layouts.
    // Declared firebase and database objects.
    private FirebaseAuth mAuth;
    private EditText mEmailView;
    private EditText mUsernameView;
    private EditText mPasswordView;
    private EditText mPlace;
    private EditText mWork;
    private DatabaseReference Db_ref;
    private Boolean check_reg;

    // oncreate method to create the required layout and fetch its layout objects.
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mEmailView=(EditText) findViewById(R.id.Email); // Fetching Email view object from layout .
        mUsernameView=(EditText)findViewById(R.id.Username);// Fetching username view object from layout
        mPasswordView=(EditText) findViewById(R.id.Password);// Fetching Pass view object from layout
        mPlace=(EditText) findViewById(R.id.Place);// Fetching place view object from layout
        mWork=(EditText) findViewById(R.id.work); // Fetching work view object from layout

        mAuth = FirebaseAuth.getInstance(); // initialised firebase instance to firebase object.
        }

    // calling method register for each click on the button.
    public void register(View v){
        //Log.d("Reg", "inside register method");
        // get all the text data from view objects of the layout.
        final String username = mUsernameView.getText().toString();
        //Log.d("Reg", "username"+username);
        final String Email = mEmailView.getText().toString();
        //Log.d("Reg", "EMail"+Email);
        final String Pass = mPasswordView.getText().toString();
        //Log.d("Reg", "EMail"+Pass);
        final String Place = mPlace.getText().toString();
        //Log.d("Reg", "EMail"+Place);
        final String work = mWork.getText().toString();
        //Log.d("Reg", "EMail"+work);

        Db_ref = FirebaseDatabase.getInstance().getReference().child("Register");
        final String Uid = Db_ref.push().getKey();
        //Log.d("Reg", "EMail"+Uid);


        //Log.d("Reg", "before create user");
        //using firebase instance creating a user in firebase using email and pass
        mAuth.createUserWithEmailAndPassword(Email,Pass).addOnCompleteListener(this,
                                           new OnCompleteListener<AuthResult>() {
            // oncomplete is inbuilt method to check create user is successful or not
        @Override
        public void onComplete(@NonNull Task<AuthResult> task) {   //task is parameter for oncomplete method which holds boolean value.
            //Log.d("Reg","Creating user"+task.isSuccessful()+"for"+username);

             //Log.d("Reg","Registering user only if he is created in firebase");

            // register the user to firebase DB if the user is created successfully.

            if (task.isSuccessful()) {
                UserDetailes user = new UserDetailes(username, Email, Pass, Place, work, Uid); // Object to hold the user data.

                check_reg=Db_ref.child(Uid).setValue(user).isComplete();// Saving the user data in Firebase DB under UID reference.


                //Log.d("Reg", "Register user successfully" );

                switch_activity(task.isSuccessful());// method to switch activity.
            } else {
                //Log.d("Reg", "if existing user comes here");
                switch_activity(task.isSuccessful());
            }

        }

    });
}

   // method to switch required activity if user is registered succcessfuly.
    public void switch_activity(boolean task) {
        //switch to different activity if user is register successfully.
        if(task){
            setContentView(R.layout.dummylayout); // switching the layout to dummylayout xml.

            // best way to switch layout is switching to java class and setting the layout.
            /*
            Intent intent = new Intent(MainActivity.this, DummyActivity.class);
            finish();
            startActivity(intent);
             */

            // Displace tost msg as successfully registered
            Toast.makeText(this, "Registration successful for user ", Toast.LENGTH_SHORT).show();// Displace tost msg as successfully registered.
        }else {
            // Displace tost msg as unsuccessful registration.
            Toast.makeText(this, "Registration unsuccessful for user something went wrong ", Toast.LENGTH_SHORT).show();// Displace tost msg as unsuccessful registration.

            // empty the register layout fields and delete the created user in firebase to make user enter again....
        }
    }

}
