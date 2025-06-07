package com.example.fatsecret.data.helper;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.fatsecret.data.DatabaseHelper;
import com.example.fatsecret.data.MappingHelper;
import com.example.fatsecret.data.contract.MenuContract;
import com.example.fatsecret.data.model.Menu;

import java.util.List;

public class MenuHelper {
    private final DatabaseHelper dbHelper;

    public MenuHelper(Context context) {
        dbHelper = new DatabaseHelper(context);
    }

    public long insert(Menu data) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(MenuContract.MenuEntry.COLUMN_NAME, data.getName());
        values.put(MenuContract.MenuEntry.COLUMN_DESCRIPTION, data.getDescription());
        values.put(MenuContract.MenuEntry.COLUMN_TOTAL_CALORIES, data.getTotalCalories());
        values.put(MenuContract.MenuEntry.COLUMN_TOTAL_PROTEIN, data.getTotalProtein());
        values.put(MenuContract.MenuEntry.COLUMN_TOTAL_CARBS, data.getTotalCarbs());
        values.put(MenuContract.MenuEntry.COLUMN_TOTAL_FAT, data.getTotalFat());
        values.put(MenuContract.MenuEntry.COLUMN_CREATED_BY, data.getCreatedBy());
        values.put(MenuContract.MenuEntry.COLUMN_CREATED_AT, data.getUpdatedAt());
        values.put(MenuContract.MenuEntry.COLUMN_UPDATED_AT, data.getUpdatedAt());
        long id = db.insert(MenuContract.MenuEntry.TABLE_NAME, null, values);
        db.close();
        return id;
    }

    public Menu getById(int id) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query(
                MenuContract.MenuEntry.TABLE_NAME,
                null,
                MenuContract.MenuEntry.COLUMN_ID + "=?",
                new String[]{String.valueOf(id)},
                null, null, null
        );
        Menu data = null;
        if (cursor != null && cursor.moveToFirst()) {
            data = MappingHelper.mapCursorToMenu(cursor);
            cursor.close();
        }
        db.close();
        return data;
    }

    public List<Menu> getAll() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query(MenuContract.MenuEntry.TABLE_NAME, null, null, null, null, null, null);
        List<Menu> list = MappingHelper.mapCursorToMenuList(cursor);
        db.close();
        return list;
    }

    public int update(Menu data) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(MenuContract.MenuEntry.COLUMN_NAME, data.getName());
        values.put(MenuContract.MenuEntry.COLUMN_DESCRIPTION, data.getDescription());
        values.put(MenuContract.MenuEntry.COLUMN_TOTAL_CALORIES, data.getTotalCalories());
        values.put(MenuContract.MenuEntry.COLUMN_TOTAL_PROTEIN, data.getTotalProtein());
        values.put(MenuContract.MenuEntry.COLUMN_TOTAL_CARBS, data.getTotalCarbs());
        values.put(MenuContract.MenuEntry.COLUMN_TOTAL_FAT, data.getTotalFat());
        values.put(MenuContract.MenuEntry.COLUMN_CREATED_BY, data.getCreatedBy());
        values.put(MenuContract.MenuEntry.COLUMN_CREATED_AT, data.getUpdatedAt());
        values.put(MenuContract.MenuEntry.COLUMN_UPDATED_AT, data.getUpdatedAt());
        int rows = db.update(
                MenuContract.MenuEntry.TABLE_NAME,
                values,
                MenuContract.MenuEntry.COLUMN_ID + "=?",
                new String[]{String.valueOf(data.getId())}
        );
        db.close();
        return rows;
    }

    public int delete(int id) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        int rows = db.delete(
                MenuContract.MenuEntry.TABLE_NAME,
                MenuContract.MenuEntry.COLUMN_ID + "=?",
                new String[]{String.valueOf(id)}
        );
        db.close();
        return rows;
    }
}
