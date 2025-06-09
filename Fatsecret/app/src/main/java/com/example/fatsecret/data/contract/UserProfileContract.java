package com.example.fatsecret.data.contract;

import android.provider.BaseColumns;

public final class UserProfileContract {

    private UserProfileContract() {}

    public static class UserProfileEntry implements BaseColumns {
        public static final String TABLE_NAME = "user_profiles";

        // ✅ Existing columns
        public static final String COLUMN_ID = "id";
        public static final String COLUMN_USER_ID = "user_id";
        public static final String COLUMN_HEIGHT = "height";
        public static final String COLUMN_WEIGHT = "weight";
        public static final String COLUMN_TARGET_WEIGHT = "target_weight";
        public static final String COLUMN_CREATED_AT = "created_at";
        public static final String COLUMN_UPDATED_AT = "updated_at";

        // ✅ NEW: Daily nutrition targets columns
        public static final String COLUMN_DAILY_CALORIES_TARGET = "daily_calories_target";
        public static final String COLUMN_DAILY_PROTEIN_TARGET = "daily_protein_target";
        public static final String COLUMN_DAILY_CARBS_TARGET = "daily_carbs_target";
        public static final String COLUMN_DAILY_FAT_TARGET = "daily_fat_target";

        // ✅ NEW: User profile data columns
        public static final String COLUMN_GENDER = "gender";
        public static final String COLUMN_AGE = "age";
        public static final String COLUMN_ACTIVITY_LEVEL = "activity_level";
    }

    // ✅ Updated SQL CREATE TABLE statement
    public static final String SQL_CREATE_TABLE =
            "CREATE TABLE " + UserProfileEntry.TABLE_NAME + " (" +
                    UserProfileEntry.COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                    UserProfileEntry.COLUMN_USER_ID + " INTEGER NOT NULL," +
                    UserProfileEntry.COLUMN_HEIGHT + " REAL NOT NULL," +
                    UserProfileEntry.COLUMN_WEIGHT + " REAL NOT NULL," +
                    UserProfileEntry.COLUMN_TARGET_WEIGHT + " REAL NOT NULL," +

                    // ✅ NEW: Daily nutrition targets
                    UserProfileEntry.COLUMN_DAILY_CALORIES_TARGET + " REAL DEFAULT 0," +
                    UserProfileEntry.COLUMN_DAILY_PROTEIN_TARGET + " REAL DEFAULT 0," +
                    UserProfileEntry.COLUMN_DAILY_CARBS_TARGET + " REAL DEFAULT 0," +
                    UserProfileEntry.COLUMN_DAILY_FAT_TARGET + " REAL DEFAULT 0," +

                    // ✅ NEW: User profile data
                    UserProfileEntry.COLUMN_GENDER + " TEXT," +
                    UserProfileEntry.COLUMN_AGE + " INTEGER DEFAULT 0," +
                    UserProfileEntry.COLUMN_ACTIVITY_LEVEL + " TEXT," +

                    UserProfileEntry.COLUMN_CREATED_AT + " TEXT NOT NULL," +
                    UserProfileEntry.COLUMN_UPDATED_AT + " TEXT NOT NULL," +
                    "FOREIGN KEY(" + UserProfileEntry.COLUMN_USER_ID + ") REFERENCES users(id)" +
                    ")";

    // ✅ SQL ALTER TABLE statements for existing databases
    public static final String[] SQL_ALTER_STATEMENTS = {
            "ALTER TABLE " + UserProfileEntry.TABLE_NAME + " ADD COLUMN " +
                    UserProfileEntry.COLUMN_DAILY_CALORIES_TARGET + " REAL DEFAULT 0",

            "ALTER TABLE " + UserProfileEntry.TABLE_NAME + " ADD COLUMN " +
                    UserProfileEntry.COLUMN_DAILY_PROTEIN_TARGET + " REAL DEFAULT 0",

            "ALTER TABLE " + UserProfileEntry.TABLE_NAME + " ADD COLUMN " +
                    UserProfileEntry.COLUMN_DAILY_CARBS_TARGET + " REAL DEFAULT 0",

            "ALTER TABLE " + UserProfileEntry.TABLE_NAME + " ADD COLUMN " +
                    UserProfileEntry.COLUMN_DAILY_FAT_TARGET + " REAL DEFAULT 0",

            "ALTER TABLE " + UserProfileEntry.TABLE_NAME + " ADD COLUMN " +
                    UserProfileEntry.COLUMN_GENDER + " TEXT",

            "ALTER TABLE " + UserProfileEntry.TABLE_NAME + " ADD COLUMN " +
                    UserProfileEntry.COLUMN_AGE + " INTEGER DEFAULT 0",

            "ALTER TABLE " + UserProfileEntry.TABLE_NAME + " ADD COLUMN " +
                    UserProfileEntry.COLUMN_ACTIVITY_LEVEL + " TEXT"
    };

    public static final String SQL_DROP_TABLE =
            "DROP TABLE IF EXISTS " + UserProfileEntry.TABLE_NAME;
}