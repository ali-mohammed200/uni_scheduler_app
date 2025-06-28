package com.example.mytestapplication;

import android.Manifest;
import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.mytestapplication.database.AssessmentDAO;
import com.example.mytestapplication.database.CourseDAO;
import com.example.mytestapplication.database.DatabaseHelper;
import com.example.mytestapplication.models.Assessment;
import com.example.mytestapplication.models.Course;
import com.example.mytestapplication.models.Term;
import com.example.mytestapplication.database.TermDAO;
import com.example.mytestapplication.notifications.AssessmentsAlertReceiver;
import com.example.mytestapplication.notifications.CoursesAlertReceiver;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;


public class AddCourseActivity extends AppCompatActivity {

    private EditText titleInput, startDateInput, endDateInput, instructorNameInput,
            instructorPhoneInput, instructorEmailInput, noteInput;
    private Spinner statusSpinner, termSpinner;
    private List<Term> termList;

    private Course course;
    private boolean editMode = false;
    private Term term;
    private CourseDAO dao;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_course);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true); // Show Up button
        getSupportActionBar().setTitle("Add Course");

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.POST_NOTIFICATIONS}, 1);
            }
        }

        titleInput = findViewById(R.id.inputCourseTitle);
        startDateInput = findViewById(R.id.inputStartDate);
        endDateInput = findViewById(R.id.inputEndDate);
        instructorNameInput = findViewById(R.id.inputInstructorName);
        instructorPhoneInput = findViewById(R.id.inputInstructorPhone);
        instructorEmailInput = findViewById(R.id.inputInstructorEmail);
        noteInput = findViewById(R.id.inputNote);
        statusSpinner = findViewById(R.id.spinnerStatus);
        Button saveButton = findViewById(R.id.buttonSaveCourse);

        startDateInput.setOnClickListener(v -> showDatePickerDialog(startDateInput));
        endDateInput.setOnClickListener(v -> showDatePickerDialog(endDateInput));


        termSpinner = findViewById(R.id.spinnerTerms);

        // Get passed term info
        TermDAO termDAO = new TermDAO(this);
        term = (Term) getIntent().getSerializableExtra("term"); // From Detail Term
        course = (Course) getIntent().getSerializableExtra("course"); // From Edit
        editMode = getIntent().getBooleanExtra("editMode", false);

        if (editMode) {
            term = termDAO.getTermById(course.getTermId());
        }
        termDAO = new TermDAO(this);
        termList = termDAO.getAllTerms();
        Term noTerm = new Term(-1, "(No Term)", "", "");
        termList.add(0, noTerm);



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

        // when edit, only course is present
        // when detail term add course, only term is present
        // we want to fill term if coming from edit or from detail term
        if (editMode || term != null) {
            int termPosition = -1; // Need to manually loop since we aren't comparing object identity or we need to overwrite equals/hashcode
            if (term != null) {
                for (int i = 0; i < termAdapter.getCount(); i++) {
                    Term t = termAdapter.getItem(i);
                    if (t != null && t.getId() == term.getId()) {
                        termPosition = i;
                        break;
                    }
                }
            }
            if (course == null) {
                prefillFormTerm(termPosition);
            } else {
                int statusPosition = adapter.getPosition(course.getStatus());
                prefillForm(statusPosition, termPosition);
            }

        }

        saveButton.setOnClickListener(v -> {
            saveCourse();
        });
    }

    private void showDatePickerDialog(final EditText targetEditText) {
        final Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);
