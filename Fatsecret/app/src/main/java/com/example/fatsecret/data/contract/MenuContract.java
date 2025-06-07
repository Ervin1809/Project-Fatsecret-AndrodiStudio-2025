package com.example.fatsecret.data.contract;

import android.provider.BaseColumns;

public final class MenuContract {
    private MenuContract() {}

    public static class MenuEntry implements BaseColumns {
        public static final String TABLE_NAME = "menus";
        public static final String COLUMN_ID = "id";
        public static final String COLUMN_USER_ID = "user_id";
        public static final String COLUMN_NAME = "name";
        public static final String COLUMN_DESCRIPTION = "description";
        public static final String COLUMN_TOTAL_CALORIES = "total_calories";
        public static final String COLUMN_TOTAL_PROTEIN = "total_protein";
        public static final String COLUMN_TOTAL_CARBS = "total_carbs";
        public static final String COLUMN_TOTAL_FAT = "total_fat";
        public static final String COLUMN_CREATED_BY = "created_by";
        public static final String COLUMN_CREATED_AT = "created_at";
        public static final String COLUMN_UPDATED_AT = "updated_at";
    }

    // SQL CREATE TABLE
    public static final String SQL_CREATE_TABLE =
            "CREATE TABLE " + MenuEntry.TABLE_NAME + " (" +
                    MenuEntry.COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                    MenuEntry.COLUMN_USER_ID + " INTEGER NOT NULL," +
                    MenuEntry.COLUMN_NAME + " TEXT NOT NULL," +
                    MenuEntry.COLUMN_DESCRIPTION + " TEXT," +
                    MenuEntry.COLUMN_TOTAL_CALORIES + " REAL DEFAULT 0," +
                    MenuEntry.COLUMN_TOTAL_PROTEIN + " REAL DEFAULT 0," +
                    MenuEntry.COLUMN_TOTAL_CARBS + " REAL DEFAULT 0," +
                    MenuEntry.COLUMN_TOTAL_FAT + " REAL DEFAULT 0," +
                    MenuEntry.COLUMN_CREATED_BY + " INTEGER," +
                    MenuEntry.COLUMN_CREATED_AT + " TEXT NOT NULL," +
                    MenuEntry.COLUMN_UPDATED_AT + " TEXT NOT NULL," +
                    "FOREIGN KEY(" + MenuEntry.COLUMN_USER_ID + ") REFERENCES " +
                    UserContract.UserEntry.TABLE_NAME + "(" + UserContract.UserEntry.COLUMN_ID + ") ON DELETE CASCADE," +
                    "FOREIGN KEY(" + MenuEntry.COLUMN_CREATED_BY + ") REFERENCES " +
                    UserContract.UserEntry.TABLE_NAME + "(" + UserContract.UserEntry.COLUMN_ID + ") ON DELETE SET NULL)";

    // SQL DROP TABLE
    public static final String SQL_DROP_TABLE =
            "DROP TABLE IF EXISTS " + MenuEntry.TABLE_NAME;
}