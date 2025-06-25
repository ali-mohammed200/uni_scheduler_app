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
import com.example.mytestapplication.models.Term;
import com.example.mytestapplication.database.TermDAO;

import java.util.List;


public class AddCourseActivity extends AppCompatActivity {

    private EditText titleInput, startDateInput, endDateInput, instructorNameInput,
            instructorPhoneInput, instructorEmailInput, noteInput;
    private Spinner statusSpinner, termSpinner;
    private List<Term> termList;


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

        termSpinner = findViewById(R.id.spinnerTerms);

        // Load terms from database
        TermDAO termDAO = new TermDAO(this);
        termList = termDAO.getAllTerms();

        // Use ArrayAdapter with toString()
        ArrayAdapter<Term> termAdapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                termList
        );
        termAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        termSpinner.setAdapter(termAdapter);


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
        String title = titleInput.getText().toString().trim();
        String start = startDateInput.getText().toString().trim();
        String end = endDateInput.getText().toString().trim();
        String status = statusSpinner.getSelectedItem().toString();
        String instructorName = instructorNameInput.getText().toString().trim();
        String instructorPhone = instructorPhoneInput.getText().toString().trim();
        String instructorEmail = instructorEmailInput.getText().toString().trim();
        String note = noteInput.getText().toString().trim();

        // Validate required fields
        if (title.isEmpty() || start.isEmpty() || end.isEmpty() ||
                instructorName.isEmpty() || instructorPhone.isEmpty() || instructorEmail.isEmpty()) {
            Toast.makeText(this, "Please fill out all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        // Add simple format validations
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(instructorEmail).matches()) {
            Toast.makeText(this, "Enter a valid email", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!instructorPhone.matches("\\d{3}-\\d{3}-\\d{4}")) {
            Toast.makeText(this, "Enter a valid phone (e.g., 555-123-4567)", Toast.LENGTH_SHORT).show();
            return;
        }

        DatabaseHelper dbHelper = new DatabaseHelper(this);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues values = new ContentValues();

        Term selectedTerm = (Term) termSpinner.getSelectedItem();
        int selectedTermId = selectedTerm.getId();
        values.put("term_id", selectedTermId);

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
            setResult(RESULT_OK);
            finish(); // close activity
        } else {
            Toast.makeText(this, "Failed to add course", Toast.LENGTH_SHORT).show();
        }
    }
}
