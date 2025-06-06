package com.example.projectfinal.DAO;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.projectfinal.database.DatabaseHelper;
import com.example.projectfinal.models.MenuIngredient;

import java.util.ArrayList;
import java.util.List;

public class MenuIngredientDAO {
    private SQLiteDatabase db;

    public MenuIngredientDAO(Context context) {
        DatabaseHelper dbHelper = new DatabaseHelper(context);
        db = dbHelper.getWritableDatabase();
    }

    // Insert
    public long insert(MenuIngredient mi) {
        ContentValues values = new ContentValues();
        values.put("menu_id", mi.getMenuId());
        values.put("ingredient_id", mi.getIngredientId());
        values.put("quantity_in_grams", mi.getQuantityInGrams());

        return db.insert("menu_ingredients", null, values);
    }

    // Get all by menu_id
    public List<MenuIngredient> getIngredientsByMenuId(int menuId) {
        List<MenuIngredient> list = new ArrayList<>();
        Cursor cursor = db.query("menu_ingredients", null, "menu_id = ?", new String[]{String.valueOf(menuId)}, null, null, null);
        if (cursor != null && cursor.moveToFirst()) {
            do {
                list.add(extractFromCursor(cursor));
            } while (cursor.moveToNext());
            cursor.close();
        }
        return list;
    }

    // Delete by id
    public int delete(int id) {
        return db.delete("menu_ingredients", "id = ?", new String[]{String.valueOf(id)});
    }

    // Delete by menu_id
    public int deleteByMenuId(int menuId) {
        return db.delete("menu_ingredients", "menu_id = ?", new String[]{String.valueOf(menuId)});
    }

    // Helper
    private MenuIngredient extractFromCursor(Cursor cursor) {
        MenuIngredient mi = new MenuIngredient();
        mi.setId(cursor.getInt(cursor.getColumnIndexOrThrow("id")));
        mi.setMenuId(cursor.getInt(cursor.getColumnIndexOrThrow("menu_id")));
        mi.setIngredientId(cursor.getInt(cursor.getColumnIndexOrThrow("ingredient_id")));
        mi.setQuantityInGrams(cursor.getFloat(cursor.getColumnIndexOrThrow("quantity_in_grams")));
        return mi;
    }
}

