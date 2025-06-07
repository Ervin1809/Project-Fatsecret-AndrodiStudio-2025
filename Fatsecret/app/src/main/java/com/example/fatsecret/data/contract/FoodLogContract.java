package com.example.fatsecret.data.contract;

import android.provider.BaseColumns;

public final class FoodLogContract {
    private FoodLogContract() {}

    public static class FoodLogEntry implements BaseColumns {
        public static final String TABLE_NAME = "food_logs";
        public static final String COLUMN_ID = "id";
        public static final String COLUMN_USER_PROFILE_ID = "user_profile_id";
        public static final String COLUMN_MEAL_TIME = "meal_time";
        public static final String COLUMN_NOTE = "note";
        public static final String COLUMN_CREATED_AT = "created_at";
        public static final String COLUMN_UPDATED_AT = "updated_at";
    }

    public static final String SQL_CREATE_TABLE =
            "CREATE TABLE " + FoodLogEntry.TABLE_NAME + " (" +
                    FoodLogEntry.COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    FoodLogEntry.COLUMN_USER_PROFILE_ID + " INTEGER, " +
                    FoodLogEntry.COLUMN_MEAL_TIME + " TEXT, " +
                    FoodLogEntry.COLUMN_NOTE + " TEXT, " +
                    FoodLogEntry.COLUMN_CREATED_AT + " TEXT, " +
                    FoodLogEntry.COLUMN_UPDATED_AT + " TEXT, " +
                    "FOREIGN KEY(" + FoodLogEntry.COLUMN_USER_PROFILE_ID + ") REFERENCES user_profiles(id) ON DELETE CASCADE" +
                    ");";

    public static final String SQL_DROP_TABLE =
            "DROP TABLE IF EXISTS " + FoodLogEntry.TABLE_NAME;
}