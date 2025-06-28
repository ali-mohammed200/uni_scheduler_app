package com.example.uniblazerorganizer;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.uniblazerorganizer.database.AlertDAO;
import com.example.uniblazerorganizer.database.AssessmentDAO;
import com.example.uniblazerorganizer.database.CourseDAO;
import com.example.uniblazerorganizer.models.Alert;
import com.example.uniblazerorganizer.notifications.AlertReceiver;

import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ManageAlertActivity extends AppCompatActivity {

    String objectType;
    int objectId;
    //    private Assessment assessment;
    private TextView titleView, startView, endView;
    private Switch switchStartAlert, switchEndAlert;
    private AlertDAO alertDAO;
    private Alert alert;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_alert);
        getSupportActionBar().setTitle("Manage Alerts");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        objectId = getIntent().getIntExtra("objectId", -1);
        objectType = getIntent().getStringExtra("objectType");

        if (objectId == -1) {
            Toast.makeText(this, "Object not found", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        setVariables();

        Object object = null;
        if (objectType.equals("Assessment")) {
            AssessmentDAO assessmentDAO = new AssessmentDAO(this);
            object = assessmentDAO.getAssessmentById(objectId);
        } else if (objectType.equals("Course")) {
            CourseDAO courseDAO = new CourseDAO(this);
            object = courseDAO.getCourseById(objectId);
        }

        if (object == null) {
            Toast.makeText(this, "Object not found in DB", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        Log.d("ManageAlertActivity", getAttributeDynamically(object, "getId"));
        Log.d("ManageAlertActivity", String.valueOf(object.getClass()));


//        int id = Integer.parseInt(getAttributeDynamically(object, "getId"));
        String title = getAttributeDynamically(object, "getTitle");
        String start = getAttributeDynamically(object, "getStartDate");
        String end = getAttributeDynamically(object, "getEndDate");


        titleView.setText("Title: " + title);
        startView.setText("Start: " + start);
        endView.setText("End: " + end);

        switchStartAlert.setOnCheckedChangeListener((CompoundButton buttonView, boolean isChecked) -> {
            if (isChecked) {
                scheduleAlert("Start Alert", buildMessage(objectType, title, start, "starts today"), start, 0, false);
            } else {
                cancelAlert("BUILD", "THIS OUT");
                Toast.makeText(this, "Alert will still trigger. Functionality Not Built.", Toast.LENGTH_SHORT).show();
            }
            alert.setStartToggle(isChecked);
            alertDAO.updateAlert(alert);
        });

        switchEndAlert.setOnCheckedChangeListener((CompoundButton buttonView, boolean isChecked) -> {
            if (isChecked) {
                scheduleAlert("End Alert", buildMessage(objectType, title, end, "ends now"), end, 1000, false);
            }  else {
                cancelAlert("BUILD", "THIS OUT");
                Toast.makeText(this, "Alert will still trigger. Functionality Not Built.", Toast.LENGTH_SHORT).show();
            }
            alert.setEndToggle(isChecked);
            alertDAO.updateAlert(alert);
        });

        findViewById(R.id.buttonTestNotification).setOnClickListener(v -> scheduleAlert("Test Alert", buildMessage(objectType, title, start, "This is a test notification!"), start, 2500, true));
    }

    private String buildMessage(String type, String title, String date, String msg) {
        return type + " " + title + " " + msg + " " + date;
    }

    private String getAttributeDynamically(Object obj, String methodName) {
        try {
            Method method = obj.getClass().getMethod(methodName);
            Object result = method.invoke(obj);

            return result != null ? result.toString() : "";
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    private Alert findAlert(){
        alert = alertDAO.getAlert(objectType, objectId);
        if(alert == null) {
            int alertId = (int) alertDAO.insertAlert(new Alert(objectType, objectId));
            alert = alertDAO.getAlertById(alertId, objectType, objectId);
        }
        return alert;
    }


    private void setVariables() {
        titleView = findViewById(R.id.textTitle);
        startView = findViewById(R.id.textStartDate);
        endView = findViewById(R.id.textEndDate);
        switchStartAlert = findViewById(R.id.switchStartAlert);
        switchEndAlert = findViewById(R.id.switchEndAlert);
        alertDAO = new AlertDAO(this);
        findAlert();
        switchStartAlert.setChecked(alert.isStartToggle());
        switchEndAlert.setChecked(alert.isEndToggle());
    }

    private void scheduleAlert(String title, String message, String dateStr, long offsetMillis, boolean test) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            sdf.setLenient(false);
            Date date = sdf.parse(dateStr);

            Log.d("ManageAlertActivity", date.toString());

            long triggerTime = date.getTime() + offsetMillis;
            if (test) {
                triggerTime = System.currentTimeMillis() + offsetMillis;
            }

            Intent intent = new Intent(this, AlertReceiver.class);
            intent.putExtra("title", title);
            intent.putExtra("message", message);
            intent.putExtra("objectId", objectId);
            intent.putExtra("objectType", objectType);

            int requestCode = (int) System.currentTimeMillis();
            PendingIntent pendingIntent = PendingIntent.getBroadcast(this, requestCode, intent, PendingIntent.FLAG_IMMUTABLE);

            AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
            alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerTime, pendingIntent);

            Toast.makeText(this, "Alert scheduled", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Failed to schedule alert", Toast.LENGTH_SHORT).show();
        }
    }

    private void cancelAlert(String title, String message) {
// TODO: Build this out, need to to store the requestcode
//        Intent intent = new Intent(this, AlertReceiver.class);
//        intent.putExtra("title", title);
//        intent.putExtra("message", message);
//        intent.putExtra("objectId", objectId);
//        intent.putExtra("objectType", objectType);
//
//        // You MUST use the same requestCode as when you scheduled it
//        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, /* SAME requestCode */, intent, PendingIntent.FLAG_IMMUTABLE);
//
//        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
//        alarmManager.cancel(pendingIntent);
//
//        Toast.makeText(this, "Alert canceled", Toast.LENGTH_SHORT).show();
    }


    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}
