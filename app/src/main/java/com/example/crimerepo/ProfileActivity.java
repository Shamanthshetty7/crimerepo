package com.example.crimerepo;

import static android.content.ContentValues.TAG;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ProfileActivity extends AppCompatActivity {

    private TextView nameTextView;
    private TextView locationTextView;
    private TextView emailTextView;
    private SharedPreferences sharedPreferences;
    private DatabaseReference databaseReference;
    private SessionManager sessionManager;
    private Button reports;
    private Button logoutButton;
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        sessionManager = new SessionManager(this);
        // Initialize views
        nameTextView = findViewById(R.id.name_textview);
        locationTextView = findViewById(R.id.email_textview);
        emailTextView = findViewById(R.id.location_textview);
        reports=findViewById(R.id.my_reports);
        logoutButton=findViewById(R.id.logout);
        reports.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Start the ReportActivity
                String userName = getIntent().getStringExtra("userName");
                Intent intent=new Intent(ProfileActivity.this, Myreportlist.class);
                intent.putExtra("userName", userName);
                startActivity(intent);

            }
        });
        // Logout button click listener
        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sessionManager.setLoggedIn(false);
                sessionManager.setUsername("");
                Toast.makeText(ProfileActivity.this, "Logged out successfully!", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(ProfileActivity.this, LoginActivity.class);
                startActivity(intent);

            }
        });

        // Get the user name from the SignUpActivity
        Intent intent=getIntent();

            String userName = intent.getStringExtra("userName");

            // Initialize Firebase Database reference
            databaseReference = FirebaseDatabase.getInstance().getReference();

            // Fetch user information from Firebase
            fetchUserInfo(userName);

    }

    private void fetchUserInfo(String userName) {
        databaseReference.child("users").child(userName).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    // Retrieve user information from dataSnapshot
                    String name = dataSnapshot.child("Name").getValue(String.class);
                    String email = dataSnapshot.child("Email").getValue(String.class);
                    String location = dataSnapshot.child("Location").getValue(String.class);
                     Log.d(TAG,userName);
                    // Set the fetched information to the corresponding views
                    nameTextView.setText(name);
                    emailTextView.setText(email);
                    locationTextView.setText(location);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle any errors
            }
        });
    }
}
