package com.example.mytestapplication;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Button;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
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
    private ActivityResultLauncher<Intent> activityResultLauncher;
    private AssessmentDAO dao;

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

        createPlaceHolderTextView();

//         Get course object passed in
        try {
            Intent intent = getIntent();
            course = (Course) intent.getSerializableExtra("course");
            int courseId = intent.getIntExtra("courseId", -1);
            Log.d("DetailCourseActivity", "Intent Course Id " + courseId);
            if (courseId != -1) {
                CourseDAO courseDAO = new CourseDAO(this);
                course = courseDAO.getCourseById(courseId);
            }

            setPageData();

            activityResultLauncher = registerForActivityResult(
                    new ActivityResultContracts.StartActivityForResult(),
                    result -> {
                        if (result.getResultCode() == RESULT_OK) {
                            Intent resultData = result.getData();
                            if(resultData != null) {
                                if (resultData.hasExtra("deletedCourseId")) {
                                    int deletedId = resultData.getIntExtra("deletedCourseId", -1);
                                    if (deletedId != -1) {
                                        // Close this screen if it was deleted from the screens on top
                                        finish();
                                    }
                                } else if (resultData.hasExtra("originator")) {
                                    if (resultData.getStringExtra("originator").equals("AddCourseActivity")){
                                        course = (Course) resultData.getSerializableExtra("course");
                                        boolean fromEdit = resultData.getBooleanExtra("fromEdit", false);
                                        setPageData();
                                        Log.d("DetailAssessmentActivity ResultContract", course + " " + fromEdit);
                                    }
                                }
                            } else {
                                // TODO: Move to a method like in detailtermactivity
                                List<Assessment> updatedAssessments = dao.getAssessmentsByCourseId(course.getId());
                                adapter.setAssessments(updatedAssessments);
                                adapter.notifyDataSetChanged();
                            }
                        }
                    }
            );
        } catch (NullPointerException e) {
            Toast.makeText(this, "Unable to find the deleted object", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private void createPlaceHolderTextView() {
        ConstraintLayout rootLayout = findViewById(R.id.constraintLayoutMain); // your root ConstraintLayout
        Button button = findViewById(R.id.buttonOpenNote);

        TextView backgroundLabel = new TextView(this);
        backgroundLabel.setText("List of Associated Assessments");
        backgroundLabel.setAlpha(0.05f);
        backgroundLabel.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);

        ConstraintLayout.LayoutParams params = new ConstraintLayout.LayoutParams(
                ConstraintLayout.LayoutParams.WRAP_CONTENT,
                ConstraintLayout.LayoutParams.WRAP_CONTENT
        );

        params.topToBottom = button.getId();
        params.bottomToBottom = ConstraintLayout.LayoutParams.PARENT_ID;
        params.startToStart = ConstraintLayout.LayoutParams.PARENT_ID;
        params.endToEnd = ConstraintLayout.LayoutParams.PARENT_ID;

        backgroundLabel.setLayoutParams(params);

        // Insert as the **first view** to make sure it has lowest Z-index
        rootLayout.addView(backgroundLabel, 0);
    }

    private void setPageData() {
        courseTitleView.setText("[" + course.getId() + "] " + "Title: " + course.getTitle());
        startDateView.setText("Start: " + course.getStartDate());
        endDateView.setText("End: " + course.getEndDate());
        statusView.setText("Status: " + course.getStatus());
        instructorNameView.setText("Instructor: " + course.getInstructorName());
        instructorPhoneView.setText("Phone: " + course.getInstructorPhone());
        instructorEmailView.setText("Email: " + course.getInstructorEmail());

        loadAssessments();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        try {
            loadAssessments();
        } catch (NullPointerException e) {
            Toast.makeText(this, "Unable to find the deleted object", Toast.LENGTH_SHORT).show();
            finish();
        }
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

    private void loadAssessments() {
        try {
            recyclerView = findViewById(R.id.recyclerViewAssessments);
            recyclerView.setLayoutManager(new LinearLayoutManager(this));

            dao = new AssessmentDAO(this);
            List<Assessment> assessments = dao.getAssessmentsByCourseId(course.getId());
            adapter = new AssessmentAdapter(assessments, deletedId -> {
                Intent resultIntent = new Intent();
                resultIntent.putExtra("deletedAssessmentId", deletedId);
                setResult(RESULT_OK, resultIntent);
            });
            recyclerView.setAdapter(adapter);
        } catch (NullPointerException e) {
            Toast.makeText(this, "Unable to find the deleted object", Toast.LENGTH_SHORT).show();
            finish();
        }
    }


    public void showDetailAssessment(View view) {
        Assessment clickedAssessment = (Assessment) view.getTag(); // Retrieve the course from the adapterAdd commentMore actions
        Intent intent = new Intent(this, DetailAssessmentActivity.class);
        intent.putExtra("assessment", clickedAssessment);
        activityResultLauncher.launch(intent);
    }

    public void addAssessment(View view) {
        int courseAssessmentsCount = dao.getAssessmentCountByCourseId(course.getId());
        if (courseAssessmentsCount >= 5){
            Toast.makeText(this, "Course already has 5 assessments", Toast.LENGTH_SHORT).show();
        } else {
            Intent addIntent = new Intent(this, AddAssessmentActivity.class);
            addIntent.putExtra("course", course);
            activityResultLauncher.launch(addIntent);
        }
    }

    public void openNote(View view) {
        Intent intent = new Intent(this, NoteActivity.class);
        intent.putExtra("courseId", course.getId());
        activityResultLauncher.launch(intent);
    }

    public void confirmAndDeleteDetailPage(View view) {
        new AlertDialog.Builder(this)
                .setTitle("Delete Course")
                .setMessage("Are you sure you want to delete this course? Notes & Assessments will also be deleted")
                .setPositiveButton("Delete", (dialog, which) -> {
                    // Delete from DB
                    CourseDAO dao = new CourseDAO(this);
                    AssessmentDAO a_dao = new AssessmentDAO(this);
                    a_dao.deleteAssessmentByCourseId(course.getId());
                    dao.deleteCourse(course.getId());

                    Intent resultIntent = new Intent();
                    resultIntent.putExtra("deletedCourseId", course.getId());
                    setResult(RESULT_OK, resultIntent);
                    finish();
                })
                .setNegativeButton("Cancel", null)
                .show();
    }


    public void openEditFormDetailPage(View view) {
        Intent intent = new Intent(this, AddCourseActivity.class);
        intent.putExtra("editMode", true);
        intent.putExtra("course", course);
        activityResultLauncher.launch(intent);
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}
