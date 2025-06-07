package com.example.fatsecret.data.contract;

import android.provider.BaseColumns;

public final class MenuContract {
    private MenuContract() {}

    public static class MenuEntry implements BaseColumns {
        public static final String TABLE_NAME = "menus";
        public static final String COLUMN_ID = "id";
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

    public static final String SQL_CREATE_TABLE =
            "CREATE TABLE " + MenuEntry.TABLE_NAME + " (" +
                    MenuEntry.COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    MenuEntry.COLUMN_NAME + " TEXT, " +
                    MenuEntry.COLUMN_DESCRIPTION + " TEXT, " +
                    MenuEntry.COLUMN_TOTAL_CALORIES + " REAL, " +
                    MenuEntry.COLUMN_TOTAL_PROTEIN + " REAL, " +
                    MenuEntry.COLUMN_TOTAL_CARBS + " REAL, " +
                    MenuEntry.COLUMN_TOTAL_FAT + " REAL, " +
                    MenuEntry.COLUMN_CREATED_BY + " INTEGER, " +
                    MenuEntry.COLUMN_CREATED_AT + " TEXT, " +
                    MenuEntry.COLUMN_UPDATED_AT + " TEXT, " +
                    "FOREIGN KEY(" + MenuEntry.COLUMN_CREATED_BY + ") REFERENCES users(id) ON DELETE SET NULL" +
                    ");";

    public static final String SQL_DROP_TABLE =
            "DROP TABLE IF EXISTS " + MenuEntry.TABLE_NAME;
}
