package com.example.fatsecret.data.helper;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.fatsecret.data.DatabaseHelper;
import com.example.fatsecret.data.MappingHelper;
import com.example.fatsecret.data.contract.IngredientContract;
import com.example.fatsecret.data.model.Ingredient;

import java.util.List;

public class IngredientHelper {
    private final DatabaseHelper dbHelper;

    public IngredientHelper(Context context) {
        dbHelper = new DatabaseHelper(context);
    }

    public long insert(Ingredient data) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(IngredientContract.IngredientEntry.COLUMN_NAME, data.getName());
        values.put(IngredientContract.IngredientEntry.COLUMN_CALORIES_PER_100G, data.getCaloriesPer100g());
        values.put(IngredientContract.IngredientEntry.COLUMN_PROTEIN_PER_100G, data.getProteinPer100g());
        values.put(IngredientContract.IngredientEntry.COLUMN_CARBS_PER_100G, data.getCarbsPer100g());
        values.put(IngredientContract.IngredientEntry.COLUMN_FAT_PER_100G, data.getFatPer100g());
        values.put(IngredientContract.IngredientEntry.COLUMN_CREATED_AT, data.getCreatedAt());
        values.put(IngredientContract.IngredientEntry.COLUMN_UPDATED_AT, data.getUpdatedAt());
        long id = db.insert(IngredientContract.IngredientEntry.TABLE_NAME, null, values);
        db.close();
        return id;
    }

    public Ingredient getById(int id) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query(
                IngredientContract.IngredientEntry.TABLE_NAME,
                null,
                IngredientContract.IngredientEntry.COLUMN_ID + "=?",
                new String[]{String.valueOf(id)},
                null, null, null
        );
        Ingredient data = null;
        if (cursor != null && cursor.moveToFirst()) {
            data = MappingHelper.mapCursorToIngredient(cursor);
            cursor.close();
        }
        db.close();
        return data;
    }

    public List<Ingredient> getAll() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query(IngredientContract.IngredientEntry.TABLE_NAME, null, null, null, null, null, null);
        List<Ingredient> list = MappingHelper.mapCursorToIngredientList(cursor);
        db.close();
        return list;
    }

    public int update(Ingredient data) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(IngredientContract.IngredientEntry.COLUMN_NAME, data.getName());
        values.put(IngredientContract.IngredientEntry.COLUMN_CALORIES_PER_100G, data.getCaloriesPer100g());
        values.put(IngredientContract.IngredientEntry.COLUMN_PROTEIN_PER_100G, data.getProteinPer100g());
        values.put(IngredientContract.IngredientEntry.COLUMN_CARBS_PER_100G, data.getCarbsPer100g());
        values.put(IngredientContract.IngredientEntry.COLUMN_FAT_PER_100G, data.getFatPer100g());
        values.put(IngredientContract.IngredientEntry.COLUMN_CREATED_AT, data.getCreatedAt());
        values.put(IngredientContract.IngredientEntry.COLUMN_UPDATED_AT, data.getUpdatedAt());
        int rows = db.update(
                IngredientContract.IngredientEntry.TABLE_NAME,
                values,
                IngredientContract.IngredientEntry.COLUMN_ID + "=?",
                new String[]{String.valueOf(data.getId())}
        );
        db.close();
        return rows;
    }

    public int delete(int id) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        int rows = db.delete(
                IngredientContract.IngredientEntry.TABLE_NAME,
                IngredientContract.IngredientEntry.COLUMN_ID + "=?",
                new String[]{String.valueOf(id)}
        );
        db.close();
        return rows;
    }
}
