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
    }

    public static final String SQL_CREATE_TABLE =
            "CREATE TABLE " + MenuIngredientEntry.TABLE_NAME + " (" +
                    MenuIngredientEntry.COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    MenuIngredientEntry.COLUMN_MENU_ID + " INTEGER, " +
                    MenuIngredientEntry.COLUMN_INGREDIENT_ID + " INTEGER, " +
                    MenuIngredientEntry.COLUMN_QUANTITY_IN_GRAMS + " REAL, " +
                    "FOREIGN KEY(" + MenuIngredientEntry.COLUMN_MENU_ID + ") REFERENCES menus(id) ON DELETE CASCADE, " +
                    "FOREIGN KEY(" + MenuIngredientEntry.COLUMN_INGREDIENT_ID + ") REFERENCES ingredients(id) ON DELETE CASCADE" +
                    ");";

    public static final String SQL_DROP_TABLE =
            "DROP TABLE IF EXISTS " + MenuIngredientEntry.TABLE_NAME;
}
