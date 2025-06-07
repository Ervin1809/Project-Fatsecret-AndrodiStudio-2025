package com.example.fatsecret.data.contract;

import android.provider.BaseColumns;

public final class FoodLogItemContract {
    private FoodLogItemContract() {}

    public static class FoodLogItemEntry implements BaseColumns {
        public static final String TABLE_NAME = "food_log_items";
        public static final String COLUMN_ID = "id"; // Added
        public static final String COLUMN_FOOD_LOG_ID = "food_log_id";
        public static final String COLUMN_INGREDIENT_ID = "ingredient_id";
        public static final String COLUMN_MENU_ID = "menu_id";
        public static final String COLUMN_TYPE = "type"; // Added
        public static final String COLUMN_QUANTITY = "quantity";
        public static final String COLUMN_QUANTITY_IN_GRAMS = "quantity_in_grams"; // Added
        public static final String COLUMN_UNIT = "unit";
        public static final String COLUMN_CALORIES = "calories";
        public static final String COLUMN_PROTEIN = "protein";
        public static final String COLUMN_CARBS = "carbs";
        public static final String COLUMN_FAT = "fat";
        public static final String COLUMN_CREATED_AT = "created_at";
    }

    // SQL CREATE TABLE
    public static final String SQL_CREATE_TABLE =
            "CREATE TABLE " + FoodLogItemEntry.TABLE_NAME + " (" +
                    FoodLogItemEntry.COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                    FoodLogItemEntry.COLUMN_FOOD_LOG_ID + " INTEGER NOT NULL," +
                    FoodLogItemEntry.COLUMN_INGREDIENT_ID + " INTEGER," +
                    FoodLogItemEntry.COLUMN_MENU_ID + " INTEGER," +
                    FoodLogItemEntry.COLUMN_TYPE + " TEXT," +
                    FoodLogItemEntry.COLUMN_QUANTITY + " REAL NOT NULL," +
                    FoodLogItemEntry.COLUMN_QUANTITY_IN_GRAMS + " REAL," +
                    FoodLogItemEntry.COLUMN_UNIT + " TEXT NOT NULL," +
                    FoodLogItemEntry.COLUMN_CALORIES + " REAL NOT NULL," +
                    FoodLogItemEntry.COLUMN_PROTEIN + " REAL NOT NULL," +
                    FoodLogItemEntry.COLUMN_CARBS + " REAL NOT NULL," +
                    FoodLogItemEntry.COLUMN_FAT + " REAL NOT NULL," +
                    FoodLogItemEntry.COLUMN_CREATED_AT + " TEXT NOT NULL," +
                    "FOREIGN KEY(" + FoodLogItemEntry.COLUMN_FOOD_LOG_ID + ") REFERENCES " +
                    FoodLogContract.FoodLogEntry.TABLE_NAME + "(" + FoodLogContract.FoodLogEntry.COLUMN_ID + ") ON DELETE CASCADE," +
                    "FOREIGN KEY(" + FoodLogItemEntry.COLUMN_INGREDIENT_ID + ") REFERENCES " +
                    IngredientContract.IngredientEntry.TABLE_NAME + "(" + IngredientContract.IngredientEntry.COLUMN_ID + ") ON DELETE CASCADE," +
                    "FOREIGN KEY(" + FoodLogItemEntry.COLUMN_MENU_ID + ") REFERENCES " +
                    MenuContract.MenuEntry.TABLE_NAME + "(" + MenuContract.MenuEntry.COLUMN_ID + ") ON DELETE CASCADE)";

    // SQL DROP TABLE
    public static final String SQL_DROP_TABLE =
            "DROP TABLE IF EXISTS " + FoodLogItemEntry.TABLE_NAME;
}