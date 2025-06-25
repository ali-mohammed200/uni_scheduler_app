package com.example.mytestapplication;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mytestapplication.adapters.AssessmentAdapter;
import com.example.mytestapplication.database.AssessmentDAO;
import com.example.mytestapplication.models.Assessment;
import com.example.mytestapplication.models.Course;

import java.util.List;

public class DetailCourseActivity extends AppCompatActivity {

    private TextView courseTitleView, startDateView, endDateView, statusView,
            instructorNameView, instructorPhoneView, instructorEmailView;
    private RecyclerView recyclerView;
    private AssessmentAdapter adapter;
    private Course course;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_course);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Course Details");

        courseTitleView = findViewById(R.id.textCourseTitle);
        startDateView = findViewById(R.id.textStartDate);
        endDateView = findViewById(R.id.textEndDate);
        statusView = findViewById(R.id.textStatus);
        instructorNameView = findViewById(R.id.textInstructorName);
        instructorPhoneView = findViewById(R.id.textInstructorPhone);
        instructorEmailView = findViewById(R.id.textInstructorEmail);

//         Get course object passed in
        Intent intent = getIntent();
        course = (Course) intent.getSerializableExtra("course");
//        Log.d("TAG", "This is a debug message MOMO");

        courseTitleView.setText("Title: " + course.getTitle());
        startDateView.setText("Start: " + course.getStartDate());
        endDateView.setText("End: " + course.getEndDate());
        statusView.setText("Status: " + course.getStatus());
        instructorNameView.setText("Instructor: " + course.getInstructorName());
        instructorPhoneView.setText("Phone: " + course.getInstructorPhone());
        instructorEmailView.setText("Email: " + course.getInstructorEmail());

        recyclerView = findViewById(R.id.recyclerViewAssessments);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        AssessmentDAO dao = new AssessmentDAO(this);
        List<Assessment> assessments = dao.getAssessmentsByCourseId(course.getId());
        adapter = new AssessmentAdapter(assessments);
        recyclerView.setAdapter(adapter);
    }

    public void addAssessment(View view) {
//        Intent intent = new Intent(this, AddAssessmentActivity.class);
//        intent.putExtra("courseId", course.getId());
//        startActivity(intent);
    }

    public void openNotes(View view) {
//        Intent intent = new Intent(this, NotesActivity.class);
//        intent.putExtra("courseId", course.getId());
//        intent.putExtra("note", course.getNote());
//        startActivity(intent);
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}
