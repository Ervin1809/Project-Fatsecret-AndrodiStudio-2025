package com.example.fatsecret.data.helper;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.fatsecret.data.DatabaseHelper;
import com.example.fatsecret.data.MappingHelper;
import com.example.fatsecret.data.contract.UserContract;
import com.example.fatsecret.data.model.User;

import java.util.List;

public class UserHelper {
    private final DatabaseHelper dbHelper;

    public UserHelper(Context context) {
        dbHelper = new DatabaseHelper(context);
    }

    public long insertUser(User user) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(UserContract.UserEntry.COLUMN_NAME, user.getName());
        values.put(UserContract.UserEntry.COLUMN_EMAIL, user.getEmail());
        values.put(UserContract.UserEntry.COLUMN_PASSWORD, user.getPassword());
        values.put(UserContract.UserEntry.COLUMN_ROLE, user.getRole());
        values.put(UserContract.UserEntry.COLUMN_PROFILE_PICTURE, user.getProfilePicture());
        values.put(UserContract.UserEntry.COLUMN_CREATED_AT, user.getCreatedAt());
        values.put(UserContract.UserEntry.COLUMN_UPDATED_AT, user.getUpdatedAt());
        long id = db.insert(UserContract.UserEntry.TABLE_NAME, null, values);
        db.close();
        return id;
    }

    public User getUserById(int id) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query(
                UserContract.UserEntry.TABLE_NAME,
                null,
                UserContract.UserEntry.COLUMN_ID + "=?",
                new String[]{String.valueOf(id)},
                null, null, null
        );
        User user = null;
        if (cursor != null && cursor.moveToFirst()) {
            user = MappingHelper.mapCursorToUser(cursor);
            cursor.close();
        }
        db.close();
        return user;
    }

    public List<User> getAllUsers() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query(UserContract.UserEntry.TABLE_NAME, null, null, null, null, null, null);
        List<User> userList = MappingHelper.mapCursorToUserList(cursor);
        db.close();
        return userList;
    }

    public int updateUser(User user) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(UserContract.UserEntry.COLUMN_NAME, user.getName());
        values.put(UserContract.UserEntry.COLUMN_EMAIL, user.getEmail());
        values.put(UserContract.UserEntry.COLUMN_PASSWORD, user.getPassword());
        values.put(UserContract.UserEntry.COLUMN_ROLE, user.getRole());
        values.put(UserContract.UserEntry.COLUMN_PROFILE_PICTURE, user.getProfilePicture());
        values.put(UserContract.UserEntry.COLUMN_CREATED_AT, user.getCreatedAt());
        values.put(UserContract.UserEntry.COLUMN_UPDATED_AT, user.getUpdatedAt());
        int rows = db.update(
                UserContract.UserEntry.TABLE_NAME,
                values,
                UserContract.UserEntry.COLUMN_ID + "=?",
                new String[]{String.valueOf(user.getId())}
        );
        db.close();
        return rows;
    }

    public int deleteUser(int id) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        int rows = db.delete(
                UserContract.UserEntry.TABLE_NAME,
                UserContract.UserEntry.COLUMN_ID + "=?",
                new String[]{String.valueOf(id)}
        );
        db.close();
        return rows;
    }
}