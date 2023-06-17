package com.example.crimerepo;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class LoginActivity extends AppCompatActivity {

    private EditText nameEditText, passwordEditText;
    private Button loginButton;
    private TextView switchTab;
    DatabaseReference databaseReference= FirebaseDatabase.getInstance().getReferenceFromUrl("https://crimereporter-9a347-default-rtdb.firebaseio.com");

    @SuppressLint({"WrongViewCast", "MissingInflatedId"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Initialize views
        nameEditText = findViewById(R.id.loginname);
        passwordEditText = findViewById(R.id.password);
        loginButton = findViewById(R.id.login);
        switchTab=findViewById(R.id.alreadyHaveAccount);
        // Handle login button click
        switchTab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this, SignUpActivity.class));
            }
        });
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Get user input
                String username = nameEditText.getText().toString();
                String password = passwordEditText.getText().toString();

                // Retrieve stored signup data


                // Validate login credentials
                if (username.isEmpty() || password.isEmpty()) {
                    Toast.makeText(LoginActivity.this, "Please enter email and password", Toast.LENGTH_SHORT).show();
                } else  {
                    databaseReference.child("users").addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    if(snapshot.hasChild(username)) {
                                        final String getPassword=snapshot.child(username).child("Password").getValue(String.class);
                                        if(getPassword.equals(password)){
                                            Toast.makeText(LoginActivity.this, "Login successful!", Toast.LENGTH_SHORT).show();
                                            Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                                            intent.putExtra("userName", username);
                                            startActivity(intent);
                                            finish();
                                    }else {
                                            Toast.makeText(LoginActivity.this, "Wrong Password", Toast.LENGTH_SHORT).show();
                                        }
                                        }else {
                                        Toast.makeText(LoginActivity.this, "Username  Does not exist!", Toast.LENGTH_SHORT).show();
                                    }
                                    }


                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });
                }






            }
        });
    }
}
