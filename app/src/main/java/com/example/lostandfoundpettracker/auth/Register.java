package com.example.lostandfoundpettracker.auth;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.lostandfoundpettracker.R;
import com.example.lostandfoundpettracker.model.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Register extends AppCompatActivity {

    // Reference to Firebase Realtime Database
    private final DatabaseReference databaseReference =
            FirebaseDatabase.getInstance().getReferenceFromUrl("https://lostandfoundpettracker-6861a-default-rtdb.firebaseio.com/");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_register);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Initialize UI components
        final EditText username = findViewById(R.id.username);
        final EditText email = findViewById(R.id.email);
        final EditText phone = findViewById(R.id.phone);
        final EditText pass = findViewById(R.id.password);
        final EditText confPass = findViewById(R.id.confPassword);
        final Button registerBtn = findViewById(R.id.registerBtn);
        final TextView loginNowBtn = findViewById(R.id.loginNowbtn);

        // Handle Register button click
        registerBtn.setOnClickListener(v -> {
            // Get user inputs
            final String usernameTxt = username.getText().toString().trim();
            final String emailTxt = email.getText().toString().trim();
            final String phoneTxt = phone.getText().toString().trim();
            final String passTxt = pass.getText().toString();
            final String confPassTxt = confPass.getText().toString();

            // Validate inputs
            if (usernameTxt.isEmpty() || emailTxt.isEmpty() || phoneTxt.isEmpty() || passTxt.isEmpty() || confPassTxt.isEmpty()) {
                Toast.makeText(Register.this, "Please fill all the fields", Toast.LENGTH_SHORT).show();
            } else if (!passTxt.equals(confPassTxt)) {
                Toast.makeText(Register.this, "Passwords do not match", Toast.LENGTH_SHORT).show();
            } else {
                // Check if the email is already registered
                databaseReference.child("users").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        String sanitizedEmail = emailTxt.replace(".", ","); // Firebase keys cannot contain '.'
                        if (snapshot.child(sanitizedEmail).exists()) {
                            Toast.makeText(Register.this, "Email is already registered. Try another one.", Toast.LENGTH_SHORT).show();
                        } else {
                            // Register the new user
                            User user = new User(usernameTxt, emailTxt, phoneTxt, passTxt);
                            databaseReference.child("users").child(sanitizedEmail).setValue(user)
                                    .addOnSuccessListener(aVoid -> {
                                        Toast.makeText(Register.this, "User registered successfully!", Toast.LENGTH_SHORT).show();
                                        finish(); // Close the activity
                                    })
                                    .addOnFailureListener(e -> Toast.makeText(Register.this, "Registration failed: " + e.getMessage(), Toast.LENGTH_SHORT).show());
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(Register.this, "Database error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
        loginNowBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


    }
}