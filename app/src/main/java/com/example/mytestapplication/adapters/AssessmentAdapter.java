package com.example.mytestapplication.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mytestapplication.R;
import com.example.mytestapplication.models.Assessment;

import java.util.List;

public class AssessmentAdapter extends RecyclerView.Adapter<AssessmentAdapter.AssessmentViewHolder> {

    private List<Assessment> assessments;

    public AssessmentAdapter(List<Assessment> assessments) {
        this.assessments = assessments;
    }

    public static class AssessmentViewHolder extends RecyclerView.ViewHolder {
        TextView assessmentTitle;
        TextView assessmentType;
        TextView assessmentDates;

        public AssessmentViewHolder(@NonNull View itemView) {
            super(itemView);
            assessmentTitle = itemView.findViewById(R.id.assessmentTitle);
            assessmentType = itemView.findViewById(R.id.assessmentType);
            assessmentDates = itemView.findViewById(R.id.assessmentDates);
        }
    }

    @NonNull
    @Override
    public AssessmentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.card_assessment, parent, false);
        return new AssessmentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AssessmentViewHolder holder, int position) {
        Assessment assessment = assessments.get(position);
        holder.assessmentTitle.setText(assessment.getTitle());
        holder.assessmentType.setText("Type: " + assessment.getType());
        holder.assessmentDates.setText("Due: " + assessment.getEndDate());
    }

    @Override
    public int getItemCount() {
        return assessments.size();
    }
}
