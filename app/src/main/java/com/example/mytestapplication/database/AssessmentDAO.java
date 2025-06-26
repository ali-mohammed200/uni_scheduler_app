package com.example.mytestapplication.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.mytestapplication.models.Assessment;
import com.example.mytestapplication.models.Course;

import java.util.ArrayList;
import java.util.List;

public class AssessmentDAO {
    private SQLiteDatabase db;

    public AssessmentDAO(Context context) {
        DatabaseHelper dbHelper = new DatabaseHelper(context);
        db = dbHelper.getWritableDatabase();
    }

    public long insertAssessment(Assessment assessment) {
        ContentValues values = new ContentValues();
        values.put("course_id", assessment.getCourseId());
        values.put("type", assessment.getType());
        values.put("title", assessment.getTitle());
        values.put("start_date", assessment.getStartDate());
        values.put("end_date", assessment.getEndDate());
        return db.insert("assessments", null, values);
    }

    public List<Assessment> getAssessmentsByCourseId(int courseId) {
        List<Assessment> assessments = new ArrayList<>();
        Cursor cursor = db.query("assessments", null, "course_id = ?", new String[]{String.valueOf(courseId)}, null, null, null);

        while (cursor.moveToNext()) {
            assessments.add(buildModelFromCursor(cursor));
        }

        cursor.close();
        return assessments;
    }

    public Assessment getAssessmentById(int assessmentId) {
        Assessment assessment = new Assessment();
        Cursor cursor = db.query("assessments", null, "id = ?", new String[]{String.valueOf(assessmentId)}, null, null, null);

        while (cursor.moveToNext()) {
            assessment = buildModelFromCursor(cursor);
        }

        cursor.close();
        return assessment;
    }

    public List<Assessment> getAllAssessments() {
        List<Assessment> assessments = new ArrayList<>();
        Cursor cursor = db.query("assessments", null, null, null, null, null, null);

        while (cursor.moveToNext()) {
            assessments.add(buildModelFromCursor(cursor));
        }

        cursor.close();
        return assessments;
    }

    public int deleteAssessment(int assessmentId) {
        return db.delete("assessments", "id = ?", new String[]{String.valueOf(assessmentId)});
    }

    public int deleteAssessmentByCourseId(int courseId) {
        return db.delete("assessments", "course_id = ?", new String[]{String.valueOf(courseId)});
    }

    public int updateAssessment(Assessment assessment) {
        ContentValues values = new ContentValues();
        values.put("type", assessment.getType());
        values.put("title", assessment.getTitle());
        values.put("start_date", assessment.getStartDate());
        values.put("end_date", assessment.getEndDate());
        return db.update("assessments", values, "id = ?", new String[]{String.valueOf(assessment.getId())});
    }

    private Assessment buildModelFromCursor(Cursor cursor) {
        Assessment assessment = new Assessment();
        assessment.setId(cursor.getInt(cursor.getColumnIndexOrThrow("id")));
        assessment.setCourseId(cursor.getInt(cursor.getColumnIndexOrThrow("course_id")));
        assessment.setType(cursor.getString(cursor.getColumnIndexOrThrow("type")));
        assessment.setTitle(cursor.getString(cursor.getColumnIndexOrThrow("title")));
        assessment.setStartDate(cursor.getString(cursor.getColumnIndexOrThrow("start_date")));
        assessment.setEndDate(cursor.getString(cursor.getColumnIndexOrThrow("end_date")));
        return assessment;
    }
}
