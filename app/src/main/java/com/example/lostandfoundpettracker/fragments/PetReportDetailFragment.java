package com.example.lostandfoundpettracker.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.example.lostandfoundpettracker.R;
import com.example.lostandfoundpettracker.model.PetReport;

public class PetReportDetailFragment extends Fragment {

    private static final String ARG_PET_REPORT = "pet_report";

    private PetReport petReport;

    public static PetReportDetailFragment newInstance(PetReport petReport) {
        PetReportDetailFragment fragment = new PetReportDetailFragment();
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
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_pet_report_detail, container, false);

        ImageView petImageView = view.findViewById(R.id.petImageView);
        TextView statusTextView = view.findViewById(R.id.statusTextView);
        TextView petNameTextView = view.findViewById(R.id.petNameTextView);
        TextView locationTextView = view.findViewById(R.id.locationTextView);
        TextView descriptionTextView = view.findViewById(R.id.descriptionTextView);
        TextView timestampTextView = view.findViewById(R.id.timestampTextView);
        Button sawItButton = view.findViewById(R.id.sawItButton);

        if (petReport != null) {
            Glide.with(this).load(petReport.getImageUrl()).placeholder(R.drawable.rounded_image_background).into(petImageView);
            statusTextView.setText(petReport.getStatus());
            petNameTextView.setText(petReport.getPetName());
            locationTextView.setText(petReport.getLocation());
            descriptionTextView.setText(petReport.getDescription());
            timestampTextView.setText(petReport.getFormattedTimestamp());

            if ("lost".equals(petReport.getStatus())) {
                sawItButton.setVisibility(View.VISIBLE);
                statusTextView.setTextColor(getResources().getColor(R.color.status_lost));
                sawItButton.setOnClickListener(v -> navigateToSawItFragment());
            } else {
                statusTextView.setTextColor(getResources().getColor(R.color.status_pending));
                sawItButton.setVisibility(View.GONE);
            }
        }

        return view;
    }

    private void navigateToSawItFragment() {
        SawItFragment sawItFragment = SawItFragment.newInstance(petReport);
        getParentFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, sawItFragment)
                .addToBackStack(null)
                .commit();
    }
}

