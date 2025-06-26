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
        db = DatabaseHelper.getInstance(context).getWritableDatabase();
    }

    public void close(){
        db.close();
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
//            TODO: Conver to buildcursor like coursesdao
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

    public Term getTermById(int id) {
        Term term = null;
        Cursor cursor = db.query(
                "terms",
                null,
                "id = ?",
                new String[]{String.valueOf(id)},
                null,
                null,
                null
        );

        if (cursor.moveToFirst()) {
            term = new Term();
            term.setId(cursor.getInt(cursor.getColumnIndexOrThrow("id")));
            term.setTitle(cursor.getString(cursor.getColumnIndexOrThrow("title")));
            term.setStartDate(cursor.getString(cursor.getColumnIndexOrThrow("start_date")));
            term.setEndDate(cursor.getString(cursor.getColumnIndexOrThrow("end_date")));
        }

        cursor.close();
        return term;
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
