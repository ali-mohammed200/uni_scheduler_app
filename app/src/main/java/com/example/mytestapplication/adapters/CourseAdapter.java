package com.example.mytestapplication.adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.mytestapplication.AddAssessmentActivity;
import com.example.mytestapplication.AddCourseActivity;
import com.example.mytestapplication.R;
import com.example.mytestapplication.database.AssessmentDAO;
import com.example.mytestapplication.database.CourseDAO;
import com.example.mytestapplication.models.Assessment;
import com.example.mytestapplication.models.Course;

import java.util.List;

public class CourseAdapter extends RecyclerView.Adapter<CourseAdapter.CourseViewHolder> {

    private List<Course> courses;
    private OnCourseDeletedListener deletedListener;

    public CourseAdapter(List<Course> courses, OnCourseDeletedListener listener) {
        this.courses = courses;
        this.deletedListener = listener;
    }

    public interface OnCourseDeletedListener {
        void onCourseDeleted(int courseId);
    }

    public static class CourseViewHolder extends RecyclerView.ViewHolder {
        TextView titleTextView;
        TextView instructorTextView;

        public CourseViewHolder(View itemView) {
            super(itemView);
            titleTextView = itemView.findViewById(R.id.courseTitle);
            instructorTextView = itemView.findViewById(R.id.courseInstructor);
        }
    }

    @Override
    public CourseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_course, parent, false);
        return new CourseViewHolder(view);
    }

    @Override
    public void onBindViewHolder(CourseViewHolder holder, int position) {
        Course course = courses.get(position);
        holder.titleTextView.setText(course.getTitle());
        holder.instructorTextView.setText(course.getInstructorName());
        holder.itemView.setTag(course);

        holder.itemView.setOnLongClickListener(v -> {
            showPopupMenu(v, course);
            return true;
        });
    }

    private void showPopupMenu(View anchorView, Course course) {
        PopupMenu popupMenu = new PopupMenu(anchorView.getContext(), anchorView);
        popupMenu.getMenuInflater().inflate(R.menu.menu_card_options, popupMenu.getMenu());
        popupMenu.setOnMenuItemClickListener(item -> {
            if (item.getItemId() == R.id.menu_edit) {
                openEditForm(anchorView.getContext(), course);
                return true;
            } else if (item.getItemId() == R.id.menu_delete) {
                confirmAndDelete(anchorView.getContext(), course);
                return true;
            } else {
                return false;
            }
        });
        popupMenu.show();
    }

    private void confirmAndDelete(Context context, Course course) {
        new AlertDialog.Builder(context)
                .setTitle("Delete Course")
                .setMessage("Are you sure you want to delete this course? Notes & Assessments will also be deleted")
                .setPositiveButton("Delete", (dialog, which) -> {
                    // Delete from DB
                    CourseDAO dao = new CourseDAO(context);
                    AssessmentDAO a_dao = new AssessmentDAO(context);
                    a_dao.deleteAssessmentByCourseId(course.getId());
                    dao.deleteCourse(course.getId());

                    // Update adapter
                    courses.remove(course);
                    notifyDataSetChanged();
                    // Notify the Activity
                    Log.d("CourseAdapter", "in 1 deletedCourseId: " + course.getId());
                    if (deletedListener != null) {
                        Log.d("CourseAdapter", "in 2 deletedCourseId: " + course.getId());
                        deletedListener.onCourseDeleted(course.getId());
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }


    private void openEditForm(Context context, Course course) {
        Intent intent = new Intent(context, AddCourseActivity.class);
        intent.putExtra("editMode", true);
        intent.putExtra("course", course);
        context.startActivity(intent);
    }

    @Override
    public int getItemCount() {
        return courses.size();
    }

    public void setCourses(List<Course> newCourses) {
        this.courses = newCourses;
    }
}
