package com.example.lostandfoundpettracker.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.lostandfoundpettracker.R;
import com.example.lostandfoundpettracker.model.PetReport;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

public class SawItFragment extends Fragment {

    private static final String ARG_PET_REPORT = "pet_report";

    private PetReport petReport;
    private EditText locationEditText;
    private EditText descriptionEditText;
    private Button submitButton;
    private DatabaseReference mDatabase;

    public static SawItFragment newInstance(PetReport petReport) {
        SawItFragment fragment = new SawItFragment();
        Bundle args = new Bundle();
        args.putParcelable(ARG_PET_REPORT, petReport);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            petReport = getArguments().getParcelable(ARG_PET_REPORT);
        }
        mDatabase = FirebaseDatabase.getInstance().getReference();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_saw_it, container, false);

        locationEditText = view.findViewById(R.id.locationEditText);
        descriptionEditText = view.findViewById(R.id.descriptionEditText);
        submitButton = view.findViewById(R.id.submitButton);

        submitButton.setOnClickListener(v -> submitSighting());

        return view;
    }

    private void submitSighting() {
        String location = locationEditText.getText().toString().trim();
        String description = descriptionEditText.getText().toString().trim();

        if (location.isEmpty() || description.isEmpty()) {
            Toast.makeText(getContext(), "Please fill in all fields", Toast.LENGTH_SHORT).show();
            return;
        }


        if (petReport == null || petReport.getId() == null) {
            Toast.makeText(getContext(), "Invalid pet report data", Toast.LENGTH_SHORT).show();
            return;
        }
        String sightingId = mDatabase.child("sightings").push().getKey();

        if (sightingId == null) {
            Toast.makeText(getContext(), "Failed to generate sighting ID", Toast.LENGTH_SHORT).show();
            return;
        }
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);
        String currentUserEmail = sharedPreferences.getString("userEmail", null);
        Map<String, Object> sightingData = new HashMap<>();
        sightingData.put("reportId", petReport.getId());
        sightingData.put("location", location);
        sightingData.put("description", description);
        sightingData.put("timestamp", System.currentTimeMillis());
        sightingData.put("reporterId", currentUserEmail);

        mDatabase.child("sightings").child(sightingId).setValue(sightingData)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(getContext(), "Sighting reported successfully", Toast.LENGTH_SHORT).show();
                    sendNotificationToOwner();
                    navigateToHome();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(getContext(), "Failed to report sighting: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    e.printStackTrace(); // Log the error for debugging
                });
    }

    private void sendNotificationToOwner() {
        Toast.makeText(getContext(), "Notification sent to the owner", Toast.LENGTH_SHORT).show();
    }

    private void navigateToHome() {
        if (getParentFragmentManager() != null) {
            getParentFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, new HomeFragment())
                    .commit();
        } else {
            Toast.makeText(getContext(), "Navigation failed", Toast.LENGTH_SHORT).show();
        }
    }
}

