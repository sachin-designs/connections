package com.example.myapplication;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class RegisterActivity extends AppCompatActivity {
    EditText username;
    EditText password;
    EditText confpassword;
    EditText email;
    EditText phnumber;
    EditText school;
    EditText place;
    EditText work;
    private FirebaseAuth mAuth;
    private DatabaseReference databaseRef;
    Button b;
    String gmail, gname;
    String Uid;
    FirebaseUser user;
    String LOCATION_PROVIDER = LocationManager.GPS_PROVIDER;
    LocationManager mLocationManager;
    LocationListener mLocationListener;
    long MIN_TIME = 5000;
    float MIN_DISTANCE = 1000;
    final int REQUEST_CODE = 123;
    boolean mUseLocation = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register_activity);
        SessionManagement obj = new SessionManagement(RegisterActivity.this);
        gmail = obj.getGaccnt_gmail();
        gname = obj.getGaccnt_name();
        username = (EditText) findViewById(R.id.username);
        email = (EditText) findViewById(R.id.emailId);
        if (!gname.isEmpty()) {
            username.setText(gname);
            email.setText(gmail);
        }
        Log.d("DB", "session_object " + gmail + gname);
    }
    // onResume() lifecycle callback:
    @Override
    protected void onResume() {
        super.onResume();
        Log.d("DB", "onResume() called");
        if(mUseLocation) locationcurrent();
    }


    public void signUpProcess(View view) {
        username = (EditText) findViewById(R.id.username);
        password = (EditText) findViewById(R.id.regpassword);
        confpassword = (EditText) findViewById(R.id.regconfpassword);
        email = (EditText) findViewById(R.id.emailId);
        phnumber = (EditText) findViewById(R.id.phnumber);
        school = (EditText) findViewById(R.id.school);
        place = (EditText) findViewById(R.id.Place);
        work = (EditText) findViewById(R.id.work);

        final String sUsername = username.getText().toString();
        final String sPassword = password.getText().toString();
        String sConfPassword = confpassword.getText().toString();
        final String sEmail = email.getText().toString();
        final String sPhNumber = phnumber.getText().toString();
        final String sSchool = school.getText().toString();
        final String sPlace = place.getText().toString();
        final String sWork = work.getText().toString();
        b = findViewById(R.id.button);

         mAuth = FirebaseAuth.getInstance();

        databaseRef = FirebaseDatabase.getInstance().getReference().child("Register");
       // final String Uid = databaseRef.push().getKey();
        SessionManagement obj = new SessionManagement(RegisterActivity.this);
        Log.d("DB","session_object"+obj.getGaccnt_gmail());
    if(gmail.isEmpty()) {
        mAuth.createUserWithEmailAndPassword(sEmail, sPassword)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Log.d("TAG", "usercreated");
                            user = mAuth.getCurrentUser();
                            Uid = user.getUid();
                            Users users = new Users(sUsername, sPassword, sPhNumber, sEmail, sPlace, sSchool, sWork, Uid);
                            databaseRef.child(Uid).setValue(users);
                            Toast.makeText(RegisterActivity.this, "Authentication success.",
                                    Toast.LENGTH_LONG).show();
                            SessionManagement obj = new SessionManagement(RegisterActivity.this);
                            obj.setName(sEmail);
                            switchActivity();
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.d("TAG", "createUserWithEmail:failure");
                            Toast.makeText(RegisterActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }else{
        user = mAuth.getCurrentUser();
        Uid = user.getUid();
        Users users = new Users(gname,sPhNumber, gmail, sPlace, sSchool, sWork, Uid);
        databaseRef.child(Uid).setValue(users);
        switchActivity();
    }
    }

    public void locationcurrent(){
        Log.d("DB","Indide locaitoncurrent");
        mLocationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        mLocationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                Log.d("DB", "onLocationChanged() callback received");
                Log.d("DB","inside");

                String longitude = String.valueOf(location.getLongitude());
                String latitude = String.valueOf(location.getLatitude());

                Log.d("DB", "longitude is: " + longitude);
                Log.d("DB", "latitude is: " + latitude);
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {
                Log.d("DB", "onStatusChanged() callback received. Status: " + status);
            }

            @Override
            public void onProviderEnabled(String provider) {
                Log.d("DB", "onProviderEnabled() callback received. Provider: " + provider);
            }

            @Override
            public void onProviderDisabled(String provider) {
                Log.d("DB", "onProviderDisabled() callback received. Provider: " + provider);
            }
        };
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            ActivityCompat.requestPermissions(this,
                    new String[] {Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_CODE);
            return;
        }

        mLocationManager.requestLocationUpdates(LOCATION_PROVIDER, MIN_TIME, MIN_DISTANCE, mLocationListener);

    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if(requestCode == REQUEST_CODE){
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.d("DB", "onRequestPermissionsResult(): Permission granted!");

                // Getting weather only if we were granted permission.
                locationcurrent();
            } else {
                Log.d("DB", "Permission denied =( ");
            }
        }

    }


    public void switchActivity(){
        Intent intent = new Intent(RegisterActivity.this, ChatActivity.class);
        SessionManagement sessionManagement = new SessionManagement(RegisterActivity.this);
        intent.putExtra("name", sessionManagement.getName());
        finish();
        startActivity(intent);
    }
}