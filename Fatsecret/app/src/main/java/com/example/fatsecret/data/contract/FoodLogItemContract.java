package com.example.fatsecret.data.contract;

import android.provider.BaseColumns;

public final class FoodLogItemContract {
    private FoodLogItemContract() {}

    public static class FoodLogItemEntry implements BaseColumns {
        public static final String TABLE_NAME = "food_log_items";
        public static final String COLUMN_ID = "id";
        public static final String COLUMN_FOOD_LOG_ID = "food_log_id"; // FK ke food_logs
        public static final String COLUMN_INGREDIENT_ID = "ingredient_id"; // FK ke ingredients
        public static final String COLUMN_WEIGHT_GRAMS = "weight_grams"; // berat yang dimakan
        public static final String COLUMN_CALCULATED_CALORIES = "calculated_calories"; // kalori sesuai weight
        public static final String COLUMN_CALCULATED_PROTEIN = "calculated_protein"; // protein sesuai weight
        public static final String COLUMN_CALCULATED_CARBS = "calculated_carbs"; // carbs sesuai weight
        public static final String COLUMN_CALCULATED_FAT = "calculated_fat"; // fat sesuai weight
        public static final String COLUMN_CREATED_AT = "created_at";
        public static final String COLUMN_UPDATED_AT = "updated_at";
    }

    // SQL CREATE TABLE
    public static final String SQL_CREATE_TABLE =
            "CREATE TABLE " + FoodLogItemEntry.TABLE_NAME + " (" +
                    FoodLogItemEntry.COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                    FoodLogItemEntry.COLUMN_FOOD_LOG_ID + " INTEGER NOT NULL," +
                    FoodLogItemEntry.COLUMN_INGREDIENT_ID + " INTEGER NOT NULL," +
                    FoodLogItemEntry.COLUMN_WEIGHT_GRAMS + " REAL NOT NULL," +
                    FoodLogItemEntry.COLUMN_CALCULATED_CALORIES + " REAL DEFAULT 0," +
                    FoodLogItemEntry.COLUMN_CALCULATED_PROTEIN + " REAL DEFAULT 0," +
                    FoodLogItemEntry.COLUMN_CALCULATED_CARBS + " REAL DEFAULT 0," +
                    FoodLogItemEntry.COLUMN_CALCULATED_FAT + " REAL DEFAULT 0," +
                    FoodLogItemEntry.COLUMN_CREATED_AT + " TEXT NOT NULL," +
                    FoodLogItemEntry.COLUMN_UPDATED_AT + " TEXT NOT NULL," +
                    "FOREIGN KEY(" + FoodLogItemEntry.COLUMN_FOOD_LOG_ID + ") REFERENCES " +
                    FoodLogContract.FoodLogEntry.TABLE_NAME + "(" + FoodLogContract.FoodLogEntry.COLUMN_ID + ") ON DELETE CASCADE," +
                    "FOREIGN KEY(" + FoodLogItemEntry.COLUMN_INGREDIENT_ID + ") REFERENCES " +
                    IngredientContract.IngredientEntry.TABLE_NAME + "(" + IngredientContract.IngredientEntry.COLUMN_ID + ") ON DELETE CASCADE)";
    // SQL DROP TABLE
    public static final String SQL_DROP_TABLE =
            "DROP TABLE IF EXISTS " + FoodLogItemEntry.TABLE_NAME;
}