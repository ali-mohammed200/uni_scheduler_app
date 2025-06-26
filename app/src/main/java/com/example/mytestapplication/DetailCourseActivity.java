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
import com.example.mytestapplication.database.AssessmentDAO;
import com.example.mytestapplication.database.CourseDAO;
import com.example.mytestapplication.models.Assessment;
import com.example.mytestapplication.models.Course;

import java.util.List;

public class DetailCourseActivity extends AppCompatActivity {

    private TextView courseTitleView, startDateView, endDateView, statusView,
            instructorNameView, instructorPhoneView, instructorEmailView;
    private RecyclerView recyclerView;
    private AssessmentAdapter adapter;
    private Course course;
    private ActivityResultLauncher<Intent> addAssessmentLauncher;

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
        int courseId = intent.getIntExtra("courseId", -1);
        Log.d("DetailCourseActivity", "Intent Course Id " + courseId);
        if (courseId != -1) {
            CourseDAO dao = new CourseDAO(this);
            course = dao.getCourseById(courseId);
        }

        courseTitleView.setText("Title: " + course.getTitle());
        startDateView.setText("Start: " + course.getStartDate());
        endDateView.setText("End: " + course.getEndDate());
        statusView.setText("Status: " + course.getStatus());
        instructorNameView.setText("Instructor: " + course.getInstructorName());
        instructorPhoneView.setText("Phone: " + course.getInstructorPhone());
        instructorEmailView.setText("Email: " + course.getInstructorEmail());

        loadAssessments();

        addAssessmentLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK) {
                        // TODO: Move to a method like in detailtermactivity
                        AssessmentDAO updatedDao = new AssessmentDAO(this);
                        List<Assessment> updatedAssessments = updatedDao.getAssessmentsByCourseId(course.getId());
                        adapter.setAssessments(updatedAssessments);
                        adapter.notifyDataSetChanged();
                    }
                }
        );
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        loadAssessments();
    }

//    TODO: Fix data cycle issues as you navigate screens.
//    TODO: This behavior will need to be added to all relevant flows
//    TODO: Figure out how to remove infinite navigation loop
    private void loadAssessments() {
        recyclerView = findViewById(R.id.recyclerViewAssessments);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        AssessmentDAO dao = new AssessmentDAO(this);
        List<Assessment> assessments = dao.getAssessmentsByCourseId(course.getId());
        adapter = new AssessmentAdapter(assessments);
        recyclerView.setAdapter(adapter);
    }


    public void showDetailAssessment(View view) {
        Assessment clickedAssessment = (Assessment) view.getTag(); // Retrieve the course from the adapterAdd commentMore actions
        Intent intent = new Intent(this, DetailAssessmentActivity.class);
        intent.putExtra("assessment", clickedAssessment);
        startActivity(intent);
    }

    public void addAssessment(View view) {
        Intent addIntent = new Intent(this, AddAssessmentActivity.class);
        addIntent.putExtra("course", course);
        addAssessmentLauncher.launch(addIntent);
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
