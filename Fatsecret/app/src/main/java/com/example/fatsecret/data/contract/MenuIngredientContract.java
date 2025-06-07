package com.example.fatsecret.data.contract;

import android.provider.BaseColumns;

public final class MenuIngredientContract {
    private MenuIngredientContract() {}

    public static class MenuIngredientEntry implements BaseColumns {
        public static final String TABLE_NAME = "menu_ingredients";
        public static final String COLUMN_ID = "id";
        public static final String COLUMN_MENU_ID = "menu_id";
        public static final String COLUMN_INGREDIENT_ID = "ingredient_id";
        public static final String COLUMN_QUANTITY_IN_GRAMS = "quantity_in_grams";
        public static final String COLUMN_CREATED_AT = "created_at";
    }

    // SQL CREATE TABLE
    public static final String SQL_CREATE_TABLE =
            "CREATE TABLE " + MenuIngredientEntry.TABLE_NAME + " (" +
                    MenuIngredientEntry.COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                    MenuIngredientEntry.COLUMN_MENU_ID + " INTEGER NOT NULL," +
                    MenuIngredientEntry.COLUMN_INGREDIENT_ID + " INTEGER NOT NULL," +
                    MenuIngredientEntry.COLUMN_QUANTITY_IN_GRAMS + " REAL NOT NULL," +
                    MenuIngredientEntry.COLUMN_CREATED_AT + " TEXT NOT NULL," +
                    "FOREIGN KEY(" + MenuIngredientEntry.COLUMN_MENU_ID + ") REFERENCES " +
                    MenuContract.MenuEntry.TABLE_NAME + "(" + MenuContract.MenuEntry.COLUMN_ID + ") ON DELETE CASCADE," +
                    "FOREIGN KEY(" + MenuIngredientEntry.COLUMN_INGREDIENT_ID + ") REFERENCES " +
                    IngredientContract.IngredientEntry.TABLE_NAME + "(" + IngredientContract.IngredientEntry.COLUMN_ID + ") ON DELETE CASCADE)";

    // SQL DROP TABLE
    public static final String SQL_DROP_TABLE =
            "DROP TABLE IF EXISTS " + MenuIngredientEntry.TABLE_NAME;
}