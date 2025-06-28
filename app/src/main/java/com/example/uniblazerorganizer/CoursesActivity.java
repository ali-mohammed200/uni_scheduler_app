package com.example.uniblazerorganizer;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.uniblazerorganizer.adapters.CourseAdapter;
import com.example.uniblazerorganizer.database.CourseDAO;
import com.example.uniblazerorganizer.models.Course;

import java.util.List;

public class CoursesActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private CourseAdapter adapter;
    private ActivityResultLauncher<Intent> activityResultLauncher;
    private CourseDAO dao;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_courses);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true); // Show Up button
        getSupportActionBar().setTitle("Courses");

        loadCourses();

        activityResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
            if (result.getResultCode() == RESULT_OK) {
                // Reload updated data
                List<Course> updatedCourses = dao.getAllCourses();
                adapter.setCourses(updatedCourses);
                adapter.notifyDataSetChanged();
            }
        });
    }

    public void addCourse(View view) {
        Intent intent = new Intent(CoursesActivity.this, AddCourseActivity.class);
        activityResultLauncher.launch(intent);
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        loadCourses();
    }

    private void loadCourses() {
        recyclerView = findViewById(R.id.recyclerViewCourses);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        dao = new CourseDAO(this);
        List<Course> courses = dao.getAllCourses();
        adapter = new CourseAdapter(courses, deletedId -> {
            Intent resultIntent = new Intent();
            resultIntent.putExtra("deletedCourseId", deletedId);
            Log.d("CourseActivity", "in deletedCourseId: " + deletedId);
            setResult(RESULT_OK, resultIntent);
        });
        recyclerView.setAdapter(adapter);
    }

    public void showDetailCourse(View view) {
        Course clickedCourse = (Course) view.getTag(); // Retrieve the course from the adapter
        Intent intent = new Intent(this, DetailCourseActivity.class);
        intent.putExtra("course", clickedCourse);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        activityResultLauncher.launch(intent);
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}