// TODO: Fix Date format to preferred Mon, day, year - anywhere where date is used
        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                (view, selectedYear, selectedMonth, selectedDay) -> {
                    // Month is 0-based in Calendar, so add 1
                    String formattedDate = String.format("%04d-%02d-%02d", selectedYear, selectedMonth + 1, selectedDay);
                    targetEditText.setText(formattedDate);
                },
                year, month, day
        );

        datePickerDialog.show();
    }

    private void prefillFormTerm(Integer termPosition) {
        termSpinner.setSelection(termPosition);
    }

    private void prefillForm(Integer statusPosition, Integer termPosition) {
        titleInput.setText(course.getTitle());
        startDateInput.setText(course.getStartDate());
        endDateInput.setText(course.getEndDate());
        statusSpinner.setSelection(statusPosition);
        termSpinner.setSelection(termPosition);
        instructorNameInput.setText(course.getInstructorName());
        instructorPhoneInput.setText(course.getInstructorPhone());
        instructorEmailInput.setText(course.getInstructorEmail());
        noteInput.setText(course.getNote());
    }

    //    TODO: Validate start and end dates
    private void saveCourse() {
        String title = titleInput.getText().toString().trim();
        String start = startDateInput.getText().toString().trim();
        String end = endDateInput.getText().toString().trim();
        String status = statusSpinner.getSelectedItem().toString();
        String instructorName = instructorNameInput.getText().toString().trim();
        String instructorPhone = instructorPhoneInput.getText().toString().trim();
        String instructorEmail = instructorEmailInput.getText().toString().trim();
        String note = noteInput.getText().toString().trim();

        Term selectedTerm = (Term) termSpinner.getSelectedItem();
        int selectedTermId=  selectedTerm.getId();

        // Validate required fields
        if (title.isEmpty() || start.isEmpty() || end.isEmpty() ||
                instructorName.isEmpty() || instructorPhone.isEmpty() || instructorEmail.isEmpty()) {
            Toast.makeText(this, "Please fill out all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        // Add simple format validations
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(instructorEmail).matches()) {
            Toast.makeText(this, "Enter a valid format email (e.g., john@wgu.edu)", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!instructorPhone.matches("\\d{3}-\\d{3}-\\d{4}")) {
            Toast.makeText(this, "Enter a valid format phone (e.g., 555-123-4567)", Toast.LENGTH_SHORT).show();
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

        Course new_course;
        dao = new CourseDAO(this);
        long newRowId;
        if (editMode) {
            new_course = new Course(course.getId(), selectedTermId, title, start, end, status, instructorName, instructorPhone, instructorEmail, note);
            newRowId = dao.updateCourse(new_course);
        } else {
            new_course = new Course(selectedTermId, title, start, end, status, instructorName, instructorPhone, instructorEmail, note);
            newRowId = dao.insertCourse(new_course);
        }

        if (newRowId != -1) {
            long oneDay = 24 * 60 * 60 * 1000L;
            int courseId = (int) newRowId;
            if (editMode){
                courseId  = new_course.getId();
            }

            Log.d("AddCourseActivity", "DB Transaction Success - newRowId: " + newRowId);
            Log.d("AddCourseActivity", "DB Transaction Success - courseId: " + courseId);

            scheduleAlert(
                    "Course Reminder",
                    "Course \"" + title + "\" starts tomorrow!",
                    start,
                    courseId,
                    -oneDay // 1 day before
            );

            scheduleAlert(
                    "Course Today",
                    "\"" + title + "\" starts today! (" + start + " - " + end + ")",
                    start,
                    courseId,
                    0 // same day (midnight)
            );

            scheduleAlert(
                    "Course Ended",
                    "Course \"" + title + "\" has ended.",
                    end,
                    courseId,
                    1000 // 1 second after midnight
            );

            Toast.makeText(this, "Course added", Toast.LENGTH_SHORT).show();
            Intent resultIntent = new Intent();
            resultIntent.putExtra("originator", "AddCourseActivity");
            resultIntent.putExtra("course", new_course);
            resultIntent.putExtra("fromEdit", editMode);
            resultIntent.putExtra("courseId", courseId);
            setResult(RESULT_OK, resultIntent);
            finish(); // close activity
        } else {
            Toast.makeText(this, "Failed to add course", Toast.LENGTH_SHORT).show();
        }
    }

    private void scheduleAlert(String title, String message, String dateStr, int courseId, long offsetMillis) {
        try {
//            TODO: Build out boot persistence for notifications
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            sdf.setLenient(false);
            Date date = sdf.parse(dateStr);
            long triggerTime = date.getTime() + offsetMillis;
//            long triggerTime = System.currentTimeMillis() + offsetMillis; // Uncomment to test, arg 5000 => 5 sec from now

            Intent intent = new Intent(this, CoursesAlertReceiver.class);
            intent.putExtra("title", title);
            intent.putExtra("message", message);
            intent.putExtra("courseId", courseId);

            intent.putExtra("channelId", "course_channel");
            intent.putExtra("channelName", "Course Alerts");

            int requestCode = (int) System.currentTimeMillis();
            PendingIntent pendingIntent = PendingIntent.getBroadcast(
                    this, requestCode, intent, PendingIntent.FLAG_IMMUTABLE
            );

            AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
            alarmManager.setExactAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP, triggerTime, pendingIntent
            );
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}
