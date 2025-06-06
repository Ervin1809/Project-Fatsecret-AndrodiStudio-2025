package com.example.projectfinal.DAO;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.projectfinal.database.DatabaseHelper;
import com.example.projectfinal.models.Menu;

import java.util.ArrayList;
import java.util.List;

public class MenuDAO {
    private SQLiteDatabase db;

    public MenuDAO(Context context) {
        DatabaseHelper dbHelper = new DatabaseHelper(context);
        db = dbHelper.getWritableDatabase();
    }

    // Insert
    public long insertMenu(Menu menu) {
        ContentValues values = new ContentValues();
        values.put("name", menu.getName());
        values.put("description", menu.getDescription());
        values.put("total_calories", menu.getTotalCalories());
        values.put("total_protein", menu.getTotalProtein());
        values.put("total_carbs", menu.getTotalCarbs());
        values.put("total_fat", menu.getTotalFat());
        values.put("created_by", menu.getCreatedBy());
        values.put("created_at", menu.getCreatedAt());
        values.put("updated_at", menu.getUpdatedAt());

        return db.insert("menus", null, values);
    }

    // Get all
    public List<Menu> getAllMenus() {
        List<Menu> list = new ArrayList<>();
        Cursor cursor = db.query("menus", null, null, null, null, null, "name ASC");
        if (cursor != null && cursor.moveToFirst()) {
            do {
                list.add(extractFromCursor(cursor));
            } while (cursor.moveToNext());
            cursor.close();
        }
        return list;
    }

    // Get by ID
    public Menu getMenuById(int id) {
        Cursor cursor = db.query("menus", null, "id = ?", new String[]{String.valueOf(id)}, null, null, null);
        if (cursor != null && cursor.moveToFirst()) {
            Menu menu = extractFromCursor(cursor);
            cursor.close();
            return menu;
        }
        return null;
    }

    // Update
    public int updateMenu(Menu menu) {
        ContentValues values = new ContentValues();
        values.put("name", menu.getName());
        values.put("description", menu.getDescription());
        values.put("total_calories", menu.getTotalCalories());
        values.put("total_protein", menu.getTotalProtein());
        values.put("total_carbs", menu.getTotalCarbs());
        values.put("total_fat", menu.getTotalFat());
        values.put("updated_at", menu.getUpdatedAt());

        return db.update("menus", values, "id = ?", new String[]{String.valueOf(menu.getId())});
    }

    // Delete
    public int deleteMenu(int id) {
        return db.delete("menus", "id = ?", new String[]{String.valueOf(id)});
    }

    // Helper
    private Menu extractFromCursor(Cursor cursor) {
        Menu menu = new Menu();
        menu.setId(cursor.getInt(cursor.getColumnIndexOrThrow("id")));
        menu.setName(cursor.getString(cursor.getColumnIndexOrThrow("name")));
        menu.setDescription(cursor.getString(cursor.getColumnIndexOrThrow("description")));
        menu.setTotalCalories(cursor.getFloat(cursor.getColumnIndexOrThrow("total_calories")));
        menu.setTotalProtein(cursor.getFloat(cursor.getColumnIndexOrThrow("total_protein")));
        menu.setTotalCarbs(cursor.getFloat(cursor.getColumnIndexOrThrow("total_carbs")));
        menu.setTotalFat(cursor.getFloat(cursor.getColumnIndexOrThrow("total_fat")));
        menu.setCreatedBy(cursor.getInt(cursor.getColumnIndexOrThrow("created_by")));
        menu.setCreatedAt(cursor.getString(cursor.getColumnIndexOrThrow("created_at")));
        menu.setUpdatedAt(cursor.getString(cursor.getColumnIndexOrThrow("updated_at")));
        return menu;
    }
}
