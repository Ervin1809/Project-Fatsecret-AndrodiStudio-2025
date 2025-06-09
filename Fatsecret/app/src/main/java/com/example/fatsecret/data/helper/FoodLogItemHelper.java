package com.example.fatsecret.data.helper;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.example.fatsecret.data.DatabaseHelper;
import com.example.fatsecret.data.MappingHelper;
import com.example.fatsecret.data.contract.FoodLogItemContract;
import com.example.fatsecret.data.model.FoodLogItem;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class FoodLogItemHelper {
    private static final String TAG = "FoodLogItemHelper";
    private final DatabaseHelper dbHelper;

    public FoodLogItemHelper(Context context) {
        dbHelper = new DatabaseHelper(context);
    }

    // ✅ INSERT method (already exists - good)
    public long insert(FoodLogItem data) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(FoodLogItemContract.FoodLogItemEntry.COLUMN_FOOD_LOG_ID, data.getFoodLogId());
        values.put(FoodLogItemContract.FoodLogItemEntry.COLUMN_INGREDIENT_ID, data.getIngredientId());
        values.put(FoodLogItemContract.FoodLogItemEntry.COLUMN_WEIGHT_GRAMS, data.getWeightGrams());
        values.put(FoodLogItemContract.FoodLogItemEntry.COLUMN_CALCULATED_CALORIES, data.getCalculatedCalories());
        values.put(FoodLogItemContract.FoodLogItemEntry.COLUMN_CALCULATED_PROTEIN, data.getCalculatedProtein());
        values.put(FoodLogItemContract.FoodLogItemEntry.COLUMN_CALCULATED_CARBS, data.getCalculatedCarbs());
        values.put(FoodLogItemContract.FoodLogItemEntry.COLUMN_CALCULATED_FAT, data.getCalculatedFat());

        String currentTime = getCurrentTimestamp();
        values.put(FoodLogItemContract.FoodLogItemEntry.COLUMN_CREATED_AT, currentTime);
        values.put(FoodLogItemContract.FoodLogItemEntry.COLUMN_UPDATED_AT, currentTime);

        long id = db.insert(FoodLogItemContract.FoodLogItemEntry.TABLE_NAME, null, values);
        db.close();
        return id;
    }

    // ✅ ADD: getById method
    public FoodLogItem getById(int id) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        FoodLogItem item = null;

        try {
            String[] projection = {
                    FoodLogItemContract.FoodLogItemEntry.COLUMN_ID,
                    FoodLogItemContract.FoodLogItemEntry.COLUMN_FOOD_LOG_ID,
                    FoodLogItemContract.FoodLogItemEntry.COLUMN_INGREDIENT_ID,
                    FoodLogItemContract.FoodLogItemEntry.COLUMN_WEIGHT_GRAMS,
                    FoodLogItemContract.FoodLogItemEntry.COLUMN_CALCULATED_CALORIES,
                    FoodLogItemContract.FoodLogItemEntry.COLUMN_CALCULATED_PROTEIN,
                    FoodLogItemContract.FoodLogItemEntry.COLUMN_CALCULATED_CARBS,
                    FoodLogItemContract.FoodLogItemEntry.COLUMN_CALCULATED_FAT,
                    FoodLogItemContract.FoodLogItemEntry.COLUMN_CREATED_AT,
                    FoodLogItemContract.FoodLogItemEntry.COLUMN_UPDATED_AT
            };

            String selection = FoodLogItemContract.FoodLogItemEntry.COLUMN_ID + " = ?";
            String[] selectionArgs = {String.valueOf(id)};

            Cursor cursor = db.query(
                    FoodLogItemContract.FoodLogItemEntry.TABLE_NAME,
                    projection,
                    selection,
                    selectionArgs,
                    null,
                    null,
                    null
            );

            if (cursor != null && cursor.moveToFirst()) {
                item = MappingHelper.mapCursorToFoodLogItem(cursor);
                cursor.close();
            }

        } catch (Exception e) {
            Log.e(TAG, "Error getting FoodLogItem by ID: " + e.getMessage(), e);
        } finally {
            db.close();
        }

        return item;
    }

    // ✅ ADD: getAll method
    public List<FoodLogItem> getAll() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        List<FoodLogItem> items;

        try {
            String[] projection = {
                    FoodLogItemContract.FoodLogItemEntry.COLUMN_ID,
                    FoodLogItemContract.FoodLogItemEntry.COLUMN_FOOD_LOG_ID,
                    FoodLogItemContract.FoodLogItemEntry.COLUMN_INGREDIENT_ID,
                    FoodLogItemContract.FoodLogItemEntry.COLUMN_WEIGHT_GRAMS,
                    FoodLogItemContract.FoodLogItemEntry.COLUMN_CALCULATED_CALORIES,
                    FoodLogItemContract.FoodLogItemEntry.COLUMN_CALCULATED_PROTEIN,
                    FoodLogItemContract.FoodLogItemEntry.COLUMN_CALCULATED_CARBS,
                    FoodLogItemContract.FoodLogItemEntry.COLUMN_CALCULATED_FAT,
                    FoodLogItemContract.FoodLogItemEntry.COLUMN_CREATED_AT,
                    FoodLogItemContract.FoodLogItemEntry.COLUMN_UPDATED_AT
            };

            Cursor cursor = db.query(
                    FoodLogItemContract.FoodLogItemEntry.TABLE_NAME,
                    projection,
                    null,
                    null,
                    null,
                    null,
                    FoodLogItemContract.FoodLogItemEntry.COLUMN_CREATED_AT + " DESC"
            );

            items = MappingHelper.mapCursorToFoodLogItemList(cursor);

        } catch (Exception e) {
            Log.e(TAG, "Error getting all FoodLogItems: " + e.getMessage(), e);
            items = new ArrayList<>();
        } finally {
            db.close();
        }

        return items;
    }

    // ✅ ADD: update method (return int instead of boolean)
    public int update(FoodLogItem data) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        int rowsAffected = 0;

        try {
            ContentValues values = new ContentValues();

            values.put(FoodLogItemContract.FoodLogItemEntry.COLUMN_FOOD_LOG_ID, data.getFoodLogId());
            values.put(FoodLogItemContract.FoodLogItemEntry.COLUMN_INGREDIENT_ID, data.getIngredientId());
            values.put(FoodLogItemContract.FoodLogItemEntry.COLUMN_WEIGHT_GRAMS, data.getWeightGrams());
            values.put(FoodLogItemContract.FoodLogItemEntry.COLUMN_CALCULATED_CALORIES, data.getCalculatedCalories());
            values.put(FoodLogItemContract.FoodLogItemEntry.COLUMN_CALCULATED_PROTEIN, data.getCalculatedProtein());
            values.put(FoodLogItemContract.FoodLogItemEntry.COLUMN_CALCULATED_CARBS, data.getCalculatedCarbs());
            values.put(FoodLogItemContract.FoodLogItemEntry.COLUMN_CALCULATED_FAT, data.getCalculatedFat());
            values.put(FoodLogItemContract.FoodLogItemEntry.COLUMN_UPDATED_AT, getCurrentTimestamp());

            String whereClause = FoodLogItemContract.FoodLogItemEntry.COLUMN_ID + " = ?";
            String[] whereArgs = {String.valueOf(data.getId())};

            rowsAffected = db.update(FoodLogItemContract.FoodLogItemEntry.TABLE_NAME, values, whereClause, whereArgs);

        } catch (Exception e) {
            Log.e(TAG, "Error updating FoodLogItem: " + e.getMessage(), e);
        } finally {
            db.close();
        }

        return rowsAffected;
    }

    // ✅ ADD: delete method
    public int delete(int id) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        int rowsAffected = 0;

        try {
            String whereClause = FoodLogItemContract.FoodLogItemEntry.COLUMN_ID + " = ?";
            String[] whereArgs = {String.valueOf(id)};

            rowsAffected = db.delete(FoodLogItemContract.FoodLogItemEntry.TABLE_NAME, whereClause, whereArgs);

            Log.d(TAG, "Deleted FoodLogItem with ID: " + id + ", rows affected: " + rowsAffected);

        } catch (Exception e) {
            Log.e(TAG, "Error deleting FoodLogItem: " + e.getMessage(), e);
        } finally {
            db.close();
        }

        return rowsAffected;
    }

    // ✅ EXISTING: getFoodLogItemsByFoodLogId method (keep this)
    public List<FoodLogItem> getFoodLogItemsByFoodLogId(int foodLogId) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        String[] projection = {
                FoodLogItemContract.FoodLogItemEntry.COLUMN_ID,
                FoodLogItemContract.FoodLogItemEntry.COLUMN_FOOD_LOG_ID,
                FoodLogItemContract.FoodLogItemEntry.COLUMN_INGREDIENT_ID,
                FoodLogItemContract.FoodLogItemEntry.COLUMN_WEIGHT_GRAMS,
                FoodLogItemContract.FoodLogItemEntry.COLUMN_CALCULATED_CALORIES,
                FoodLogItemContract.FoodLogItemEntry.COLUMN_CALCULATED_PROTEIN,
                FoodLogItemContract.FoodLogItemEntry.COLUMN_CALCULATED_CARBS,
                FoodLogItemContract.FoodLogItemEntry.COLUMN_CALCULATED_FAT,
                FoodLogItemContract.FoodLogItemEntry.COLUMN_CREATED_AT,
                FoodLogItemContract.FoodLogItemEntry.COLUMN_UPDATED_AT
        };

        String selection = FoodLogItemContract.FoodLogItemEntry.COLUMN_FOOD_LOG_ID + " = ?";
        String[] selectionArgs = {String.valueOf(foodLogId)};

        Cursor cursor = db.query(
                FoodLogItemContract.FoodLogItemEntry.TABLE_NAME,
                projection,
                selection,
                selectionArgs,
                null,
                null,
                FoodLogItemContract.FoodLogItemEntry.COLUMN_CREATED_AT + " ASC"
        );

        List<FoodLogItem> items = MappingHelper.mapCursorToFoodLogItemList(cursor);
        db.close();
        return items;
    }

    // ✅ Helper method untuk timestamps
    private String getCurrentTimestamp() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        return sdf.format(new Date());
    }
}