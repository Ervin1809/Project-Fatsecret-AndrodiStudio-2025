package com.example.fatsecret.data.contract;

import android.provider.BaseColumns;

public final class UserContract {
    private UserContract() {}

    public static class UserEntry implements BaseColumns {
        public static final String TABLE_NAME = "users";
        public static final String COLUMN_ID = "id"; // Added
        public static final String COLUMN_NAME = "name";
        public static final String COLUMN_EMAIL = "email";
        public static final String COLUMN_PASSWORD = "password";
        public static final String COLUMN_ROLE = "role";
        public static final String COLUMN_PROFILE_PICTURE = "profile_picture";
        public static final String COLUMN_CREATED_AT = "created_at";
        public static final String COLUMN_UPDATED_AT = "updated_at";
    }

    // SQL CREATE TABLE
    public static final String SQL_CREATE_TABLE =
            "CREATE TABLE " + UserEntry.TABLE_NAME + " (" +
                    UserEntry.COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                    UserEntry.COLUMN_NAME + " TEXT NOT NULL," +
                    UserEntry.COLUMN_EMAIL + " TEXT UNIQUE NOT NULL," +
                    UserEntry.COLUMN_PASSWORD + " TEXT NOT NULL," +
                    UserEntry.COLUMN_ROLE + " TEXT DEFAULT 'user'," +
                    UserEntry.COLUMN_PROFILE_PICTURE + " TEXT," +
                    UserEntry.COLUMN_CREATED_AT + " TEXT NOT NULL," +
                    UserEntry.COLUMN_UPDATED_AT + " TEXT NOT NULL)";

    // SQL DROP TABLE
    public static final String SQL_DROP_TABLE =
            "DROP TABLE IF EXISTS " + UserEntry.TABLE_NAME;
}