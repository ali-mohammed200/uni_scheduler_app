package com.example.uniblazerorganizer;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.uniblazerorganizer.adapters.CourseAdapter;
import com.example.uniblazerorganizer.database.AssessmentDAO;
import com.example.uniblazerorganizer.database.CourseDAO;
import com.example.uniblazerorganizer.models.Assessment;
import com.example.uniblazerorganizer.models.Course;

import java.util.ArrayList;
import java.util.List;

public class DetailAssessmentActivity extends AppCompatActivity {

    private TextView textAssessmentTitle, assessmentDates, assessmentType, courseTitle, courseInstructor;
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

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        View rootView = findViewById(R.id.root_view); // your root layout
        recyclerView = findViewById(R.id.recyclerViewTermCourses);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Only enable toggle in landscape
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            toolbar.setVisibility(View.GONE);
            rootView.setOnClickListener(v -> {
                if (toolbar.isShown()) {
                    toolbar.setVisibility(View.GONE);
                } else {
                    toolbar.setVisibility(View.VISIBLE);
                }
            });
        }

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Assessment Details");

        createPlaceHolderTextView();

        assessmentDates = findViewById(R.id.assessmentDates);
        textAssessmentTitle = findViewById(R.id.textAssessmentTitle);
        assessmentType = findViewById(R.id.assessmentType);
        recyclerView = findViewById(R.id.recyclerViewTermCourses);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

////     Get course object passed in
        try {
            Intent intent = getIntent();
            assessment = (Assessment) intent.getSerializableExtra("assessment");

            int assessmentId = intent.getIntExtra("assessmentId", -1);
            Log.d("DetailAssessment", "assessmentId " + assessmentId);
            if (assessmentId != -1) {
                AssessmentDAO assessmentDAO = new AssessmentDAO(this);
                assessment = assessmentDAO.getAssessmentById(assessmentId);
            }

            setPageData();

            activityResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
                if (result.getResultCode() == RESULT_OK) {
                    Intent resultData = result.getData();
                    if (resultData != null) {
                        if (resultData.hasExtra("deletedAssessmentId")) {
                            int deletedId = resultData.getIntExtra("deletedAssessmentId", -1);
                            if (deletedId != -1) {
                                // Close this screen if it was deleted from the screens on top
                                finish();
                            }
                        }
                        if (resultData.hasExtra("originator")) {
                            if (resultData.getStringExtra("originator").equals("AddAssessmentActivity")) {
                                assessment = (Assessment) resultData.getSerializableExtra("assessment");
                                boolean fromEdit = resultData.getBooleanExtra("fromEdit", false);
                                setPageData();
                                Log.d("DetailAssessmentActivity ResultContract", assessment + " " + fromEdit);
                            }
                        }
                    }
                }
            });
        } catch (NullPointerException e) {
            Toast.makeText(this, "Unable to find the deleted object", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private void createPlaceHolderTextView() {
        ConstraintLayout rootLayout = findViewById(R.id.constraintLayoutMain); // your root ConstraintLayout

        TextView backgroundLabel = new TextView(this);
        backgroundLabel.setText("Associated Course");
        backgroundLabel.setAlpha(0.05f);
        backgroundLabel.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);

        ConstraintLayout.LayoutParams params = new ConstraintLayout.LayoutParams(ConstraintLayout.LayoutParams.WRAP_CONTENT, ConstraintLayout.LayoutParams.WRAP_CONTENT);

        params.topToTop = ConstraintLayout.LayoutParams.PARENT_ID;
        params.bottomToBottom = ConstraintLayout.LayoutParams.PARENT_ID;
        params.startToStart = ConstraintLayout.LayoutParams.PARENT_ID;
        params.endToEnd = ConstraintLayout.LayoutParams.PARENT_ID;

        backgroundLabel.setLayoutParams(params);

        // Insert as the **first view** to make sure it has lowest Z-index
        rootLayout.addView(backgroundLabel, 0);
    }

    private void setPageData() {
        AssessmentDAO assessmentDAO = new AssessmentDAO(this);
        assessment = assessmentDAO.getAssessmentById(assessment.getId());

        textAssessmentTitle.setText("[" + assessment.getId() + "] " + "Title: " + assessment.getTitle());
        assessmentDates.setText(assessment.getStartDate() + " - " + assessment.getEndDate());
        assessmentType.setText("Type: " + assessment.getType());

        courseId = assessment.getCourseId();
        loadCourse();
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
        try {
            loadCourse();
        } catch (NullPointerException e) {
            Toast.makeText(this, "Unable to find the deleted object", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private void loadCourse() {
        try {
            dao = new CourseDAO(this);
            Course course = dao.getCourseById(courseId);

            adapter = new CourseAdapter(new ArrayList<>(List.of(course)), deletedId -> {
                Intent resultIntent = new Intent();
                resultIntent.putExtra("deletedCourseId", deletedId);
                Log.d("DetailAssessmentActivity", "in deletedCourseId: " + deletedId);
                setResult(RESULT_OK, resultIntent);
                finish();
            });
            recyclerView.setAdapter(adapter);
        } catch (NullPointerException e) {
            Toast.makeText(this, "Unable to find the deleted object", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    public void confirmAndDeleteDetailPage(View view) {
        new AlertDialog.Builder(this).setTitle("Delete Assessment").setMessage("Are you sure you want to delete this assessment?").setPositiveButton("Delete", (dialog, which) -> {
            // Delete from DB
            AssessmentDAO dao = new AssessmentDAO(this);
            dao.deleteAssessment(assessment.getId());

            Intent resultIntent = new Intent();
            resultIntent.putExtra("deletedAssessmentId", assessment.getId());
            setResult(RESULT_OK, resultIntent);
            finish();
        }).setNegativeButton("Cancel", null).show();
    }


    public void openEditFormDetailPage(View view) {
        Intent intent = new Intent(this, AddAssessmentActivity.class);
        intent.putExtra("editMode", true);
        intent.putExtra("assessment", assessment);
        activityResultLauncher.launch(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(android.view.Menu menu) {
        getMenuInflater().inflate(R.menu.menu_detail_assessment, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(android.view.MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.menu_alerts) {
            Intent intent = new Intent(this, ManageAlertActivity.class);
            intent.putExtra("objectId", assessment.getId());
            intent.putExtra("objectType", "Assessment");
            startActivity(intent);
            return true;
        } else if (id == R.id.menu_edit) {
            openEditFormDetailPage(item.getActionView());
            return true;
        } else if (id == R.id.menu_delete) {
            confirmAndDeleteDetailPage(item.getActionView());
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}