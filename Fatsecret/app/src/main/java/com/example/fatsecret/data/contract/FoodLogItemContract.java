package com.example.fatsecret.data.contract;

import android.provider.BaseColumns;

public final class FoodLogItemContract {
    private FoodLogItemContract() {}

    public static class FoodLogItemEntry implements BaseColumns {
        public static final String TABLE_NAME = "food_log_items";
        public static final String COLUMN_ID = "id";
        public static final String COLUMN_FOOD_LOG_ID = "food_log_id";
        public static final String COLUMN_TYPE = "type";
        public static final String COLUMN_MENU_ID = "menu_id";
        public static final String COLUMN_INGREDIENT_ID = "ingredient_id";
        public static final String COLUMN_QUANTITY_IN_GRAMS = "quantity_in_grams";
        public static final String COLUMN_CALORIES = "calories";
        public static final String COLUMN_PROTEIN = "protein";
        public static final String COLUMN_CARBS = "carbs";
        public static final String COLUMN_FAT = "fat";
    }

    public static final String SQL_CREATE_TABLE =
            "CREATE TABLE " + FoodLogItemEntry.TABLE_NAME + " (" +
                    FoodLogItemEntry.COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    FoodLogItemEntry.COLUMN_FOOD_LOG_ID + " INTEGER, " +
                    FoodLogItemEntry.COLUMN_TYPE + " TEXT, " +
                    FoodLogItemEntry.COLUMN_MENU_ID + " INTEGER, " +
                    FoodLogItemEntry.COLUMN_INGREDIENT_ID + " INTEGER, " +
                    FoodLogItemEntry.COLUMN_QUANTITY_IN_GRAMS + " REAL, " +
                    FoodLogItemEntry.COLUMN_CALORIES + " REAL, " +
                    FoodLogItemEntry.COLUMN_PROTEIN + " REAL, " +
                    FoodLogItemEntry.COLUMN_CARBS + " REAL, " +
                    FoodLogItemEntry.COLUMN_FAT + " REAL, " +
                    "FOREIGN KEY(" + FoodLogItemEntry.COLUMN_FOOD_LOG_ID + ") REFERENCES food_logs(id) ON DELETE CASCADE, " +
                    "FOREIGN KEY(" + FoodLogItemEntry.COLUMN_MENU_ID + ") REFERENCES menus(id) ON DELETE SET NULL, " +
                    "FOREIGN KEY(" + FoodLogItemEntry.COLUMN_INGREDIENT_ID + ") REFERENCES ingredients(id) ON DELETE SET NULL" +
                    ");";

    public static final String SQL_DROP_TABLE =
            "DROP TABLE IF EXISTS " + FoodLogItemEntry.TABLE_NAME;
}
