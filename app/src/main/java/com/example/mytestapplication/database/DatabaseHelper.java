package com.example.mytestapplication.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "uni_scheduler.db";
    private static final int DATABASE_VERSION = 1;

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // Called when DB is created for the first time
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE terms (id INTEGER PRIMARY KEY AUTOINCREMENT, title TEXT, start_date TEXT, end_date TEXT)");
        db.execSQL("CREATE TABLE courses (id INTEGER PRIMARY KEY AUTOINCREMENT, term_id INTEGER, title TEXT, start_date TEXT, end_date TEXT, status TEXT, instructor_name TEXT, instructor_phone TEXT, instructor_email TEXT, note TEXT, FOREIGN KEY(term_id) REFERENCES terms(id))");
        db.execSQL("CREATE TABLE assessments (id INTEGER PRIMARY KEY AUTOINCREMENT, course_id INTEGER, type TEXT, title TEXT, start_date TEXT, end_date TEXT, FOREIGN KEY(course_id) REFERENCES courses(id))");
    }

    // Called when DB version is upgraded
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS assessments");
        db.execSQL("DROP TABLE IF EXISTS courses");
        db.execSQL("DROP TABLE IF EXISTS terms");
        onCreate(db);
    }
}
