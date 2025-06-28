package com.example.uniblazerorganizer.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.uniblazerorganizer.models.Alert;

public class AlertDAO {
    private final SQLiteDatabase db;

    public AlertDAO(Context context) {
        db = DatabaseHelper.getInstance(context).getWritableDatabase();
    }

    public long insertAlert(Alert alert) {
        ContentValues values = new ContentValues();
        values.put("object_type", alert.getObjectType());
        values.put("object_id", alert.getObjectId());
        values.put("start_toggle", alert.isStartToggle() ? 1 : 0);
        values.put("end_toggle", alert.isEndToggle() ? 1 : 0);
        return db.insert("alerts", null, values);
    }


    public Alert getAlert(String objectType, int objectId) {
        Alert alert = null;
        Cursor cursor = db.query("alerts", null, "object_type = ? AND object_id = ?", new String[]{objectType, String.valueOf(objectId)}, null, null, null);

        if (cursor.moveToFirst()) {
            alert = new Alert();
            alert.setId(cursor.getInt(cursor.getColumnIndexOrThrow("id")));
            alert.setObjectType(cursor.getString(cursor.getColumnIndexOrThrow("object_type")));
            alert.setObjectId(cursor.getInt(cursor.getColumnIndexOrThrow("object_id")));
            alert.setStartToggle(cursor.getInt(cursor.getColumnIndexOrThrow("start_toggle")) == 1);
            alert.setEndToggle(cursor.getInt(cursor.getColumnIndexOrThrow("end_toggle")) == 1);
        }

        cursor.close();
        return alert;
    }

    public Alert getAlertById(int id, String objectType, int objectId) {
        Alert alert = null;
        Cursor cursor = db.query("alerts", null, "id = ? AND object_type = ? AND object_id = ?", new String[]{String.valueOf(id), objectType, String.valueOf(objectId)}, null, null, null);

        if (cursor.moveToFirst()) {
            alert = new Alert();
            alert.setId(cursor.getInt(cursor.getColumnIndexOrThrow("id")));
            alert.setObjectType(cursor.getString(cursor.getColumnIndexOrThrow("object_type")));
            alert.setObjectId(cursor.getInt(cursor.getColumnIndexOrThrow("object_id")));
            alert.setStartToggle(cursor.getInt(cursor.getColumnIndexOrThrow("start_toggle")) == 1);
            alert.setEndToggle(cursor.getInt(cursor.getColumnIndexOrThrow("end_toggle")) == 1);
        }

        cursor.close();
        return alert;
    }

    public int updateAlert(Alert alert) {
        ContentValues values = new ContentValues();
        values.put("start_toggle", alert.isStartToggle() ? 1 : 0);
        values.put("end_toggle", alert.isEndToggle() ? 1 : 0);
        return db.update("alerts", values, "object_type = ? AND object_id = ?", new String[]{alert.getObjectType(), String.valueOf(alert.getObjectId())});
    }
}
