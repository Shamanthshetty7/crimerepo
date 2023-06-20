package com.example.crimerepo;



import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.ImageDecoder;
import android.net.Uri;
import android.os.Bundle;

import android.provider.Contacts;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.android.gms.common.internal.Constants;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class EditReportActivity extends AppCompatActivity {
    private Spinner statusSpinner;
    private ArrayAdapter<CharSequence> statusAdapter;

    private ImageView imageView;
    private EditText titleEditText;
    private EditText descriptionEditText;
    private EditText locationEditText;
    private Button chooseImageButton;
    private Button updateButton;
    public static final int PICK_IMAGE_REQUEST = 1;
    private Uri selectedImageUri;

    private DatabaseReference databaseReference;
    private StorageReference storageReference;

    private String reportId; // The ID of the report being edited

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_report);

        databaseReference = FirebaseDatabase.getInstance().getReference("crime_reports");

        imageView = findViewById(R.id.edit_image_view);
        titleEditText = findViewById(R.id.edit_title_edit_text);
        descriptionEditText = findViewById(R.id.edit_description_edit_text);
        locationEditText = findViewById(R.id.edit_location_edit_text);
        chooseImageButton = findViewById(R.id.choose_image_button);
        updateButton = findViewById(R.id.update_button);
        statusSpinner = findViewById(R.id.status_spinner);
        statusAdapter = ArrayAdapter.createFromResource(this, R.array.status_array, android.R.layout.simple_spinner_item);
        statusAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        statusSpinner.setAdapter(statusAdapter);

        // Retrieve the report ID from the intent
        reportId = getIntent().getStringExtra("reportId");

        chooseImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openGallery();
            }
        });

        updateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateCrimeReport();
            }
        });

        // Retrieve the existing report details from Firebase and populate the UI
        retrieveReportDetails();
    }

    private void openGallery() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Image"), PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            selectedImageUri = data.getData();
            try {
                Bitmap bitmap;
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.P) {
                    ImageDecoder.Source source = ImageDecoder.createSource(getContentResolver(), selectedImageUri);
                    bitmap = ImageDecoder.decodeBitmap(source);
                } else {
                    bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), selectedImageUri);
                }
                imageView.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void retrieveReportDetails() {
        // Retrieve the existing report details from Firebase based on the report ID
        databaseReference.child(reportId).get().addOnSuccessListener(new OnSuccessListener<DataSnapshot>() {
            @Override
            public void onSuccess(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    String title = dataSnapshot.child("title").getValue(String.class);
                    String description = dataSnapshot.child("description").getValue(String.class);
                    String location = dataSnapshot.child("location").getValue(String.class);
                    String status = dataSnapshot.child("status").getValue(String.class);
                    String imageUrl = dataSnapshot.child("image").getValue(String.class);

                    titleEditText.setText(title);
                    descriptionEditText.setText(description);
                    locationEditText.setText(location);
                    statusSpinner.setSelection(statusAdapter.getPosition(status));

                    // Load the existing image using the imageUrl (if available)
                    if (imageUrl != null && !imageUrl.isEmpty()) {

                       Glide.with(EditReportActivity.this).load(imageUrl).into(imageView);
                    }
                } else {
                    Toast.makeText(EditReportActivity.this, "Report not found.", Toast.LENGTH_SHORT).show();
                    finish();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(EditReportActivity.this, "Failed to retrieve report details.", Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }

    private void updateCrimeReport() {
        String title = titleEditText.getText().toString().trim();
        String description = descriptionEditText.getText().toString().trim();
        String location = locationEditText.getText().toString().trim();
        String status = statusSpinner.getSelectedItem().toString();

        if (title.isEmpty() || description.isEmpty() || location.isEmpty()) {
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        // Update the crime report details in the database
        DatabaseReference reportRef = databaseReference.child(reportId);
        reportRef.child("title").setValue(title);
        reportRef.child("description").setValue(description);
        reportRef.child("location").setValue(location);
        reportRef.child("status").setValue(status);

        if (selectedImageUri != null) {
            // If a new image is selected, update the image as well
            storageReference = FirebaseStorage.getInstance().getReference("/images");
            storageReference.child(reportId).putFile(selectedImageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    storageReference.child(reportId).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            String imgUri = uri.toString();
                            reportRef.child("image").setValue(imgUri);
                            Toast.makeText(EditReportActivity.this, "Crime report updated successfully", Toast.LENGTH_SHORT).show();
                            finish();
                        }
                    });
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(EditReportActivity.this, "Failed to upload image. Please try again.", Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            Toast.makeText(EditReportActivity.this, "Crime report updated successfully", Toast.LENGTH_SHORT).show();
            finish();
        }
    }
}
