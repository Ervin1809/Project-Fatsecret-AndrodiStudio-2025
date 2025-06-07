package com.example.fatsecret.data.helper;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.fatsecret.data.DatabaseHelper;
import com.example.fatsecret.data.MappingHelper;
import com.example.fatsecret.data.contract.UserProfileContract;
import com.example.fatsecret.data.model.UserProfile;

import java.util.List;

public class UserProfileHelper {
    private final DatabaseHelper dbHelper;

    public UserProfileHelper(Context context) {
        dbHelper = new DatabaseHelper(context);
    }

    public long insert(UserProfile data) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(UserProfileContract.UserProfileEntry.COLUMN_USER_ID, data.getUserId());
        values.put(UserProfileContract.UserProfileEntry.COLUMN_HEIGHT, data.getHeight());
        values.put(UserProfileContract.UserProfileEntry.COLUMN_WEIGHT, data.getWeight());
        values.put(UserProfileContract.UserProfileEntry.COLUMN_TARGET_WEIGHT, data.getTargetWeight());
        values.put(UserProfileContract.UserProfileEntry.COLUMN_CREATED_AT, data.getCreatedAt());
        values.put(UserProfileContract.UserProfileEntry.COLUMN_UPDATED_AT, data.getUpdatedAt());
        long id = db.insert(UserProfileContract.UserProfileEntry.TABLE_NAME, null, values);
        db.close();
        return id;
    }

    public UserProfile getById(int id) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query(
                UserProfileContract.UserProfileEntry.TABLE_NAME,
                null,
                UserProfileContract.UserProfileEntry.COLUMN_ID + "=?",
                new String[]{String.valueOf(id)},
                null, null, null
        );
        UserProfile data = null;
        if (cursor != null && cursor.moveToFirst()) {
            data = MappingHelper.mapCursorToUserProfile(cursor);
            cursor.close();
        }
        db.close();
        return data;
    }

    public List<UserProfile> getAll() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query(UserProfileContract.UserProfileEntry.TABLE_NAME, null, null, null, null, null, null);
        List<UserProfile> list = MappingHelper.mapCursorToUserProfileList(cursor);
        db.close();
        return list;
    }

    public int update(UserProfile data) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(UserProfileContract.UserProfileEntry.COLUMN_USER_ID, data.getUserId());
        values.put(UserProfileContract.UserProfileEntry.COLUMN_HEIGHT, data.getHeight());
        values.put(UserProfileContract.UserProfileEntry.COLUMN_WEIGHT, data.getWeight());
        values.put(UserProfileContract.UserProfileEntry.COLUMN_TARGET_WEIGHT, data.getTargetWeight());
        values.put(UserProfileContract.UserProfileEntry.COLUMN_CREATED_AT, data.getCreatedAt());
        values.put(UserProfileContract.UserProfileEntry.COLUMN_UPDATED_AT, data.getUpdatedAt());
        int rows = db.update(
                UserProfileContract.UserProfileEntry.TABLE_NAME,
                values,
                UserProfileContract.UserProfileEntry.COLUMN_ID + "=?",
                new String[]{String.valueOf(data.getId())}
        );
        db.close();
        return rows;
    }

    public int delete(int id) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        int rows = db.delete(
                UserProfileContract.UserProfileEntry.TABLE_NAME,
                UserProfileContract.UserProfileEntry.COLUMN_ID + "=?",
                new String[]{String.valueOf(id)}
        );
        db.close();
        return rows;
    }
}
