package com.example.mytestapplication.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.mytestapplication.models.Course;

import java.util.ArrayList;
import java.util.List;

public class CourseDAO {
    private SQLiteDatabase db;

    public CourseDAO(Context context) {
        db = DatabaseHelper.getInstance(context).getWritableDatabase();
    }

    public long insertCourse(Course course) {
        ContentValues values = new ContentValues();
        values.put("term_id", course.getTermId());
        values.put("title", course.getTitle());
        values.put("start_date", course.getStartDate());
        values.put("end_date", course.getEndDate());
        values.put("status", course.getStatus());
        values.put("instructor_name", course.getInstructorName());
        values.put("instructor_phone", course.getInstructorPhone());
        values.put("instructor_email", course.getInstructorEmail());
        values.put("note", course.getNote());

        return db.insert("courses", null, values);
    }

    public int updateCourse(Course course) {
        ContentValues values = new ContentValues();
        values.put("term_id", course.getTermId());
        values.put("title", course.getTitle());
        values.put("start_date", course.getStartDate());
        values.put("end_date", course.getEndDate());
        values.put("status", course.getStatus());
        values.put("instructor_name", course.getInstructorName());
        values.put("instructor_phone", course.getInstructorPhone());
        values.put("instructor_email", course.getInstructorEmail());
        values.put("note", course.getNote());

        return db.update("courses", values, "id = ?", new String[]{String.valueOf(course.getId())});
    }

    public int deleteCourse(int id) {
        return db.delete("courses", "id = ?", new String[]{String.valueOf(id)});
    }

    public Course getCourseById(int id) {
        Cursor cursor = db.query(
                "courses",
                null,
                "id = ?",
                new String[]{String.valueOf(id)},
                null, null, null
        );

        if (cursor != null && cursor.moveToFirst()) {
            Course course = buildCourseFromCursor(cursor);
            cursor.close();
            return course;
        }

        return null;
    }

    public List<Course> getAllCourses() {
        List<Course> courses = new ArrayList<>();
        Cursor cursor = db.query("courses", null, null, null, null, null, null);

        while (cursor.moveToNext()) {
            courses.add(buildCourseFromCursor(cursor));
        }

        cursor.close();
        return courses;
    }

    public List<Course> getCoursesByTermId(int termId) {
        List<Course> courses = new ArrayList<>();
        Cursor cursor = db.query(
                "courses",
                null,
                "term_id = ?",
                new String[]{String.valueOf(termId)},
                null, null, null
        );

        while (cursor.moveToNext()) {
            courses.add(buildCourseFromCursor(cursor));
        }

        cursor.close();
        return courses;
    }

    public int countCoursesByTermId(int termId) {
        int count = 0;
        Cursor cursor = db.rawQuery(
                "SELECT COUNT(*) FROM courses WHERE term_id = ?",
                new String[]{String.valueOf(termId)}
        );

        if (cursor != null && cursor.moveToFirst()) {
            count = cursor.getInt(0);
            cursor.close();
        }

        return count;
    }


    private Course buildCourseFromCursor(Cursor cursor) {
        Course course = new Course();
        course.setId(cursor.getInt(cursor.getColumnIndexOrThrow("id")));
        course.setTermId(cursor.getInt(cursor.getColumnIndexOrThrow("term_id")));
        course.setTitle(cursor.getString(cursor.getColumnIndexOrThrow("title")));
        course.setStartDate(cursor.getString(cursor.getColumnIndexOrThrow("start_date")));
        course.setEndDate(cursor.getString(cursor.getColumnIndexOrThrow("end_date")));
        course.setStatus(cursor.getString(cursor.getColumnIndexOrThrow("status")));
        course.setInstructorName(cursor.getString(cursor.getColumnIndexOrThrow("instructor_name")));
        course.setInstructorPhone(cursor.getString(cursor.getColumnIndexOrThrow("instructor_phone")));
        course.setInstructorEmail(cursor.getString(cursor.getColumnIndexOrThrow("instructor_email")));
        course.setNote(cursor.getString(cursor.getColumnIndexOrThrow("note")));
        return course;
    }
}