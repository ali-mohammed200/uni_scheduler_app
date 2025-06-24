package com.example.mytestapplication.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.mytestapplication.models.Term;

import java.util.ArrayList;
import java.util.List;

public class TermDAO {
    private SQLiteDatabase db;

    public TermDAO(Context context) {
        DatabaseHelper dbHelper = new DatabaseHelper(context);
        db = dbHelper.getWritableDatabase();
    }

    public long insertTerm(Term term) {
        ContentValues values = new ContentValues();
        values.put("title", term.getTitle());
        values.put("start_date", term.getStartDate());
        values.put("end_date", term.getEndDate());
        return db.insert("terms", null, values);
    }

    public List<Term> getAllTerms() {
        List<Term> terms = new ArrayList<>();
        Cursor cursor = db.query("terms", null, null, null, null, null, null);

        while (cursor.moveToNext()) {
            Term term = new Term();
            term.setId(cursor.getInt(cursor.getColumnIndexOrThrow("id")));
            term.setTitle(cursor.getString(cursor.getColumnIndexOrThrow("title")));
            term.setStartDate(cursor.getString(cursor.getColumnIndexOrThrow("start_date")));
            term.setEndDate(cursor.getString(cursor.getColumnIndexOrThrow("end_date")));
            terms.add(term);
        }

        cursor.close();
        return terms;
    }

    public int deleteTerm(int termId) {
        return db.delete("terms", "id = ?", new String[]{String.valueOf(termId)});
    }

    public int updateTerm(Term term) {
        ContentValues values = new ContentValues();
        values.put("title", term.getTitle());
        values.put("start_date", term.getStartDate());
        values.put("end_date", term.getEndDate());
        return db.update("terms", values, "id = ?", new String[]{String.valueOf(term.getId())});
    }
}
