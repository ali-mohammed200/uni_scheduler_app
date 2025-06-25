package com.example.mytestapplication.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.mytestapplication.R;
import com.example.mytestapplication.models.Course;

import java.util.List;

public class CourseAdapter extends RecyclerView.Adapter<CourseAdapter.CourseViewHolder> {

    private List<Course> courses;

    public CourseAdapter(List<Course> courses) {
        this.courses = courses;
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
    }

    @Override
    public int getItemCount() {
        return courses.size();
    }

    public void setCourses(List<Course> newCourses) {
        this.courses = newCourses;
    }
}
