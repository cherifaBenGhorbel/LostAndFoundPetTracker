package com.example.lostandfoundpettracker.fragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.lostandfoundpettracker.R;
import com.example.lostandfoundpettracker.auth.Login;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ProfileFragment extends Fragment {

    // Firebase database reference
    private DatabaseReference databaseReference;

    // UI elements
    private TextView textViewProfileName, textViewProfileEmail;
    private Button buttonLogout;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        // Initialize Firebase database reference
        databaseReference = FirebaseDatabase.getInstance().getReference("users");

        // Initialize UI elements
        textViewProfileName = view.findViewById(R.id.textViewProfileName);
        textViewProfileEmail = view.findViewById(R.id.textViewProfileEmail);
        buttonLogout = view.findViewById(R.id.buttonlogout);

        // Load user data from Firebase
        loadUserProfile();

        buttonLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                // Clear user data from SharedPreferences
                SharedPreferences sharedPreferences = getActivity().getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.remove("userEmail");
                editor.apply();

                // Navigate to the Login Activity
                Intent intent = new Intent(getContext(), Login.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }});
        return view;
    }

    private void loadUserProfile() {
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);
        String currentUserEmail = sharedPreferences.getString("userEmail", null);

        if (currentUserEmail == null) {
            Log.d("FirebaseDebug", "User email not found in SharedPreferences");
            Toast.makeText(getContext(), "User email not found. Please log in again.", Toast.LENGTH_SHORT).show();
            return;
        }

        String sanitizedEmail = currentUserEmail.replace(".", ",");
        Log.d("FirebaseDebug", "Sanitized email: " + sanitizedEmail);

        databaseReference.child("users").child(sanitizedEmail)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            Log.d("FirebaseDebug", "User data found: " + snapshot.getValue());
                            String email = snapshot.child("email").getValue(String.class);
                            String username = snapshot.child("username").getValue(String.class);

                            // Update UI
                            textViewProfileEmail.setText(email != null ? email : "No Email");
                            textViewProfileName.setText(username != null ? username : "No Username");
                        } else {
                            Log.d("FirebaseDebug", "User not found in database.");
                            textViewProfileEmail.setText("username@gmail.com");
                            textViewProfileName.setText("Username");
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Log.e("FirebaseError", "Database error: " + error.getMessage());
                        Toast.makeText(getContext(), "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

}
