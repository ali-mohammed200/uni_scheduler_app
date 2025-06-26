package com.example.mytestapplication;

import android.app.DatePickerDialog;
import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.mytestapplication.database.AssessmentDAO;
import com.example.mytestapplication.database.DatabaseHelper;
import com.example.mytestapplication.models.Assessment;
import com.example.mytestapplication.models.Course;

import java.util.Calendar;

public class AddAssessmentActivity extends AppCompatActivity {

    private EditText titleInput, startDateInput, endDateInput;
    private Spinner typeSpinner;
    private int courseId;
    private Course course;
    private Assessment assessment;
    private boolean editMode = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_assessment);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Add Assessment");

        titleInput = findViewById(R.id.inputAssessmentTitle);
        startDateInput = findViewById(R.id.inputAssessmentStartDate);
        endDateInput = findViewById(R.id.inputAssessmentEndDate);
        typeSpinner = findViewById(R.id.spinnerAssessmentType);
        Button saveButton = findViewById(R.id.buttonSaveAssessment);

        course = (Course) getIntent().getSerializableExtra("course");
        editMode = getIntent().getBooleanExtra("editMode",false);
        if (editMode){
            assessment = (Assessment) getIntent().getSerializableExtra("assessment");
            courseId = assessment.getCourseId();
        } else {
            courseId = course.getId();
        }
        if (courseId == -1) {
            Toast.makeText(this, "Course not found", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        startDateInput.setOnClickListener(v -> showDatePickerDialog(startDateInput));
        endDateInput.setOnClickListener(v -> showDatePickerDialog(endDateInput));

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this,
                R.array.assessment_type_array,
                android.R.layout.simple_spinner_item
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        typeSpinner.setAdapter(adapter);

        if (editMode){
            int spinnerPosition = adapter.getPosition(assessment.getType());
            prefillForm(spinnerPosition);
        }

        saveButton.setOnClickListener(v -> saveAssessment());
    }

    private void showDatePickerDialog(final EditText targetEditText) {
        final Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                (view, selectedYear, selectedMonth, selectedDay) -> {
                    String formattedDate = String.format("%04d-%02d-%02d", selectedYear, selectedMonth + 1, selectedDay);
                    targetEditText.setText(formattedDate);
                },
                year, month, day
        );

        datePickerDialog.show();
    }

    private void prefillForm(Integer position){
        titleInput.setText(assessment.getTitle());
        startDateInput.setText(assessment.getStartDate());
        endDateInput.setText(assessment.getEndDate());
        typeSpinner.setSelection(position);
    }

    private void saveAssessment() {
        String title = titleInput.getText().toString().trim();
        String start = startDateInput.getText().toString().trim();
        String end = endDateInput.getText().toString().trim();
        String type = typeSpinner.getSelectedItem().toString();

        if (title.isEmpty() || start.isEmpty() || end.isEmpty()) {
            Toast.makeText(this, "Please fill out all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        Assessment new_assessment;
        AssessmentDAO dao = new AssessmentDAO(this);
        long newRowId;
        if (editMode){
            new_assessment = new Assessment(assessment.getId(), assessment.getCourseId(), type, title, start, end);
            newRowId = dao.updateAssessment(new_assessment);
        } else {
            new_assessment = new Assessment(courseId, type, title, start, end);
            newRowId = dao.insertAssessment(new_assessment);
        }

        if (newRowId != -1) {
            Toast.makeText(this, "Assessment added", Toast.LENGTH_SHORT).show();
            setResult(RESULT_OK);
            finish();
        } else {
            Toast.makeText(this, "Failed to add assessment", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}
