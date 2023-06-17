package com.example.crimerepo;

import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

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

    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        // Initialize views
        nameTextView = findViewById(R.id.name_textview);
        locationTextView = findViewById(R.id.email_textview);
        emailTextView = findViewById(R.id.location_textview);

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
