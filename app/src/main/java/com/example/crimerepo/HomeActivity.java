package com.example.crimerepo;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;


import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

public class HomeActivity extends AppCompatActivity {
    private Spinner locationSpinner;
    private ListView crimeListView;
    private List<String> locationList;
    private CrimeAdapter crimeAdapter;
    private DatabaseReference crimeRef;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        // Initialize Firebase
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
        crimeRef = rootRef.child("crime_reports");

        // Initialize views
        locationSpinner = findViewById(R.id.location_spinner);
        crimeListView = findViewById(R.id.crime_listview);

        // Retrieve location list from Firebase
        locationList = new ArrayList<>();

        crimeRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, String previousChildName) {
                String location = dataSnapshot.child("location").getValue(String.class);
                if (location != null && !locationList.contains(location)) {
                    locationList.add(location);
                    updateLocationSpinner();
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, String previousChildName) {
                String selectedLocation = locationSpinner.getSelectedItem().toString();
                updateCrimeList(selectedLocation);
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                String selectedLocation = locationSpinner.getSelectedItem().toString();
                updateCrimeList(selectedLocation);
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, String previousChildName) {
                // Do nothing
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Do nothing
            }
        });

        // Create and set the adapter for the location spinner
        ArrayAdapter<String> locationAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, locationList);
        locationAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        locationSpinner.setAdapter(locationAdapter);

        // Initialize the crime adapter
        List<Crime> initialCrimeList = new ArrayList<>(); // Empty list initially
        crimeAdapter = new CrimeAdapter(this, initialCrimeList);
        crimeListView.setAdapter(crimeAdapter);

        // Set a listener for location selection changes
        locationSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedLocation = locationList.get(position);
                updateCrimeList(selectedLocation);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Do nothing
            }
        });
        Button callButton = findViewById(R.id.call_button);
        callButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Code to initiate the call to the emergency page
                // For example, you can use an intent to open a phone dialer with the emergency number
                Intent intent = new Intent(HomeActivity.this, EmergencyCall.class);

                startActivity(intent);
            }
        });

        // Find the "Report Crime" button
        Button reportCrimeButton = findViewById(R.id.report_crime_button);

        // Set OnClickListener for the button
        reportCrimeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Start the ReportActivity
                String userName = getIntent().getStringExtra("userName");
                Intent intent=new Intent(HomeActivity.this, ReportActivity.class);
                intent.putExtra("userName", userName);
                startActivity(intent);
            }
        });
        ImageView profileButton = findViewById(R.id.profile_ic);

        // Set OnClickListener for the button
        profileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Start the ReportActivity
                String userName = getIntent().getStringExtra("userName");
                Intent intent=new Intent(HomeActivity.this, ProfileActivity.class);
                intent.putExtra("userName", userName);
                startActivity(intent);

            }
        });
        crimeListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Crime selectedCrime = crimeAdapter.getItem(position);

                // Start the DisplayReportActivity and pass the selected crime's details
                Intent intent = new Intent(HomeActivity.this, DisplayReportActivity.class);
                intent.putExtra("title", selectedCrime.getTitle());
                intent.putExtra("description", selectedCrime.getDescription());
                intent.putExtra("location", selectedCrime.getLocation());
                intent.putExtra("imageUri", selectedCrime.getImageUri());
                intent.putExtra("Status", selectedCrime.getStatus()); // Pass the image URL as well
                // Add other fields as needed
                startActivity(intent);
            }
        });
    }


    // Method to update the location spinner with the retrieved location list
    private void updateLocationSpinner() {
        ArrayAdapter<String> locationAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, locationList);
        locationAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        locationSpinner.setAdapter(locationAdapter);
        locationAdapter.notifyDataSetChanged();
    }

    // Method to fetch and display the crime list for the selected location
    private void updateCrimeList(String selectedLocation) {
        crimeAdapter.clearCrimeList();
        // Query the database to fetch crime reports for the selected location
        crimeRef.orderByChild("location").equalTo(selectedLocation).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, String previousChildName) {
                String reportId = dataSnapshot.getKey();
                String title = dataSnapshot.child("title").getValue(String.class);
                String description = dataSnapshot.child("description").getValue(String.class);
                String imageUri = dataSnapshot.child("image").getValue(String.class);
                String status = dataSnapshot.child("status").getValue(String.class);
                Crime crime = new Crime(reportId,title, description, selectedLocation, imageUri,status);
                crimeAdapter.addCrime(crime);
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, String previousChildName) {
                // Do nothing
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                // Do nothing
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, String previousChildName) {
                // Do nothing
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Do nothing
            }
        });
    }

    }

