package com.example.fatsecret.data.helper;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.fatsecret.data.DatabaseHelper;
import com.example.fatsecret.data.MappingHelper;
import com.example.fatsecret.data.contract.MenuIngredientContract;
import com.example.fatsecret.data.model.MenuIngredient;

import java.util.List;

public class MenuIngredientHelper {
    private final DatabaseHelper dbHelper;

    public MenuIngredientHelper(Context context) {
        dbHelper = new DatabaseHelper(context);
    }

    public long insert(MenuIngredient data) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(MenuIngredientContract.MenuIngredientEntry.COLUMN_MENU_ID, data.getMenuId());
        values.put(MenuIngredientContract.MenuIngredientEntry.COLUMN_INGREDIENT_ID, data.getIngredientId());
        values.put(MenuIngredientContract.MenuIngredientEntry.COLUMN_QUANTITY_IN_GRAMS, data.getQuantityInGrams());
        long id = db.insert(MenuIngredientContract.MenuIngredientEntry.TABLE_NAME, null, values);
        db.close();
        return id;
    }

    public MenuIngredient getById(int id) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query(
                MenuIngredientContract.MenuIngredientEntry.TABLE_NAME,
                null,
                MenuIngredientContract.MenuIngredientEntry.COLUMN_ID + "=?",
                new String[]{String.valueOf(id)},
                null, null, null
        );
        MenuIngredient data = null;
        if (cursor != null && cursor.moveToFirst()) {
            data = MappingHelper.mapCursorToMenuIngredient(cursor);
            cursor.close();
        }
        db.close();
        return data;
    }

    public List<MenuIngredient> getAll() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query(MenuIngredientContract.MenuIngredientEntry.TABLE_NAME, null, null, null, null, null, null);
        List<MenuIngredient> list = MappingHelper.mapCursorToMenuIngredientList(cursor);
        db.close();
        return list;
    }

    public int update(MenuIngredient data) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(MenuIngredientContract.MenuIngredientEntry.COLUMN_MENU_ID, data.getMenuId());
        values.put(MenuIngredientContract.MenuIngredientEntry.COLUMN_INGREDIENT_ID, data.getIngredientId());
        values.put(MenuIngredientContract.MenuIngredientEntry.COLUMN_QUANTITY_IN_GRAMS, data.getQuantityInGrams());
        int rows = db.update(
                MenuIngredientContract.MenuIngredientEntry.TABLE_NAME,
                values,
                MenuIngredientContract.MenuIngredientEntry.COLUMN_ID + "=?",
                new String[]{String.valueOf(data.getId())}
        );
        db.close();
        return rows;
    }

    public int delete(int id) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        int rows = db.delete(
                MenuIngredientContract.MenuIngredientEntry.TABLE_NAME,
                MenuIngredientContract.MenuIngredientEntry.COLUMN_ID + "=?",
                new String[]{String.valueOf(id)}
        );
        db.close();
        return rows;
    }
}
