package com.example.mytestapplication;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.mytestapplication.database.DatabaseHelper;

public class AddCourseActivity extends AppCompatActivity {

    private EditText titleInput, startDateInput, endDateInput, instructorNameInput,
            instructorPhoneInput, instructorEmailInput, noteInput;
    private Spinner statusSpinner;
    private int termId = 1; // You can later pass this dynamically

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_course);

        titleInput = findViewById(R.id.inputCourseTitle);
        startDateInput = findViewById(R.id.inputStartDate);
        endDateInput = findViewById(R.id.inputEndDate);
        instructorNameInput = findViewById(R.id.inputInstructorName);
        instructorPhoneInput = findViewById(R.id.inputInstructorPhone);
        instructorEmailInput = findViewById(R.id.inputInstructorEmail);
        noteInput = findViewById(R.id.inputNote);
        statusSpinner = findViewById(R.id.spinnerStatus);
        Button saveButton = findViewById(R.id.buttonSaveCourse);

        // Setup status dropdown
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this,
                R.array.course_status_array,
                android.R.layout.simple_spinner_item
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        statusSpinner.setAdapter(adapter);

        saveButton.setOnClickListener(v -> {
            saveCourse();
        });
    }

    private void saveCourse() {
        String title = titleInput.getText().toString();
        String start = startDateInput.getText().toString();
        String end = endDateInput.getText().toString();
        String status = statusSpinner.getSelectedItem().toString();
        String instructorName = instructorNameInput.getText().toString();
        String instructorPhone = instructorPhoneInput.getText().toString();
        String instructorEmail = instructorEmailInput.getText().toString();
        String note = noteInput.getText().toString();

        DatabaseHelper dbHelper = new DatabaseHelper(this);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("term_id", termId); // hardcoded for now
        values.put("title", title);
        values.put("start_date", start);
        values.put("end_date", end);
        values.put("status", status);
        values.put("instructor_name", instructorName);
        values.put("instructor_phone", instructorPhone);
        values.put("instructor_email", instructorEmail);
        values.put("note", note);

        long newRowId = db.insert("courses", null, values);
        if (newRowId != -1) {
            Toast.makeText(this, "Course added", Toast.LENGTH_SHORT).show();
            finish(); // close activity
        } else {
            Toast.makeText(this, "Failed to add course", Toast.LENGTH_SHORT).show();
        }
    }
}
