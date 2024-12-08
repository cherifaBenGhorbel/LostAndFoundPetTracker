package com.example.lostandfoundpettracker.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.lostandfoundpettracker.R;
import com.example.lostandfoundpettracker.model.PetReport;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Locale;

public class MapFragment extends Fragment {

    private WebView webView;
    private ProgressBar progressBar;
    private DatabaseReference databaseReference;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_map, container, false);

        // Initialize WebView and ProgressBar
        webView = view.findViewById(R.id.mapWebView);
        progressBar = view.findViewById(R.id.progress_bar);

        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setDomStorageEnabled(true);
        webView.setWebViewClient(new WebViewClient());

        // Load Leaflet map HTML file
        webView.loadUrl("file:///android_asset/leaflet_map.html");

        // Initialize Firebase
        databaseReference = FirebaseDatabase.getInstance().getReference("reports");

        // Wait for WebView to load before injecting data
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                loadPetReports();
            }
        });

        return view;
    }

    private void loadPetReports() {
        progressBar.setVisibility(View.VISIBLE);

        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (!dataSnapshot.exists()) {
                    Toast.makeText(getContext(), "No reports found.", Toast.LENGTH_SHORT).show();
                    progressBar.setVisibility(View.GONE);
                    return;
                }

                for (DataSnapshot reportSnapshot : dataSnapshot.getChildren()) {
                    PetReport report = reportSnapshot.getValue(PetReport.class);
                    if (report != null) {
                        // Check if valid latitude and longitude
                        if (report.getLatitude() != 0 && report.getLongitude() != 0) {
                            // Send data to JavaScript
                            String jsCommand = String.format(
                                    Locale.US,
                                    "addMarker(%f, %f, '%s');",
                                    report.getLatitude(),
                                    report.getLongitude(),
                                    report.getPetName() + " (" + report.getPetType() + ")"
                            );
                            webView.evaluateJavascript(jsCommand, null);
                        }
                    }
                }

                progressBar.setVisibility(View.GONE);
            }


            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getContext(), "Error loading reports: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                progressBar.setVisibility(View.GONE);
            }
        });
    }
}
