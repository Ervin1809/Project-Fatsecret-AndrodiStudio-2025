package com.example.projectfinal.DAO;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.projectfinal.database.DatabaseHelper;
import com.example.projectfinal.models.User;

import java.util.ArrayList;
import java.util.List;

public class UserDAO {
    private SQLiteDatabase db;

    public UserDAO(Context context) {
        DatabaseHelper dbHelper = new DatabaseHelper(context);
        db = dbHelper.getWritableDatabase();
    }

    // Insert user
    public long insertUser(User user) {
        ContentValues values = new ContentValues();
        values.put("name", user.getName());
        values.put("email", user.getEmail());
        values.put("password", user.getPassword());
        values.put("role", user.getRole());
        values.put("profile_picture", user.getProfilePicture());
        values.put("created_at", user.getCreatedAt());
        values.put("updated_at", user.getUpdatedAt());

        return db.insert("users", null, values);
    }

    // Get user by email (for login)
    public User getUserByEmail(String email) {
        Cursor cursor = db.query("users", null, "email = ?", new String[]{email}, null, null, null);
        if (cursor != null && cursor.moveToFirst()) {
            User user = extractUserFromCursor(cursor);
            cursor.close();
            return user;
        }
        return null;
    }

    public User getUserByEmailAndPassword(String email, String password) {
        Cursor cursor = db.query("users", null,
                "email=? AND password=?", new String[]{email, password},
                null, null, null);
        if (cursor != null && cursor.moveToFirst()) {
            User user = extractUserFromCursor(cursor);
            cursor.close();
            return user;
        }
        return null;
    }


    // Get all users
    public List<User> getAllUsers() {
        List<User> users = new ArrayList<>();
        Cursor cursor = db.query("users", null, null, null, null, null, "id DESC");
        if (cursor.moveToFirst()) {
            do {
                users.add(extractUserFromCursor(cursor));
            } while (cursor.moveToNext());
        }
        cursor.close();
        return users;
    }

    // Update user
    public int updateUser(User user) {
        ContentValues values = new ContentValues();
        values.put("name", user.getName());
        values.put("email", user.getEmail());
        values.put("password", user.getPassword());
        values.put("profile_picture", user.getProfilePicture());
        values.put("updated_at", user.getUpdatedAt());

        return db.update("users", values, "id = ?", new String[]{String.valueOf(user.getId())});
    }

    // Delete user
    public int deleteUser(int userId) {
        return db.delete("users", "id = ?", new String[]{String.valueOf(userId)});
    }

    // Helper untuk convert Cursor ke User
    private User extractUserFromCursor(Cursor cursor) {
        User user = new User();
        user.setId(cursor.getInt(cursor.getColumnIndexOrThrow("id")));
        user.setName(cursor.getString(cursor.getColumnIndexOrThrow("name")));
        user.setEmail(cursor.getString(cursor.getColumnIndexOrThrow("email")));
        user.setPassword(cursor.getString(cursor.getColumnIndexOrThrow("password")));
        user.setRole(cursor.getString(cursor.getColumnIndexOrThrow("role")));
        user.setProfilePicture(cursor.getString(cursor.getColumnIndexOrThrow("profile_picture")));
        user.setCreatedAt(cursor.getString(cursor.getColumnIndexOrThrow("created_at")));
        user.setUpdatedAt(cursor.getString(cursor.getColumnIndexOrThrow("updated_at")));
        return user;
    }
}
