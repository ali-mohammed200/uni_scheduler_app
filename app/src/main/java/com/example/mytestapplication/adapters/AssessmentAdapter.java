package com.example.mytestapplication.adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mytestapplication.AddAssessmentActivity;
import com.example.mytestapplication.R;
import com.example.mytestapplication.database.AssessmentDAO;
import com.example.mytestapplication.models.Assessment;
import com.example.mytestapplication.models.Course;

import java.util.List;

public class AssessmentAdapter extends RecyclerView.Adapter<AssessmentAdapter.AssessmentViewHolder> {

    private List<Assessment> assessments;
    private OnAssessmentDeletedListener deletedListener;

    public AssessmentAdapter(List<Assessment> assessments, OnAssessmentDeletedListener listener) {
        this.assessments = assessments;
        this.deletedListener = listener;
    }

    public interface OnAssessmentDeletedListener {
        void onAssessmentDeleted(int assessmentId);
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
        holder.itemView.setTag(assessment);

        holder.itemView.setOnLongClickListener(v -> {
            showPopupMenu(v, assessment);
            return true;
        });
    }

    private void showPopupMenu(View anchorView, Assessment assessment) {
        PopupMenu popupMenu = new PopupMenu(anchorView.getContext(), anchorView);
        popupMenu.getMenuInflater().inflate(R.menu.menu_card_options, popupMenu.getMenu());
        popupMenu.setOnMenuItemClickListener(item -> {
            if (item.getItemId() == R.id.menu_edit) {
                openEditForm(anchorView.getContext(), assessment);
                return true;
            } else if (item.getItemId() == R.id.menu_delete) {
                confirmAndDelete(anchorView.getContext(), assessment);
                return true;
            } else {
                return false;
            }
        });
        popupMenu.show();
    }

    private void confirmAndDelete(Context context, Assessment assessment) {
        new AlertDialog.Builder(context)
                .setTitle("Delete Assessment")
                .setMessage("Are you sure you want to delete this assessment?")
                .setPositiveButton("Delete", (dialog, which) -> {
                    // Delete from DB
                    AssessmentDAO dao = new AssessmentDAO(context);
                    dao.deleteAssessment(assessment.getId());

                    // Update adapter
                    assessments.remove(assessment);
                    notifyDataSetChanged();

                    // Notify the Activity
                    if (deletedListener != null) {
                        deletedListener.onAssessmentDeleted(assessment.getId());
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }


    private void openEditForm(Context context, Assessment assessment) {
        Intent intent = new Intent(context, AddAssessmentActivity.class);
        intent.putExtra("editMode", true);
        intent.putExtra("assessment", assessment);
        context.startActivity(intent);
    }


    @Override
    public int getItemCount() {
        return assessments.size();
    }

    public void setAssessments(List<Assessment> newAssessments) {
        this.assessments = newAssessments;
    }
}
