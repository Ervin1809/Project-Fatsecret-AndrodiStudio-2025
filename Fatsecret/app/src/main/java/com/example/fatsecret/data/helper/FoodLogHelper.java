package com.example.fatsecret.data.helper;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.example.fatsecret.data.DatabaseHelper;
import com.example.fatsecret.data.MappingHelper;
import com.example.fatsecret.data.contract.FoodLogContract;
import com.example.fatsecret.data.model.FoodLog;

import java.util.ArrayList;
import java.util.List;

public class FoodLogHelper {
    private static final String TAG = "FoodLogHelper";
    private final DatabaseHelper dbHelper;

    public FoodLogHelper(Context context) {
        dbHelper = new DatabaseHelper(context);
    }

    // ✅ EXISTING METHODS (keep as is)
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

    // ✅ FIXED: Direct user_id approach (no JOIN needed)
    public List<FoodLog> getFoodLogsByDateSync(String date, int userId) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        List<FoodLog> foodLogs = new ArrayList<>();

        try {
            Log.d(TAG, "🔍 Querying food logs for date: " + date + ", userId: " + userId);

            // ✅ SIMPLE: Direct query using user_id (no JOIN needed)
            String selection = "user_id = ? AND DATE(" + FoodLogContract.FoodLogEntry.COLUMN_CREATED_AT + ") = ?";
            String[] selectionArgs = {String.valueOf(userId), date};

            Cursor cursor = db.query(
                    FoodLogContract.FoodLogEntry.TABLE_NAME,
                    null,
                    selection,
                    selectionArgs,
                    null,
                    null,
                    FoodLogContract.FoodLogEntry.COLUMN_CREATED_AT + " ASC"
            );

            if (cursor != null) {
                Log.d(TAG, "📊 Found " + cursor.getCount() + " food logs for " + date);
                foodLogs = MappingHelper.mapCursorToFoodLogList(cursor);
                cursor.close();
            }

        } catch (Exception e) {
            Log.e(TAG, "❌ Error getting food logs by date: " + e.getMessage(), e);
        } finally {
            db.close();
        }

        Log.d(TAG, "✅ Returning " + foodLogs.size() + " food logs for " + date);
        return foodLogs;
    }

    // ✅ FIXED: Month query also using direct user_id
    public List<FoodLog> getFoodLogsForMonthSync(int year, int month, int userId) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        List<FoodLog> foodLogs = new ArrayList<>();

        try {
            Log.d(TAG, "🗓️ Querying food logs for month: " + year + "-" + String.format("%02d", month) + ", userId: " + userId);

            // Create date pattern for the month (e.g., "2025-06%" for June 2025)
            String monthPattern = String.format("%04d-%02d%%", year, month);

            // ✅ SIMPLE: Direct query using user_id
            String selection = "user_id = ? AND DATE(" + FoodLogContract.FoodLogEntry.COLUMN_CREATED_AT + ") LIKE ?";
            String[] selectionArgs = {String.valueOf(userId), monthPattern};

            Cursor cursor = db.query(
                    FoodLogContract.FoodLogEntry.TABLE_NAME,
                    null,
                    selection,
                    selectionArgs,
                    null,
                    null,
                    FoodLogContract.FoodLogEntry.COLUMN_CREATED_AT + " ASC"
            );

            if (cursor != null) {
                Log.d(TAG, "📊 Found " + cursor.getCount() + " food logs for month " + year + "-" + month);
                foodLogs = MappingHelper.mapCursorToFoodLogList(cursor);
                cursor.close();
            }

        } catch (Exception e) {
            Log.e(TAG, "❌ Error getting food logs for month: " + e.getMessage(), e);
        } finally {
            db.close();
        }

        Log.d(TAG, "✅ Returning " + foodLogs.size() + " food logs for month " + year + "-" + month);
        return foodLogs;
    }
}