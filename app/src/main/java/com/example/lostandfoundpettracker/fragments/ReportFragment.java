package com.example.lostandfoundpettracker.fragments;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.example.lostandfoundpettracker.R;
import com.example.lostandfoundpettracker.model.PetReport;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.UUID;

public class ReportFragment extends Fragment {

    private static final int PICK_IMAGE_REQUEST = 1;
    private static final int STORAGE_PERMISSION_CODE = 2;

    private TextInputEditText petNameEditText, locationEditText, descriptionEditText, petTypeEditText, colorEditText;
    private RadioGroup statusRadioGroup;
    private MaterialButton submitButton, uploadImageButton;
    private ImageView petImageView;
    private DatabaseReference mDatabase;
    private StorageReference mStorageRef;
    private Uri imageUri;
    private String reportType;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_report, container, false);

        petNameEditText = view.findViewById(R.id.petNameEditText);
        locationEditText = view.findViewById(R.id.locationEditText);
        descriptionEditText = view.findViewById(R.id.descriptionEditText);
        petTypeEditText = view.findViewById(R.id.petTypeEditText);
        colorEditText = view.findViewById(R.id.colorEditText);
        statusRadioGroup = view.findViewById(R.id.statusRadioGroup);
        submitButton = view.findViewById(R.id.submitButton);
        uploadImageButton = view.findViewById(R.id.uploadImageButton);
        petImageView = view.findViewById(R.id.petImageView);

        mDatabase = FirebaseDatabase.getInstance().getReference("reports");
        mStorageRef = FirebaseStorage.getInstance().getReference("pet_images");

        reportType = getArguments() != null ? getArguments().getString("reportType", "lost") : "lost";
        statusRadioGroup.check(reportType.equals("lost") ? R.id.radioLost : R.id.radioSaw);

        submitButton.setOnClickListener(v -> submitReport());
        uploadImageButton.setOnClickListener(v -> openFileChooser());

        return view;
    }



    private void openFileChooser() {
        Toast.makeText(getContext(), "Opening file chooser", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == -1 && data != null && data.getData() != null) {
            imageUri = data.getData();
            Toast.makeText(getContext(), "Image Selected: " + imageUri.toString(), Toast.LENGTH_SHORT).show();
            petImageView.setImageURI(imageUri);
        }
    }


    private void uploadImage(String petName, String location, String description, String petType, String color, String status) {
        if (imageUri == null) {
            // This check avoids unnecessary attempts to upload.
            Toast.makeText(getContext(), "No image selected", Toast.LENGTH_SHORT).show();
            return;
        }

        String fileName = UUID.randomUUID().toString() + "." + getFileExtension(imageUri);
        StorageReference fileReference = mStorageRef.child(fileName);

        fileReference.putFile(imageUri)
                .addOnSuccessListener(taskSnapshot -> fileReference.getDownloadUrl()
                        .addOnSuccessListener(uri -> {
                            String imageUrl = uri.toString();
                            // Save report with image URL after successful upload
                            saveReport(petName, location, description, petType, color, status, imageUrl);
                        })
                        .addOnFailureListener(e -> {
                            // Save report without an image URL if URL retrieval fails
                            Toast.makeText(getContext(), "Error retrieving image URL: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            saveReport(petName, location, description, petType, color, status, "");
                        }))
                .addOnFailureListener(e -> {
                    // Save report without an image if upload fails
                    Toast.makeText(getContext(), "Image upload failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    saveReport(petName, location, description, petType, color, status, "");
                });
    }

    private void submitReport() {
        String petName = petNameEditText.getText().toString().trim();
        String location = locationEditText.getText().toString().trim();
        String description = descriptionEditText.getText().toString().trim();
        String petType = petTypeEditText.getText().toString().trim();
        String color = colorEditText.getText().toString().trim();
        String status = statusRadioGroup.getCheckedRadioButtonId() == R.id.radioLost ? "lost" : "saw";

        if (petName.isEmpty() || location.isEmpty() || description.isEmpty() || petType.isEmpty() || color.isEmpty()) {
            Toast.makeText(getContext(), "Please fill in all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        if (imageUri != null) {
            // Upload image and save report
            uploadImage(petName, location, description, petType, color, status);
        } else {
            // Save report without image
            saveReport(petName, location, description, petType, color, status, "");
        }
    }


    private void saveReport(String petName, String location, String description, String petType, String color, String status, String imageUrl) {
        String reportId = mDatabase.push().getKey();
        if (reportId == null) {
            Toast.makeText(getContext(), "Error generating report ID", Toast.LENGTH_SHORT).show();
            return;
        }
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);
        String currentUserEmail = sharedPreferences.getString("userEmail", null);

        PetReport report = new PetReport(reportId, status, petName, location, System.currentTimeMillis(),
                description, petType, color, 0, 0, imageUrl, currentUserEmail);

        mDatabase.child(reportId).setValue(report)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(getContext(), "Report submitted successfully", Toast.LENGTH_SHORT).show();
                    clearForm();
                })
                .addOnFailureListener(e -> Toast.makeText(getContext(), "Error submitting report: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }

    private String getFileExtension(Uri uri) {
        if (getContext() == null || getContext().getContentResolver() == null) {
            return "";
        }
        return MimeTypeMap.getSingleton().getExtensionFromMimeType(getContext().getContentResolver().getType(uri));
    }

    private void clearForm() {
        petNameEditText.setText("");
        locationEditText.setText("");
        descriptionEditText.setText("");
        petTypeEditText.setText("");
        colorEditText.setText("");
        statusRadioGroup.clearCheck();
        petImageView.setImageResource(android.R.color.transparent);
        imageUri = null;
    }
}

