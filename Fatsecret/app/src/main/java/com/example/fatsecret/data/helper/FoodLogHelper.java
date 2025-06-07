package com.example.fatsecret.data.helper;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.fatsecret.data.DatabaseHelper;
import com.example.fatsecret.data.MappingHelper;
import com.example.fatsecret.data.contract.FoodLogContract;
import com.example.fatsecret.data.model.FoodLog;

import java.util.List;

public class FoodLogHelper {
    private final DatabaseHelper dbHelper;

    public FoodLogHelper(Context context) {
        dbHelper = new DatabaseHelper(context);
    }

    public long insert(FoodLog data) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(FoodLogContract.FoodLogEntry.COLUMN_USER_PROFILE_ID, data.getUserProfileId());
        values.put(FoodLogContract.FoodLogEntry.COLUMN_MEAL_TIME, data.getMealTime());
        values.put(FoodLogContract.FoodLogEntry.COLUMN_NOTE, data.getNote());
        values.put(FoodLogContract.FoodLogEntry.COLUMN_CREATED_AT, data.getCreatedAt());
        values.put(FoodLogContract.FoodLogEntry.COLUMN_UPDATED_AT, data.getUpdatedAt());
        long id = db.insert(FoodLogContract.FoodLogEntry.TABLE_NAME, null, values);
        db.close();
        return id;
    }

    public FoodLog getById(int id) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query(
                FoodLogContract.FoodLogEntry.TABLE_NAME,
                null,
                FoodLogContract.FoodLogEntry.COLUMN_ID + "=?",
                new String[]{String.valueOf(id)},
                null, null, null
        );
        FoodLog data = null;
        if (cursor != null && cursor.moveToFirst()) {
            data = MappingHelper.mapCursorToFoodLog(cursor);
            cursor.close();
        }
        db.close();
        return data;
    }

    public List<FoodLog> getAll() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query(FoodLogContract.FoodLogEntry.TABLE_NAME, null, null, null, null, null, null);
        List<FoodLog> list = MappingHelper.mapCursorToFoodLogList(cursor);
        db.close();
        return list;
    }

    public int update(FoodLog data) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(FoodLogContract.FoodLogEntry.COLUMN_USER_PROFILE_ID, data.getUserProfileId());
        values.put(FoodLogContract.FoodLogEntry.COLUMN_MEAL_TIME, data.getMealTime());
        values.put(FoodLogContract.FoodLogEntry.COLUMN_NOTE, data.getNote());
        values.put(FoodLogContract.FoodLogEntry.COLUMN_CREATED_AT, data.getCreatedAt());
        values.put(FoodLogContract.FoodLogEntry.COLUMN_UPDATED_AT, data.getUpdatedAt());
        int rows = db.update(
                FoodLogContract.FoodLogEntry.TABLE_NAME,
                values,
                FoodLogContract.FoodLogEntry.COLUMN_ID + "=?",
                new String[]{String.valueOf(data.getId())}
        );
        db.close();
        return rows;
    }

    public int delete(int id) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        int rows = db.delete(
                FoodLogContract.FoodLogEntry.TABLE_NAME,
                FoodLogContract.FoodLogEntry.COLUMN_ID + "=?",
                new String[]{String.valueOf(id)}
        );
        db.close();
        return rows;
    }
}
