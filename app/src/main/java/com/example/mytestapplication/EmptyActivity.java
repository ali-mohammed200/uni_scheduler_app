package com.example.mytestapplication;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mytestapplication.adapters.CourseAdapter;
import com.example.mytestapplication.database.CourseDAO;
import com.example.mytestapplication.database.DatabaseHelper;
import com.example.mytestapplication.models.Course;

import java.util.List;

public class EmptyActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        DatabaseHelper dbHelper = new DatabaseHelper(this);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

//        for (int i = 1; i <= 15; i++) {
//            ContentValues values = new ContentValues();
//            values.put("term_id", 1); // assuming you're adding them to term ID 1
//            values.put("title", "Course " + i);
//            values.put("start_date", "2025-09-01");
//            values.put("end_date", "2025-12-15");
//            values.put("status", "in progress");
//            values.put("instructor_name", "Instructor " + i);
//            values.put("instructor_phone", "555-000" + i);
//            values.put("instructor_email", "instructor" + i + "@school.edu");
//            values.put("note", "Sample note for course " + i);
//
//            db.insert("courses", null, values);
//        }


        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_empty);

        CourseDAO courseDAO = new CourseDAO(this);
        List<Course> allCourses = courseDAO.getAllCourses();

        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        CourseAdapter adapter = new CourseAdapter(allCourses);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

//        RecyclerView recyclerView = findViewById(R.id.recyclerView);
//        recyclerView.setLayoutManager(new LinearLayoutManager(this));
//        recyclerView.setAdapter(new TermAdapter(yourTermList)); // or AssessmentAdapter


        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
}