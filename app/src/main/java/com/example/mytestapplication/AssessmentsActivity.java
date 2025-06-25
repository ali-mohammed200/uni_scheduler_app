package com.example.mytestapplication;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mytestapplication.adapters.AssessmentAdapter;
import com.example.mytestapplication.database.AssessmentDAO;
import com.example.mytestapplication.models.Assessment;

import java.util.List;

public class AssessmentsActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private AssessmentAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_assessments);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true); // Show Up button
        getSupportActionBar().setTitle("Assessments");

        recyclerView = findViewById(R.id.recyclerViewAssessments);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        AssessmentDAO dao = new AssessmentDAO(this);
        List<Assessment> assessments = dao.getAllAssessments();
        adapter = new AssessmentAdapter(assessments);
        recyclerView.setAdapter(adapter);
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}
