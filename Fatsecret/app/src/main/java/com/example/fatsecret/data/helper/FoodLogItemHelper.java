package com.example.fatsecret.data.helper;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.fatsecret.data.DatabaseHelper;
import com.example.fatsecret.data.MappingHelper;
import com.example.fatsecret.data.contract.FoodLogItemContract;
import com.example.fatsecret.data.model.FoodLogItem;

import java.util.List;

public class FoodLogItemHelper {
    private final DatabaseHelper dbHelper;

    public FoodLogItemHelper(Context context) {
        dbHelper = new DatabaseHelper(context);
    }

    public long insert(FoodLogItem data) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(FoodLogItemContract.FoodLogItemEntry.COLUMN_FOOD_LOG_ID, data.getFoodLogId());
        values.put(FoodLogItemContract.FoodLogItemEntry.COLUMN_TYPE, data.getType());
        values.put(FoodLogItemContract.FoodLogItemEntry.COLUMN_MENU_ID, data.getMenuId());
        values.put(FoodLogItemContract.FoodLogItemEntry.COLUMN_INGREDIENT_ID, data.getIngredientId());
        values.put(FoodLogItemContract.FoodLogItemEntry.COLUMN_QUANTITY_IN_GRAMS, data.getQuantityInGrams());
        values.put(FoodLogItemContract.FoodLogItemEntry.COLUMN_CALORIES, data.getCalories());
        values.put(FoodLogItemContract.FoodLogItemEntry.COLUMN_PROTEIN, data.getProtein());
        values.put(FoodLogItemContract.FoodLogItemEntry.COLUMN_CARBS, data.getCarbs());
        values.put(FoodLogItemContract.FoodLogItemEntry.COLUMN_FAT, data.getFat());
        long id = db.insert(FoodLogItemContract.FoodLogItemEntry.TABLE_NAME, null, values);
        db.close();
        return id;
    }

    public FoodLogItem getById(int id) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query(
                FoodLogItemContract.FoodLogItemEntry.TABLE_NAME,
                null,
                FoodLogItemContract.FoodLogItemEntry.COLUMN_ID + "=?",
                new String[]{String.valueOf(id)},
                null, null, null
        );
        FoodLogItem data = null;
        if (cursor != null && cursor.moveToFirst()) {
            data = MappingHelper.mapCursorToFoodLogItem(cursor);
            cursor.close();
        }
        db.close();
        return data;
    }

    public List<FoodLogItem> getAll() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query(FoodLogItemContract.FoodLogItemEntry.TABLE_NAME, null, null, null, null, null, null);
        List<FoodLogItem> list = MappingHelper.mapCursorToFoodLogItemList(cursor);
        db.close();
        return list;
    }

    public int update(FoodLogItem data) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(FoodLogItemContract.FoodLogItemEntry.COLUMN_FOOD_LOG_ID, data.getFoodLogId());
        values.put(FoodLogItemContract.FoodLogItemEntry.COLUMN_TYPE, data.getType());
        values.put(FoodLogItemContract.FoodLogItemEntry.COLUMN_MENU_ID, data.getMenuId());
        values.put(FoodLogItemContract.FoodLogItemEntry.COLUMN_INGREDIENT_ID, data.getIngredientId());
        values.put(FoodLogItemContract.FoodLogItemEntry.COLUMN_QUANTITY_IN_GRAMS, data.getQuantityInGrams());
        values.put(FoodLogItemContract.FoodLogItemEntry.COLUMN_CALORIES, data.getCalories());
        values.put(FoodLogItemContract.FoodLogItemEntry.COLUMN_PROTEIN, data.getProtein());
        values.put(FoodLogItemContract.FoodLogItemEntry.COLUMN_CARBS, data.getCarbs());
        values.put(FoodLogItemContract.FoodLogItemEntry.COLUMN_FAT, data.getFat());
        int rows = db.update(
                FoodLogItemContract.FoodLogItemEntry.TABLE_NAME,
                values,
                FoodLogItemContract.FoodLogItemEntry.COLUMN_ID + "=?",
                new String[]{String.valueOf(data.getId())}
        );
        db.close();
        return rows;
    }

    public int delete(int id) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        int rows = db.delete(
                FoodLogItemContract.FoodLogItemEntry.TABLE_NAME,
                FoodLogItemContract.FoodLogItemEntry.COLUMN_ID + "=?",
                new String[]{String.valueOf(id)}
        );
        db.close();
        return rows;
    }
}
