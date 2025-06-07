package com.example.fatsecret.data.contract;

import android.provider.BaseColumns;

public final class FoodLogContract {
    private FoodLogContract() {}

    public static class FoodLogEntry implements BaseColumns {
        public static final String TABLE_NAME = "food_logs";
        public static final String COLUMN_ID = "id"; // Added
        public static final String COLUMN_USER_ID = "user_id";
        public static final String COLUMN_USER_PROFILE_ID = "user_profile_id"; // Added
        public static final String COLUMN_DATE = "date";
        public static final String COLUMN_MEAL_TIME = "meal_time"; // Changed from MEAL_TYPE
        public static final String COLUMN_NOTE = "note"; // Added
        public static final String COLUMN_TOTAL_CALORIES = "total_calories";
        public static final String COLUMN_TOTAL_PROTEIN = "total_protein";
        public static final String COLUMN_TOTAL_CARBS = "total_carbs";
        public static final String COLUMN_TOTAL_FAT = "total_fat";
        public static final String COLUMN_CREATED_AT = "created_at";
        public static final String COLUMN_UPDATED_AT = "updated_at";
    }

    // SQL CREATE TABLE
    public static final String SQL_CREATE_TABLE =
            "CREATE TABLE " + FoodLogEntry.TABLE_NAME + " (" +
                    FoodLogEntry.COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                    FoodLogEntry.COLUMN_USER_ID + " INTEGER NOT NULL," +
                    FoodLogEntry.COLUMN_USER_PROFILE_ID + " INTEGER," +
                    FoodLogEntry.COLUMN_DATE + " TEXT NOT NULL," +
                    FoodLogEntry.COLUMN_MEAL_TIME + " TEXT NOT NULL," +
                    FoodLogEntry.COLUMN_NOTE + " TEXT," +
                    FoodLogEntry.COLUMN_TOTAL_CALORIES + " REAL DEFAULT 0," +
                    FoodLogEntry.COLUMN_TOTAL_PROTEIN + " REAL DEFAULT 0," +
                    FoodLogEntry.COLUMN_TOTAL_CARBS + " REAL DEFAULT 0," +
                    FoodLogEntry.COLUMN_TOTAL_FAT + " REAL DEFAULT 0," +
                    FoodLogEntry.COLUMN_CREATED_AT + " TEXT NOT NULL," +
                    FoodLogEntry.COLUMN_UPDATED_AT + " TEXT NOT NULL," +
                    "FOREIGN KEY(" + FoodLogEntry.COLUMN_USER_ID + ") REFERENCES " +
                    UserContract.UserEntry.TABLE_NAME + "(" + UserContract.UserEntry.COLUMN_ID + ") ON DELETE CASCADE," +
                    "FOREIGN KEY(" + FoodLogEntry.COLUMN_USER_PROFILE_ID + ") REFERENCES " +
                    UserProfileContract.UserProfileEntry.TABLE_NAME + "(" + UserProfileContract.UserProfileEntry.COLUMN_ID + ") ON DELETE CASCADE)";

    // SQL DROP TABLE
    public static final String SQL_DROP_TABLE =
            "DROP TABLE IF EXISTS " + FoodLogEntry.TABLE_NAME;
}