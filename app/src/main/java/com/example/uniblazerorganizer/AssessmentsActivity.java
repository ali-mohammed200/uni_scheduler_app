package com.example.uniblazerorganizer;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.uniblazerorganizer.adapters.AssessmentAdapter;
import com.example.uniblazerorganizer.database.AssessmentDAO;
import com.example.uniblazerorganizer.models.Assessment;

import java.util.List;

public class AssessmentsActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private AssessmentAdapter adapter;
    private ActivityResultLauncher<Intent> addAssessmentLauncher;

    private AssessmentDAO dao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_assessments);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true); // Show Up button
        getSupportActionBar().setTitle("Assessments");

        loadAssessments();

        addAssessmentLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
            if (result.getResultCode() == RESULT_OK) {
                // TODO: Move to a method like in detailtermactivity
                List<Assessment> updatedAssessments = dao.getAllAssessments();
                adapter.setAssessments(updatedAssessments);
                adapter.notifyDataSetChanged();
            }
        });
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        loadAssessments();
    }


    private void loadAssessments() {
        recyclerView = findViewById(R.id.recyclerViewAssessments);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        dao = new AssessmentDAO(this);
        List<Assessment> assessments = dao.getAllAssessments();
        adapter = new AssessmentAdapter(assessments, deletedId -> {
            Intent resultIntent = new Intent();
            resultIntent.putExtra("deletedAssessmentId", deletedId);
            setResult(RESULT_OK, resultIntent);
        });

        recyclerView.setAdapter(adapter);
    }

    public void showDetailAssessment(View view) {
        Assessment clickedAssessment = (Assessment) view.getTag(); // Retrieve the course from the adapterAdd commentMore actions
        Intent intent = new Intent(this, DetailAssessmentActivity.class);
        intent.putExtra("assessment", clickedAssessment);
        startActivity(intent);
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}
