package com.example.crimerepo;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;

public class DisplayReportActivity extends AppCompatActivity {

    private TextView titleTextView;
    private TextView descriptionTextView;
    private TextView locationTextView;
    private ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.detailed_report_activity);

        // Get the crime report details from the intent
        Intent intent = getIntent();
        String title = intent.getStringExtra("title");
        String description = intent.getStringExtra("description");
        String location = intent.getStringExtra("location");
        String imageUrl = intent.getStringExtra("imageUri");

        // Initialize the views
        titleTextView = findViewById(R.id.title_textview);
        descriptionTextView = findViewById(R.id.description_textview);
        locationTextView = findViewById(R.id.location_text);
        imageView = findViewById(R.id.crime_image_imageview);

        // Set the crime report details in the TextViews
        titleTextView.setText(title);
        descriptionTextView.setText(description);
        locationTextView.setText(location);

        // Load and display the image using Glide
        if (imageUrl != null && !imageUrl.isEmpty()) {
            Glide.with(this)
                    .load(imageUrl)
                    .into(imageView);
        }else{
            imageView.setImageResource(R.drawable.profile_icon);
        }
    }
}
