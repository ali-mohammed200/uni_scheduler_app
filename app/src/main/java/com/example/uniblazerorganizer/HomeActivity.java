package com.example.uniblazerorganizer;

import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.uniblazerorganizer.database.DatabaseHelper;

public class HomeActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        CardView coursesCard = findViewById(R.id.coursesCard);
        CardView termsCard = findViewById(R.id.termsCard);
        CardView assessmentsCard = findViewById(R.id.assessmentsCard);

        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_home);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private void seedTerms() {
        DatabaseHelper dbHelper = new DatabaseHelper(this);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        for (int i = 1; i <= 5; i++) {
            ContentValues values = new ContentValues();
            values.put("title", "Term " + i);
            values.put("start_date", "2025-01-01");
            values.put("end_date", "2025-06-30");

            db.insert("terms", null, values);
        }
    }

    private void seedCourses() {
        DatabaseHelper dbHelper = new DatabaseHelper(this);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        for (int i = 1; i <= 15; i++) {
            ContentValues values = new ContentValues();
            values.put("term_id", 1);
            values.put("title", "Course " + i);
            values.put("start_date", "2025-09-01");
            values.put("end_date", "2025-12-15");
            values.put("status", "in progress");
            values.put("instructor_name", "Instructor " + i);
            values.put("instructor_phone", "555-000" + i);
            values.put("instructor_email", "instructor" + i + "@school.edu");
            values.put("note", "Sample note for course " + i);

            db.insert("courses", null, values);
        }
    }

    private void seedAssessments() {
        DatabaseHelper dbHelper = new DatabaseHelper(this);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        for (int i = 1; i <= 15; i++) {
            ContentValues values = new ContentValues();
            values.put("course_id", 1);
            values.put("type", i % 2 == 0 ? "Performance" : "Objective");
            values.put("title", "Assessment " + i);
            values.put("start_date", "2025-11-01");
            values.put("end_date", "2025-11-15");

            db.insert("assessments", null, values);
        }
    }


    public void openCourses(View view) {
        Intent intent = new Intent(this, CoursesActivity.class);
        startActivity(intent);
    }

    public void openTerms(View view) {
        Intent intent = new Intent(this, TermsActivity.class);
        startActivity(intent);
    }

    public void openAssessments(View view) {
        Intent intent = new Intent(this, AssessmentsActivity.class);
        startActivity(intent);
    }

}