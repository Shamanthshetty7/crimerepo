package com.example.crimerepo;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.crimerepo.Crime;
import com.example.crimerepo.ReportAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class Myreportlist extends AppCompatActivity {

    private ListView reportsListView;
    private List<Crime> reportList;
    private ReportAdapter reportAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_reports);

        reportsListView = findViewById(R.id.reports_listview);
        reportList = new ArrayList<>();
        reportAdapter = new ReportAdapter(this, reportList);
        reportsListView.setAdapter(reportAdapter);

        // Get the current user ID

        Intent intent=getIntent();
        String userId = intent.getStringExtra("userName");;

        // Query the "crime_reports" table for reports associated with the user ID
        DatabaseReference reportsRef = FirebaseDatabase.getInstance().getReference("crime_reports");
        reportsRef.orderByChild("userId").equalTo(userId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    // Extract the report data and add it to the list
                    String reportId = snapshot.getKey();
                    String title = snapshot.child("title").getValue(String.class);
                    String description = snapshot.child("description").getValue(String.class);
                    String imageUrl = snapshot.child("image").getValue(String.class);
                    String location = snapshot.child("location").getValue(String.class);
                    String status = snapshot.child("status").getValue(String.class);
                    reportList.add(new Crime(reportId,title,description,location, imageUrl,status));
                }

                // Notify the adapter that the data has changed
                reportAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle the error, if any
            }
        });
    }
}
