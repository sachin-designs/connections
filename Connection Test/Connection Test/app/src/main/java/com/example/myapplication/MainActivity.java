package com.example.myapplication;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;


public class MainActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    EditText email;
    EditText password;
    private FirebaseDatabase database;
    private DatabaseReference db_ref;
    Button b;
    SharedPreferences sharedpreferences;
    String sEmail;
    String sPassword;
    GoogleSignInClient mGoogleSignInClient;
    DatabaseReference databaseRef;
    TelephonyManager tm;
    private static final int REQUEST_CODE = 101;
    DatabaseReference dref;
    String IMEI;

    private static final int RC_SIGN_IN = 234;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        databaseRef = FirebaseDatabase.getInstance().getReference().child("Register");
        mAuth = FirebaseAuth.getInstance();

        // Configure Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        // Build a GoogleSignInClient with the options specified by gso.
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        //Now we will attach a click listener to the sign_in_button
        //and inside onClick() method we are calling the signIn() method that will open
        //google sign in intent
        findViewById(R.id.sign_in_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signIn();
//                Intent intent = new Intent(MainActivity.this, ChatActivity.class);
//                finish();
//                startActivity(intent);
            }
        });
    }

    //this method is called on click
    private void signIn() {
        //getting the google signin intent
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();

        //starting the activity for result
        startActivityForResult(signInIntent, RC_SIGN_IN);
        Log.d("DB", "result fo rcsigin" + RC_SIGN_IN);

    }
//    protected void onStart() {
//        super.onStart();
//
//        //if the user is already signed in
//        //we will close this activity
//        //and take the user to profile activity
//        if (mAuth.getCurrentUser() != null) {
//            finish();
//            startActivity(new Intent(this, ChatActivity.class));
//        }
//    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d("DB", "result for rcsigin2-" + RC_SIGN_IN);
        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                Log.d("DB", "result for task1-" + task);
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                Log.d("DB", "result for gmailaccount-" + account);
                firebaseAuthWithGoogle(account);
            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
                Log.d("DB", "Google sign in failed", e);
                // ...
            }
        }
    }

    private void firebaseAuthWithGoogle(final GoogleSignInAccount acct) {
        Log.d("DB", "firebaseAuthWithGoogle:" + acct.getId());

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d("DB", "signInWithCredential:success");
                            SessionManagement obj = new SessionManagement(MainActivity.this);
                            String acct_name = acct.getDisplayName();
                            Log.d("DB", "email display name" + acct_name);
                            Log.d("DB", "email display name" + acct.getDisplayName());
                            obj.setName(acct_name);
                            obj.setGaccnt(acct);
                            String signval = mAuth.getCurrentUser().getUid();
                            Log.d("DB", "uidvalue" + signval);
                            Query query = databaseRef.orderByKey().equalTo(signval);
                            query.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    if (dataSnapshot.exists()) {

                                        if (checkIMEI(signval)) {
                                            switchActivity();
                                        } else {
                                            //TODO: Develop a POP-UP option for Unique Key
                                        }
                                        Log.d("DB", "exists");
                                    } else {
                                        Log.d("DB", "user not exists");
                                        Register();
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {
                                    Log.d("DB", "DB failed");
                                }
                            });

//                            if(signval){
//                                switchActivity();
//                            }else {
//
//                                FirebaseUser user = mAuth.getCurrentUser();
//                            }

                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w("DB", "signInWithCredential:failure", task.getException());
                            Snackbar.make(findViewById(R.id.username), "Authentication Failed.", Snackbar.LENGTH_SHORT).show();

                        }

                        // ...
                    }
                });
    }

    public void Register() {
        Intent intent = new Intent(MainActivity.this, RegisterActivity.class);
        finish();
        startActivity(intent);
    }

    public void Register(View view) {
        Intent intent = new Intent(MainActivity.this, RegisterActivity.class);
        finish();
        startActivity(intent);
    }


    public void signinOperation(View view) {
        email = (EditText) findViewById(R.id.username);
        password = (EditText) findViewById(R.id.password);

        sEmail = email.getText().toString();
        sPassword = password.getText().toString();

        mAuth = FirebaseAuth.getInstance();

        mAuth.signInWithEmailAndPassword(sEmail, sPassword)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {

                            Toast.makeText(MainActivity.this, "Sign In success",
                                    Toast.LENGTH_SHORT).show();
                            SessionManagement obj = new SessionManagement(MainActivity.this);
                            obj.setName(sEmail);
                            switchActivity();
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.d("TAG", "signInWithEmail:failure");
                            Toast.makeText(MainActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                            //updateUI(null);
                        }
                    }
                });
    }

    public void switchActivity() {
        Intent intent = new Intent(MainActivity.this, ChatActivity.class);
        Log.d("DB", "intent" + intent);
        intent.putExtra("name", sEmail);
        finish();
        startActivity(intent);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CODE: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, "Permission granted.", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "Permission denied.", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    public Boolean checkIMEI(String signval) {

        dref = FirebaseDatabase.getInstance().getReference().child("Register").child(signval);
        dref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Users user = dataSnapshot.getValue(Users.class);
                IMEI = user.IMEI;
                if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.READ_PHONE_STATE}, REQUEST_CODE);
                    return;
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        if (IMEI.equals(tm.getDeviceId())){
            return true;
        }else{
            return false;
        }

    }
}