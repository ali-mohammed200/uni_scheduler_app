package com.example.mytestapplication;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mytestapplication.adapters.CourseAdapter;
import com.example.mytestapplication.database.CourseDAO;
import com.example.mytestapplication.models.Course;

import java.util.List;

public class CoursesActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private CourseAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_courses);

        recyclerView = findViewById(R.id.recyclerViewCourses);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        CourseDAO dao = new CourseDAO(this);
        List<Course> courses = dao.getAllCourses();
        adapter = new CourseAdapter(courses);
        recyclerView.setAdapter(adapter);
    }
}



//        RecyclerView recyclerView = findViewById(R.id.recyclerView);
//        recyclerView.setLayoutManager(new LinearLayoutManager(this));
//        recyclerView.setAdapter(new TermAdapter(yourTermList)); // or AssessmentAdapter
