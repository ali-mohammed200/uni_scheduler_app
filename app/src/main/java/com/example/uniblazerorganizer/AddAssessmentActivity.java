package com.example.uniblazerorganizer;

import android.Manifest;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.uniblazerorganizer.database.AssessmentDAO;
import com.example.uniblazerorganizer.models.Assessment;
import com.example.uniblazerorganizer.models.Course;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class AddAssessmentActivity extends AppCompatActivity {

    AssessmentDAO dao;
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

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.POST_NOTIFICATIONS}, 1);
            }
        }


        titleInput = findViewById(R.id.inputAssessmentTitle);
        startDateInput = findViewById(R.id.inputAssessmentStartDate);
        endDateInput = findViewById(R.id.inputAssessmentEndDate);
        typeSpinner = findViewById(R.id.spinnerAssessmentType);
        Button saveButton = findViewById(R.id.buttonSaveAssessment);

        course = (Course) getIntent().getSerializableExtra("course");
        editMode = getIntent().getBooleanExtra("editMode", false);
        if (editMode) {
            getSupportActionBar().setTitle("Edit Assessment");
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

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.assessment_type_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        typeSpinner.setAdapter(adapter);

        if (editMode) {
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

        DatePickerDialog datePickerDialog = new DatePickerDialog(this, (view, selectedYear, selectedMonth, selectedDay) -> {
            String formattedDate = String.format("%04d-%02d-%02d", selectedYear, selectedMonth + 1, selectedDay);
            targetEditText.setText(formattedDate);
        }, year, month, day);

        datePickerDialog.show();
    }

    private void prefillForm(Integer position) {
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

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        sdf.setLenient(false);
        try {
            Date startDate = sdf.parse(start);
            Date endDate = sdf.parse(end);

            if (startDate.after(endDate)) {
                Toast.makeText(this, "Start date must be before or equal to end date", Toast.LENGTH_SHORT).show();
                return;
            }
        } catch (Exception e) {
            Toast.makeText(this, "Invalid date format", Toast.LENGTH_SHORT).show();
            return;
        }

        Assessment new_assessment;
        dao = new AssessmentDAO(this);
        long newRowId;

        int courseAssessmentsCount = dao.getAssessmentCountByCourseId(courseId);
        if (courseAssessmentsCount >= 5 && !editMode) {
            Toast.makeText(this, "Course already has 5 assessments", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        if (editMode) {
            new_assessment = new Assessment(assessment.getId(), assessment.getCourseId(), type, title, start, end);
            newRowId = dao.updateAssessment(new_assessment);
        } else {
            new_assessment = new Assessment(courseId, type, title, start, end);
            newRowId = dao.insertAssessment(new_assessment);
        }


        if (newRowId != -1) {
            long oneDay = 24 * 60 * 60 * 1000L;
            int assessmentId = (int) newRowId;
            if (editMode) {
                assessmentId = new_assessment.getId();
            }

            Log.d("AddAssessmentActivity", "DB Transaction Success - newRowId: " + newRowId);
            Log.d("AddAssessmentActivity", "DB Transaction Success - assessmentId: " + assessmentId);


            Toast.makeText(this, "Assessment added", Toast.LENGTH_SHORT).show();
            Intent resultIntent = new Intent();
            resultIntent.putExtra("originator", "AddAssessmentActivity");
            resultIntent.putExtra("assessment", new_assessment);
            resultIntent.putExtra("fromEdit", editMode);
            resultIntent.putExtra("assessmentId", assessmentId);
            setResult(RESULT_OK, resultIntent);
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
