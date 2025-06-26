package com.example.mytestapplication;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mytestapplication.adapters.AssessmentAdapter;
import com.example.mytestapplication.adapters.CourseAdapter;
import com.example.mytestapplication.database.AssessmentDAO;
import com.example.mytestapplication.database.CourseDAO;
import com.example.mytestapplication.models.Assessment;
import com.example.mytestapplication.models.Course;

import java.util.Collections;
import java.util.List;

public class DetailAssessmentActivity extends AppCompatActivity {

    private TextView textAssessmentTitle, assessmentDates, assessmentType,
            courseTitle, courseInstructor;
    private Assessment assessment;
    private RecyclerView recyclerView;
    private CourseAdapter adapter;
    private int courseId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_assessment);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Assessment Details");

        assessmentDates = findViewById(R.id.assessmentDates);
        textAssessmentTitle = findViewById(R.id.textAssessmentTitle);
        assessmentType = findViewById(R.id.assessmentType);
        recyclerView = findViewById(R.id.recyclerViewTermCourses);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

////     Get course object passed in
        Intent intent = getIntent();
        assessment = (Assessment) intent.getSerializableExtra("assessment");

        textAssessmentTitle.setText("Title: " + assessment.getTitle());
        assessmentDates.setText(assessment.getStartDate() + " - " + assessment.getEndDate());
        assessmentType.setText("Type: " + assessment.getType());

        courseId = assessment.getCourseId();
        loadCourse();
    }


    public void showDetailCourse(View view) {
        Course clickedCourse = (Course) view.getTag(); // Retrieve the course from the adapter
        Intent intent = new Intent(this, DetailCourseActivity.class);
        intent.putExtra("course", clickedCourse);
        startActivity(intent);
    }

    private void loadCourse() {
        CourseDAO dao = new CourseDAO(this);
        Course course = dao.getCourseById(courseId);

        adapter = new CourseAdapter(Collections.singletonList(course));
        recyclerView.setAdapter(adapter);
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}