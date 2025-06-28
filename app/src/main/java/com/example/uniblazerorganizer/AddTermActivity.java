package com.example.uniblazerorganizer;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.uniblazerorganizer.database.TermDAO;
import com.example.uniblazerorganizer.models.Term;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class AddTermActivity extends AppCompatActivity {

    private EditText titleInput, startDateInput, endDateInput;
    private Term term;
    private boolean editMode = false;
    private TermDAO dao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_term);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Add Term");

        titleInput = findViewById(R.id.inputTermTitle);
        startDateInput = findViewById(R.id.inputStartDate);
        endDateInput = findViewById(R.id.inputEndDate);
        Button saveButton = findViewById(R.id.buttonSaveTerm);

        startDateInput.setOnClickListener(v -> showDatePickerDialog(startDateInput));
        endDateInput.setOnClickListener(v -> showDatePickerDialog(endDateInput));

        editMode = getIntent().getBooleanExtra("editMode", false);
        if (editMode) {
            getSupportActionBar().setTitle("Edit Term");
            term = (Term) getIntent().getSerializableExtra("term");
            prefillForm();
        }


        saveButton.setOnClickListener(v -> saveTerm());
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

    private void prefillForm() {
        titleInput.setText(term.getTitle());
        startDateInput.setText(term.getStartDate());
        endDateInput.setText(term.getEndDate());
    }

    private void saveTerm() {
        String title = titleInput.getText().toString().trim();
        String start = startDateInput.getText().toString().trim();
        String end = endDateInput.getText().toString().trim();

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

        dao = new TermDAO(this);
        long newRowId;
        Term new_term;
        if (editMode) {
            new_term = new Term(term.getId(), title, start, end);
            newRowId = dao.updateTerm(new_term);
        } else {
            new_term = new Term(title, start, end);
            newRowId = dao.insertTerm(new_term);
        }

        if (newRowId != -1) {
            Toast.makeText(this, "Term added", Toast.LENGTH_SHORT).show();
            Intent resultIntent = new Intent();
            resultIntent.putExtra("originator", "AddTermActivity");
            resultIntent.putExtra("term", new_term);
            resultIntent.putExtra("fromEdit", editMode);
            int termId = editMode ? new_term.getId() : (int) newRowId;
            resultIntent.putExtra("termId", termId);
            setResult(RESULT_OK, resultIntent);
            finish();
        } else {
            Toast.makeText(this, "Failed to add term", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}
