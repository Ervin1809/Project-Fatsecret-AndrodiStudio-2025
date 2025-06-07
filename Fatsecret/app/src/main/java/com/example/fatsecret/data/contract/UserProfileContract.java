package com.example.fatsecret.data.contract;

import android.provider.BaseColumns;

public final class UserProfileContract {
    private UserProfileContract() {}

    public static class UserProfileEntry implements BaseColumns {
        public static final String TABLE_NAME = "user_profiles";
        public static final String COLUMN_ID = "id"; // Added
        public static final String COLUMN_USER_ID = "user_id";
        public static final String COLUMN_HEIGHT = "height";
        public static final String COLUMN_WEIGHT = "weight";
        public static final String COLUMN_TARGET_WEIGHT = "target_weight";
        public static final String COLUMN_CREATED_AT = "created_at";
        public static final String COLUMN_UPDATED_AT = "updated_at";
    }

    // SQL CREATE TABLE
    public static final String SQL_CREATE_TABLE =
            "CREATE TABLE " + UserProfileEntry.TABLE_NAME + " (" +
                    UserProfileEntry.COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                    UserProfileEntry.COLUMN_USER_ID + " INTEGER NOT NULL," +
                    UserProfileEntry.COLUMN_HEIGHT + " REAL NOT NULL," +
                    UserProfileEntry.COLUMN_WEIGHT + " REAL NOT NULL," +
                    UserProfileEntry.COLUMN_TARGET_WEIGHT + " REAL NOT NULL," +
                    UserProfileEntry.COLUMN_CREATED_AT + " TEXT NOT NULL," +
                    UserProfileEntry.COLUMN_UPDATED_AT + " TEXT NOT NULL," +
                    "FOREIGN KEY(" + UserProfileEntry.COLUMN_USER_ID + ") REFERENCES " +
                    UserContract.UserEntry.TABLE_NAME + "(" + UserContract.UserEntry.COLUMN_ID + ") ON DELETE CASCADE)";

    // SQL DROP TABLE
    public static final String SQL_DROP_TABLE =
            "DROP TABLE IF EXISTS " + UserProfileEntry.TABLE_NAME;
}