package com.example.lostandfoundpettracker;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import com.example.lostandfoundpettracker.fragments.HomeFragment;
import com.example.lostandfoundpettracker.fragments.MapFragment;
import com.example.lostandfoundpettracker.fragments.ProfileFragment;

import com.example.lostandfoundpettracker.fragments.ReportFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    private BottomNavigationView bottomNavigationView;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Setup Toolbar
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Initialize bottom navigation
        bottomNavigationView = findViewById(R.id.bottom_navigation);

        // Set default fragment
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, new HomeFragment())
                    .commit();
        }

        // Setup bottom navigation listener
        bottomNavigationView.setOnItemSelectedListener(item -> {
            Fragment selectedFragment = null;

            String title = "Pet Tracker";


            int itemId = item.getItemId();
            if (itemId == R.id.nav_home) {
                selectedFragment = new HomeFragment();
            } else if (itemId == R.id.nav_map) {
                selectedFragment = new MapFragment();
                title = "Pet Map";
            } else if (itemId == R.id.nav_report) {
                selectedFragment = new ReportFragment();
                title = "Report Pet";
            } else if (itemId == R.id.nav_profile) {
                selectedFragment = new ProfileFragment();
                title = "Profile";
            }

            // Update Toolbar title
            toolbar.setTitle(title);

            // Replace the fragment
            if (selectedFragment != null) {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, selectedFragment)
                        .commit();
            }

            return true;
        });
    }
}