package com.example.crimerepo;

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

public class SignUpActivity extends AppCompatActivity {
TextView btn;
    private EditText nameEditText;
    private EditText emailEditText;
    private EditText passwordEditText;
    private EditText locationEditText;
    private Button signUpButton;
     DatabaseReference databaseReference= FirebaseDatabase.getInstance().getReferenceFromUrl("https://crimereporter-9a347-default-rtdb.firebaseio.com/");
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        nameEditText = findViewById(R.id.loginname);
        emailEditText = findViewById(R.id.editTextTextEmailAddress);
        passwordEditText = findViewById(R.id.editTextTextPassword);
        locationEditText = findViewById(R.id.editTextText3);
        signUpButton = findViewById(R.id.signupButton);
        btn = findViewById(R.id.AlreadyHaveAccount);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SignUpActivity.this, LoginActivity.class));
            }
        });

        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signUpUser();
            }
        });
    }


    private void signUpUser() {
        String name = nameEditText.getText().toString();
        String email = emailEditText.getText().toString();
        String password = passwordEditText.getText().toString();
        String location = locationEditText.getText().toString();
        if(name.isEmpty() || name.length()<7)
        {
            showError(nameEditText,"Your username is not valid!\nLength should be 7 or more");
        }
        else if(email.isEmpty() || !email.contains("@"))
        {
            showError(emailEditText,"Email is not valid");
        }
        else  if (password.isEmpty() || password.length() < 7 || !isValidPassword(password)) {
            showError(passwordEditText, "Password must be at least 7 characters and contain at least one uppercase letter, one lowercase letter, and one digit.");
        }

        else
        {
            databaseReference.child("users").addListenerForSingleValueEvent((new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if(snapshot.hasChild(name)) {
                        Toast.makeText(SignUpActivity.this,"User already exist",Toast.LENGTH_SHORT).show();
                    }else {
                        databaseReference.child("users").child(name).child("Name").setValue(name);
                        databaseReference.child("users").child(name).child("Email").setValue(email);
                        databaseReference.child("users").child(name).child("Password").setValue(password);
                        databaseReference.child("users").child(name).child("Location").setValue(location);

                        Toast.makeText(SignUpActivity.this,"Registration successful",Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(SignUpActivity.this, LoginActivity.class);
                        intent.putExtra("userName", name);
                        startActivity(intent);
                        finish();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            }));

        }


    }
    private boolean isValidPassword(String password) {
        // Use regular expression to enforce constraints
        String passwordPattern = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).+$";
        return password.matches(passwordPattern);
    }

    private void showError(EditText input, String s) {
        input.setError(s);
        input.requestFocus();
}
}

