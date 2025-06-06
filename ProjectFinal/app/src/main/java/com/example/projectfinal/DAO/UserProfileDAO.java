package com.example.projectfinal.DAO;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.projectfinal.database.DatabaseHelper;
import com.example.projectfinal.models.UserProfile;

public class UserProfileDAO {
    private SQLiteDatabase db;

    public UserProfileDAO(Context context) {
        DatabaseHelper dbHelper = new DatabaseHelper(context);
        db = dbHelper.getWritableDatabase();
    }

    // Insert user profile
    public long insertUserProfile(UserProfile profile) {
        ContentValues values = new ContentValues();
        values.put("user_id", profile.getUserId());
        values.put("height", profile.getHeight());
        values.put("weight", profile.getWeight());
        values.put("target_weight", profile.getTargetWeight());
        values.put("created_at", profile.getCreatedAt());
        values.put("updated_at", profile.getUpdatedAt());

        return db.insert("user_profiles", null, values);
    }

    // Get profile by user ID
    public UserProfile getUserProfileByUserId(int userId) {
        Cursor cursor = db.query("user_profiles", null, "user_id = ?", new String[]{String.valueOf(userId)}, null, null, null);
        if (cursor != null && cursor.moveToFirst()) {
            UserProfile profile = extractUserProfileFromCursor(cursor);
            cursor.close();
            return profile;
        }
        return null;
    }

    // Update user profile
    public int updateUserProfile(UserProfile profile) {
        ContentValues values = new ContentValues();
        values.put("height", profile.getHeight());
        values.put("weight", profile.getWeight());
        values.put("target_weight", profile.getTargetWeight());
        values.put("updated_at", profile.getUpdatedAt());

        return db.update("user_profiles", values, "user_id = ?", new String[]{String.valueOf(profile.getUserId())});
    }

    // Helper
    private UserProfile extractUserProfileFromCursor(Cursor cursor) {
        UserProfile profile = new UserProfile();
        profile.setId(cursor.getInt(cursor.getColumnIndexOrThrow("id")));
        profile.setUserId(cursor.getInt(cursor.getColumnIndexOrThrow("user_id")));
        profile.setHeight(cursor.getFloat(cursor.getColumnIndexOrThrow("height")));
        profile.setWeight(cursor.getFloat(cursor.getColumnIndexOrThrow("weight")));
        profile.setTargetWeight(cursor.getFloat(cursor.getColumnIndexOrThrow("target_weight")));
        profile.setCreatedAt(cursor.getString(cursor.getColumnIndexOrThrow("created_at")));
        profile.setUpdatedAt(cursor.getString(cursor.getColumnIndexOrThrow("updated_at")));
        return profile;
    }
}

