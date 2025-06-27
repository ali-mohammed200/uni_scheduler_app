package com.example.mytestapplication;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.mytestapplication.database.CourseDAO;
import com.example.mytestapplication.models.Course;

public class NoteActivity extends AppCompatActivity {

    private EditText noteEditText;
    private Course course;
    private CourseDAO courseDAO;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note);

        getSupportActionBar().setTitle("Course Note");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        noteEditText = findViewById(R.id.editTextNote);
        courseDAO = new CourseDAO(this);

        int courseId = getIntent().getIntExtra("courseId", -1);
        if (courseId == -1) {
            Toast.makeText(this, "Invalid course ID", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        course = courseDAO.getCourseById(courseId);
        if (course != null) {
            noteEditText.setText(course.getNote());
        } else {
            Toast.makeText(this, "Course not found", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    public void shareNote(View view){
        String note = noteEditText.getText().toString().trim();
        if (!note.isEmpty()) {
            Intent smsIntent = new Intent(Intent.ACTION_SEND);
            smsIntent.setType("text/plain");
            smsIntent.putExtra(Intent.EXTRA_TEXT, note);
            startActivity(Intent.createChooser(smsIntent, "Share note via SMS"));
        } else {
            Toast.makeText(NoteActivity.this, "Note is empty", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

    @Override
    protected void onPause() {
        super.onPause();

        if (course != null) {
            String updatedNote = noteEditText.getText().toString();
            course.setNote(updatedNote);
            courseDAO.updateCourse(course);
        }
    }
}
