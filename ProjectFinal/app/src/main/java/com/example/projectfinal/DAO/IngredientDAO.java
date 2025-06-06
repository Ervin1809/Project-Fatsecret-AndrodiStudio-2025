package com.example.projectfinal.DAO;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.projectfinal.database.DatabaseHelper;
import com.example.projectfinal.models.Ingredient;

import java.util.ArrayList;
import java.util.List;

public class IngredientDAO {
    private SQLiteDatabase db;

    public IngredientDAO(Context context) {
        DatabaseHelper dbHelper = new DatabaseHelper(context);
        db = dbHelper.getWritableDatabase();
    }

    // Insert
    public long insertIngredient(Ingredient ingredient) {
        ContentValues values = new ContentValues();
        values.put("name", ingredient.getName());
        values.put("calories_per_100g", ingredient.getCaloriesPer100g());
        values.put("protein_per_100g", ingredient.getProteinPer100g());
        values.put("carbs_per_100g", ingredient.getCarbsPer100g());
        values.put("fat_per_100g", ingredient.getFatPer100g());
        values.put("created_at", ingredient.getCreatedAt());
        values.put("updated_at", ingredient.getUpdatedAt());

        return db.insert("ingredients", null, values);
    }

    // Get all
    public List<Ingredient> getAllIngredients() {
        List<Ingredient> list = new ArrayList<>();
        Cursor cursor = db.query("ingredients", null, null, null, null, null, "name ASC");
        if (cursor != null && cursor.moveToFirst()) {
            do {
                list.add(extractFromCursor(cursor));
            } while (cursor.moveToNext());
            cursor.close();
        }
        return list;
    }

    // Get by ID
    public Ingredient getIngredientById(int id) {
        Cursor cursor = db.query("ingredients", null, "id = ?", new String[]{String.valueOf(id)}, null, null, null);
        if (cursor != null && cursor.moveToFirst()) {
            Ingredient ingredient = extractFromCursor(cursor);
            cursor.close();
            return ingredient;
        }
        return null;
    }

    // Update
    public int updateIngredient(Ingredient ingredient) {
        ContentValues values = new ContentValues();
        values.put("name", ingredient.getName());
        values.put("calories_per_100g", ingredient.getCaloriesPer100g());
        values.put("protein_per_100g", ingredient.getProteinPer100g());
        values.put("carbs_per_100g", ingredient.getCarbsPer100g());
        values.put("fat_per_100g", ingredient.getFatPer100g());
        values.put("updated_at", ingredient.getUpdatedAt());

        return db.update("ingredients", values, "id = ?", new String[]{String.valueOf(ingredient.getId())});
    }

    // Delete
    public int deleteIngredient(int id) {
        return db.delete("ingredients", "id = ?", new String[]{String.valueOf(id)});
    }

    // Helper
    private Ingredient extractFromCursor(Cursor cursor) {
        Ingredient ingredient = new Ingredient();
        ingredient.setId(cursor.getInt(cursor.getColumnIndexOrThrow("id")));
        ingredient.setName(cursor.getString(cursor.getColumnIndexOrThrow("name")));
        ingredient.setCaloriesPer100g(cursor.getFloat(cursor.getColumnIndexOrThrow("calories_per_100g")));
        ingredient.setProteinPer100g(cursor.getFloat(cursor.getColumnIndexOrThrow("protein_per_100g")));
        ingredient.setCarbsPer100g(cursor.getFloat(cursor.getColumnIndexOrThrow("carbs_per_100g")));
        ingredient.setFatPer100g(cursor.getFloat(cursor.getColumnIndexOrThrow("fat_per_100g")));
        ingredient.setCreatedAt(cursor.getString(cursor.getColumnIndexOrThrow("created_at")));
        ingredient.setUpdatedAt(cursor.getString(cursor.getColumnIndexOrThrow("updated_at")));
        return ingredient;
    }
}
