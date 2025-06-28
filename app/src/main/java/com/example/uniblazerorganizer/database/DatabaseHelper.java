package com.example.uniblazerorganizer.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "uni_scheduler.db";
    private static final int DATABASE_VERSION = 1;

    private static DatabaseHelper instance;

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public static synchronized DatabaseHelper getInstance(Context context) {
        if (instance == null) {
            // Use application context to avoid leaking activities
            instance = new DatabaseHelper(context.getApplicationContext());
        }
        return instance;
    }

    // Called when DB is created for the first time
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE terms (id INTEGER PRIMARY KEY AUTOINCREMENT, title TEXT, start_date TEXT, end_date TEXT)");
        db.execSQL("CREATE TABLE courses (id INTEGER PRIMARY KEY AUTOINCREMENT, term_id INTEGER, title TEXT, start_date TEXT, end_date TEXT, status TEXT, instructor_name TEXT, instructor_phone TEXT, instructor_email TEXT, note TEXT, FOREIGN KEY(term_id) REFERENCES terms(id))");
        db.execSQL("CREATE TABLE assessments (id INTEGER PRIMARY KEY AUTOINCREMENT, course_id INTEGER, type TEXT, title TEXT, start_date TEXT, end_date TEXT, FOREIGN KEY(course_id) REFERENCES courses(id))");
        db.execSQL("CREATE TABLE alerts (id INTEGER PRIMARY KEY AUTOINCREMENT, object_type TEXT NOT NULL, object_id INTEGER NOT NULL, start_toggle INTEGER NOT NULL CHECK(start_toggle IN (0,1)), end_toggle INTEGER NOT NULL CHECK(end_toggle IN (0,1)))");
    }

    // Called when DB version is upgraded meaning schema changes
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS assessments");
        db.execSQL("DROP TABLE IF EXISTS courses");
        db.execSQL("DROP TABLE IF EXISTS terms");
        db.execSQL("DROP TABLE IF EXISTS alerts");
        onCreate(db);
    }
}
