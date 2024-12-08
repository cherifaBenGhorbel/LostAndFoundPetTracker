package com.example.lostandfoundpettracker.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.lostandfoundpettracker.R;
import com.example.lostandfoundpettracker.model.PetReport;

import java.util.ArrayList;
import java.util.List;

public class RecentReportsAdapter extends RecyclerView.Adapter<RecentReportsAdapter.ReportViewHolder> {

    private List<PetReport> reports = new ArrayList<>();
    private OnReportClickListener listener;

    public RecentReportsAdapter(OnReportClickListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public ReportViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_recent_report, parent, false);
        return new ReportViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ReportViewHolder holder, int position) {
        PetReport report = reports.get(position);
        holder.bind(report);
    }

    @Override
    public int getItemCount() {
        return reports.size();
    }

    public void setReports(List<PetReport> reports) {
        this.reports = reports;
        notifyDataSetChanged();
    }

    class ReportViewHolder extends RecyclerView.ViewHolder {
        TextView statusTextView;
        TextView petNameTextView;
        TextView locationTextView;
        TextView timestampTextView;

        ReportViewHolder(@NonNull View itemView) {
            super(itemView);
            statusTextView = itemView.findViewById(R.id.statusTextView);
            petNameTextView = itemView.findViewById(R.id.petNameTextView);
            locationTextView = itemView.findViewById(R.id.locationTextView);
            timestampTextView = itemView.findViewById(R.id.timestampTextView);

            itemView.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    listener.onReportClick(reports.get(position));
                }
            });
        }

        void bind(PetReport report) {
            statusTextView.setText(report.getStatus());
            petNameTextView.setText(report.getPetName());
            locationTextView.setText(report.getLocation());
            timestampTextView.setText(report.getFormattedTimestamp());
        }
    }

    public interface OnReportClickListener {
        void onReportClick(PetReport report);
    }
}

