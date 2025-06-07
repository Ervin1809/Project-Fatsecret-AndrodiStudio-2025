package com.example.fatsecret.data.contract;

import android.provider.BaseColumns;

public final class IngredientContract {
    private IngredientContract() {}

    public static class IngredientEntry implements BaseColumns {
        public static final String TABLE_NAME = "ingredients";
        public static final String COLUMN_ID = "id";
        public static final String COLUMN_NAME = "name";
        public static final String COLUMN_CALORIES_PER_100G = "calories_per_100g";
        public static final String COLUMN_PROTEIN_PER_100G = "protein_per_100g";
        public static final String COLUMN_CARBS_PER_100G = "carbs_per_100g";
        public static final String COLUMN_FAT_PER_100G = "fat_per_100g";
        public static final String COLUMN_CREATED_AT = "created_at";
        public static final String COLUMN_UPDATED_AT = "updated_at";
    }

    public static final String SQL_CREATE_TABLE =
            "CREATE TABLE " + IngredientEntry.TABLE_NAME + " (" +
                    IngredientEntry.COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    IngredientEntry.COLUMN_NAME + " TEXT, " +
                    IngredientEntry.COLUMN_CALORIES_PER_100G + " REAL, " +
                    IngredientEntry.COLUMN_PROTEIN_PER_100G + " REAL, " +
                    IngredientEntry.COLUMN_CARBS_PER_100G + " REAL, " +
                    IngredientEntry.COLUMN_FAT_PER_100G + " REAL, " +
                    IngredientEntry.COLUMN_CREATED_AT + " TEXT, " +
                    IngredientEntry.COLUMN_UPDATED_AT + " TEXT" +
                    ");";

    public static final String SQL_DROP_TABLE =
            "DROP TABLE IF EXISTS " + IngredientEntry.TABLE_NAME;
}
