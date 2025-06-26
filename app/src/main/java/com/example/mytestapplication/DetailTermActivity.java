package com.example.mytestapplication;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mytestapplication.adapters.CourseAdapter;
import com.example.mytestapplication.database.CourseDAO;
import com.example.mytestapplication.models.Course;
import com.example.mytestapplication.models.Term;

import java.util.List;

public class DetailTermActivity extends AppCompatActivity {

    private TextView termTitleView, termStartView, termEndView;
    private RecyclerView recyclerView;
    private CourseAdapter adapter;
    private int termId;
    private ActivityResultLauncher<Intent> addCourseLauncher;

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
        Term term = (Term) intent.getSerializableExtra("term");

        termId = term.getId();
        termTitleView.setText("Title: " + term.getTitle());
        termStartView.setText("Start: " + term.getStartDate());
        termEndView.setText("End: " + term.getEndDate());
// TODO: Refactor other places of loading recyclerview like this
        loadCourses();

        addCourseLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK) {
                        loadCourses(); // Reload course list
                    }
                }
        );

        findViewById(R.id.fabAddCourse).setOnClickListener(view -> {
            Intent addIntent = new Intent(this, AddCourseActivity.class);
            addIntent.putExtra("term", term); // pre-fill course with this term
            addCourseLauncher.launch(addIntent);
        });
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        loadCourses();
    }

    private void loadCourses() {
        CourseDAO dao = new CourseDAO(this);
        List<Course> courses = dao.getCoursesByTermId(termId);
        adapter = new CourseAdapter(courses);
        recyclerView.setAdapter(adapter);
    }

    public void showDetailCourse(View view) {
        Course clickedCourse = (Course) view.getTag(); // Retrieve the course from the adapter
        Intent intent = new Intent(this, DetailCourseActivity.class);
        intent.putExtra("course", clickedCourse);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP); // Pops all activities above this one
        startActivity(intent);
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}
