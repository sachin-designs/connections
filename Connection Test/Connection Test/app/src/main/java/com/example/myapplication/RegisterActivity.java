package com.example.myapplication;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.AutocompletePrediction;
import com.google.android.libraries.places.api.model.AutocompleteSessionToken;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.model.RectangularBounds;
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.AutocompleteActivity;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class RegisterActivity extends AppCompatActivity {
    EditText username;
    EditText password;
    EditText confpassword;
    EditText email;
    EditText phnumber;
    AutoCompleteTextView school;
    EditText place;
    EditText work;
    String apiKey = "AIzaSyACIKN3ZBXgkkgKq53BrinWaFsOyB-AVyE";
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
    int AUTOCOMPLETE_REQUEST_CODE = 1;
    Double longitude;
    Double latitude;
    ArrayList<String> schools = new ArrayList<String>();
    TelephonyManager tm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.register_activity);
        // Initialize the SDK

        // Start the autocomplete intent.
        school = (AutoCompleteTextView) findViewById(R.id.school);
        school.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String schoolText = school.getText().toString();
                schools = autocompletebuilder(schoolText);

            }
        });

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

    public ArrayList<String> autocompletebuilder(String schoolText) {
        ArrayList<String> schools = new ArrayList<String>();
        Places.initialize(getApplicationContext(), apiKey);

// Create a new Places client instance
        PlacesClient placesClient = Places.createClient(this);
        // Set the fields to specify which types of place data to
// Create a new token for the autocomplete session. Pass this to FindAutocompletePredictionsRequest,
// and once again when the user makes a selection (for example when calling fetchPlace()).
        AutocompleteSessionToken token = AutocompleteSessionToken.newInstance();
// Create a RectangularBounds object.

        RectangularBounds bounds = RectangularBounds.newInstance(
                new LatLng(12.9982, 12.9982),
                new LatLng(12.9982, 12.9982));

// Use the builder to create a FindAutocompletePredictionsRequest.
        FindAutocompletePredictionsRequest request = FindAutocompletePredictionsRequest.builder()
                // Call either setLocationBias() OR setLocationRestriction().
                //.setLocationRestriction(bounds)
                .setSessionToken(token)
                .setQuery(schoolText)
                .build();
        schools.clear();
        placesClient.findAutocompletePredictions(request).addOnSuccessListener((response) -> {
            for (AutocompletePrediction prediction : response.getAutocompletePredictions()) {
                String id = prediction.getPlaceId();

                String name = prediction.getPrimaryText(null).toString();
                Log.d("DB", "data1" + id);
                Log.d("DB", "data" + name);
                schools.add(name);
            }
        }).addOnFailureListener((exception) -> {
            if (exception instanceof ApiException) {
                ApiException apiException = (ApiException) exception;
                Log.d("DB", "Place not found: " + apiException.getStatusCode());
            }
        });
        Log.d("DB", "autocompletebuilder:" + schools);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_dropdown_item_1line, schools);
        Log.d("DB", "autocompletebuilder:" + school.getThreshold());
        school.setAdapter(adapter);
        return schools;
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == AUTOCOMPLETE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                Place place = Autocomplete.getPlaceFromIntent(data);
                Log.i("DB", "Place: " + place.getName() + ", " + place.getId());
            } else if (resultCode == AutocompleteActivity.RESULT_ERROR) {
                // TODO: Handle the error.
                Status status = Autocomplete.getStatusFromIntent(data);
                Log.i("DB", status.getStatusMessage());
            } else if (resultCode == RESULT_CANCELED) {
                // The user canceled the operation.
            }
        }
    }

    // onResume() lifecycle callback:
    @Override
    protected void onResume() {
        super.onResume();
        Log.d("DB", "onResume() called");
        if (mUseLocation) locationcurrent();
    }


    public void signUpProcess(View view) {
        username = (EditText) findViewById(R.id.username);
        password = (EditText) findViewById(R.id.regpassword);
        confpassword = (EditText) findViewById(R.id.regconfpassword);
        email = (EditText) findViewById(R.id.emailId);
        phnumber = (EditText) findViewById(R.id.phnumber);
        school = (AutoCompleteTextView) findViewById(R.id.school);
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
        Log.d("DB", "session_object" + obj.getGaccnt_gmail());

        if (gmail.isEmpty()) {
            mAuth.createUserWithEmailAndPassword(sEmail, sPassword)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                Log.d("TAG", "usercreated");
                                user = mAuth.getCurrentUser();
                                Uid = user.getUid();
                                if (ActivityCompat.checkSelfPermission(RegisterActivity.this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
                                    ActivityCompat.requestPermissions(RegisterActivity.this, new String[]{Manifest.permission.READ_PHONE_STATE}, REQUEST_CODE);
                                    return;
                                }
                                Users users = new Users(sUsername, sPassword, sPhNumber, sEmail, sPlace, sSchool, sWork, Uid, tm.getDeviceId());
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
        } else {
            user = mAuth.getCurrentUser();
            Uid = user.getUid();

            Users users = new Users(gname,sPassword, sPhNumber, gmail, sPlace, sSchool, sWork, Uid, tm.getDeviceId());
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

                 longitude = (location.getLongitude());
                 latitude = (location.getLatitude());

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