package com.example.mytestapplication;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mytestapplication.adapters.AssessmentAdapter;
import com.example.mytestapplication.adapters.CourseAdapter;
import com.example.mytestapplication.database.AssessmentDAO;
import com.example.mytestapplication.database.CourseDAO;
import com.example.mytestapplication.models.Assessment;
import com.example.mytestapplication.models.Course;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class DetailAssessmentActivity extends AppCompatActivity {

    private TextView textAssessmentTitle, assessmentDates, assessmentType,
            courseTitle, courseInstructor;
    private Assessment assessment;
    private RecyclerView recyclerView;
    private CourseAdapter adapter;
    private int courseId;
    private ActivityResultLauncher<Intent> activityResultLauncher;
    private CourseDAO dao;


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

        int assessmentId = intent.getIntExtra("assessmentId", -1);
        Log.d("DetailAssessment", "assessmentId " + assessmentId);
        if (assessmentId != -1) {
            AssessmentDAO assessmentDAO = new AssessmentDAO(this);
            assessment = assessmentDAO.getAssessmentById(assessmentId);
        }

        textAssessmentTitle.setText("Title: " + assessment.getTitle());
        assessmentDates.setText(assessment.getStartDate() + " - " + assessment.getEndDate());
        assessmentType.setText("Type: " + assessment.getType());

        courseId = assessment.getCourseId();
        loadCourse();

        activityResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK) {
                        Intent data = result.getData();
                        if (data != null && data.hasExtra("deletedAssessmentId")) {
                            int deletedId = data.getIntExtra("deletedAssessmentId", -1);
                            if (deletedId != -1) {
                                // Close this screen if it was deleted from the screens on top
                                finish();
                            }
                        }
                    }
                }
        );
    }


    public void showDetailCourse(View view) {
        Course clickedCourse = (Course) view.getTag(); // Retrieve the course from the adapter
        Intent intent = new Intent(this, DetailCourseActivity.class);
        intent.putExtra("course", clickedCourse);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        activityResultLauncher.launch(intent);
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        loadCourse();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            dao.close();
        } catch (NullPointerException e) {
            Log.d("onDestroy", "NullPointerException - DOA empty");
        }
    }

    private void loadCourse() {
        dao = new CourseDAO(this);
        Course course = dao.getCourseById(courseId);

        adapter = new CourseAdapter(new ArrayList<>(List.of(course)));
        recyclerView.setAdapter(adapter);
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}