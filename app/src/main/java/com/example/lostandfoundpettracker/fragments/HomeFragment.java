package com.example.lostandfoundpettracker.fragments;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.lostandfoundpettracker.R;
import com.example.lostandfoundpettracker.adapter.RecentReportsAdapter;
import com.example.lostandfoundpettracker.model.PetReport;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment implements RecentReportsAdapter.OnReportClickListener {

    private TextInputEditText searchEditText;
    private MaterialButton reportLostButton;
    private MaterialButton reportFoundButton;
    private RecyclerView recentReportsRecyclerView;
    private RecentReportsAdapter recentReportsAdapter;
    private DatabaseReference mDatabase;
    private SwipeRefreshLayout swipeRefreshLayout;
    private List<PetReport> allReports = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        searchEditText = view.findViewById(R.id.searchEditText);
        reportLostButton = view.findViewById(R.id.reportLostButton);
        reportFoundButton = view.findViewById(R.id.reportFoundButton);
        recentReportsRecyclerView = view.findViewById(R.id.recentReportsRecyclerView);
        swipeRefreshLayout = view.findViewById(R.id.swipeRefreshLayout);

        mDatabase = FirebaseDatabase.getInstance("https://lostandfoundpettracker-6861a-default-rtdb.firebaseio.com/").getReference();

        setupRecyclerView();
        setupButtonListeners();
        setupSwipeRefresh();
        setupSearchBar();
        loadRecentReports();

        return view;
    }

    private void setupRecyclerView() {
        recentReportsAdapter = new RecentReportsAdapter(this);
        recentReportsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recentReportsRecyclerView.setAdapter(recentReportsAdapter);
    }

    private void setupButtonListeners() {
        reportLostButton.setOnClickListener(v -> {
            getParentFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, new ReportFragment())
                    .commit();
        });

        reportFoundButton.setOnClickListener(v -> {
            getParentFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, new ReportFragment())
                    .commit();
        });
    }

    private void setupSwipeRefresh() {
        swipeRefreshLayout.setOnRefreshListener(this::loadRecentReports);
    }

    private void setupSearchBar() {
        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                filterReports(s.toString());
            }
        });
    }

    private void loadRecentReports() {
        mDatabase.child("reports").orderByChild("timestamp").limitToLast(50).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                allReports.clear();
                for (DataSnapshot reportSnapshot : dataSnapshot.getChildren()) {
                    PetReport report = reportSnapshot.getValue(PetReport.class);
                    if (report != null) {
                        allReports.add(0, report); // Add to the beginning of the list for reverse chronological order
                    }
                }
                recentReportsAdapter.setReports(allReports);
                swipeRefreshLayout.setRefreshing(false);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getContext(), "Error loading recent reports: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                swipeRefreshLayout.setRefreshing(false);
            }
        });
    }

    private void filterReports(String query) {
        List<PetReport> filteredReports = new ArrayList<>();
        for (PetReport report : allReports) {
            if (report.getPetName().toLowerCase().contains(query.toLowerCase()) ||
                    report.getPetType().toLowerCase().contains(query.toLowerCase()) ||
                    report.getColor().toLowerCase().contains(query.toLowerCase()) ||
                    report.getLocation().toLowerCase().contains(query.toLowerCase())) {
                filteredReports.add(report);
            }
        }
        recentReportsAdapter.setReports(filteredReports);
    }

    @Override
    public void onReportClick(PetReport report) {
        // Navigate to detail view
        PetReportDetailFragment detailFragment = PetReportDetailFragment.newInstance(report);
        getParentFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, detailFragment)
                .addToBackStack(null)
                .commit();
    }
}

