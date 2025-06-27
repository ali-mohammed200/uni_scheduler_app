package com.example.mytestapplication;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mytestapplication.adapters.CourseAdapter;
import com.example.mytestapplication.database.AssessmentDAO;
import com.example.mytestapplication.database.CourseDAO;
import com.example.mytestapplication.database.TermDAO;
import com.example.mytestapplication.models.Course;
import com.example.mytestapplication.models.Term;

import java.util.List;

public class DetailTermActivity extends AppCompatActivity {

    private TextView termTitleView, termStartView, termEndView;
    private RecyclerView recyclerView;
    private CourseAdapter adapter;
    private int termId;
    private ActivityResultLauncher<Intent> activityResultLauncher;
    private CourseDAO dao;
    private Term term;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_term);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Term Details");

        termTitleView = findViewById(R.id.textTermTitle);
        termStartView = findViewById(R.id.textTermStart);
        termEndView = findViewById(R.id.textTermEnd);
        recyclerView = findViewById(R.id.recyclerViewTermCourses);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Get passed term info
        Intent intent = getIntent();
        term = (Term) intent.getSerializableExtra("term");
        termId = term.getId();
        setPageData();
        loadCourses();

        activityResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK) {
                        Intent resultData = result.getData();
                        if(resultData != null){
                            term = (Term) resultData.getSerializableExtra("term");
                            boolean fromEdit = resultData.getBooleanExtra("fromEdit", false);
                            termId = resultData.getIntExtra("termId", termId);
                            setPageData();
                            Log.d("DetailTermActivity ResultContract", term + " " + termId + " " + fromEdit);
                        }
                        loadCourses(); // Reload course list
                    }
                }
        );

        findViewById(R.id.fabAddCourse).setOnClickListener(view -> {
            Intent addIntent = new Intent(this, AddCourseActivity.class);
            addIntent.putExtra("term", term); // pre-fill course with this term
            activityResultLauncher.launch(addIntent);
        });
    }
    private void setPageData(){
        termTitleView.setText("[" + termId + "] " + "Title: " + term.getTitle());
        termStartView.setText("Start: " + term.getStartDate());
        termEndView.setText("End: " + term.getEndDate());
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        try {
            loadCourses();
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

    private void loadCourses() {
        dao = new CourseDAO(this);
        List<Course> courses = dao.getCoursesByTermId(termId);
        adapter = new CourseAdapter(courses, deletedId -> {
            Intent resultIntent = new Intent();
            resultIntent.putExtra("deletedCourseId", deletedId);
            Log.d("DetailTermActivity", "in deletedCourseId: " + deletedId);
            setResult(RESULT_OK, resultIntent);
        });
        recyclerView.setAdapter(adapter);
    }

    public void showDetailCourse(View view) {
        Course clickedCourse = (Course) view.getTag(); // Retrieve the course from the adapter
        Intent intent = new Intent(this, DetailCourseActivity.class);
        intent.putExtra("course", clickedCourse);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP); // Pops all activities above this one
        activityResultLauncher.launch(intent);
    }

    public void confirmAndDeleteDetailPage(View view) {
        new AlertDialog.Builder(this)
                .setTitle("Delete Term")
                .setMessage("Are you sure you want to delete this term? Terms with courses will not be deleted")
                .setPositiveButton("Delete", (dialog, which) -> {
                    // Delete from DB
                    TermDAO dao = new TermDAO(this);
                    CourseDAO c_dao = new CourseDAO(this);

                    if (c_dao.countCoursesByTermId(term.getId()) == 0) {
                        dao.deleteTerm(term.getId());

                        Intent resultIntent = new Intent();
                        resultIntent.putExtra("deletedTermId", term.getId());
                        setResult(RESULT_OK, resultIntent);
                        finish();
                    } else {
                        Toast.makeText(this, "Delete associated courses for this term first", Toast.LENGTH_SHORT).show();

                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }


    public void openEditFormDetailPage(View view) {
        Intent intent = new Intent(this, AddTermActivity.class);
        intent.putExtra("editMode", true);
        intent.putExtra("term", term);
        activityResultLauncher.launch(intent);
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}
