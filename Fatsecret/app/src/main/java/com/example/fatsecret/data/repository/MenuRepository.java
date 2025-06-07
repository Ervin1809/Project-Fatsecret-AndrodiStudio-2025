package com.example.fatsecret.data.repository;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.example.fatsecret.data.DatabaseHelper;
import com.example.fatsecret.data.MappingHelper;
import com.example.fatsecret.data.contract.MenuContract;
import com.example.fatsecret.data.contract.MenuIngredientContract;
import com.example.fatsecret.data.contract.IngredientContract;
import com.example.fatsecret.data.model.Menu;
import com.example.fatsecret.data.model.MenuIngredient;
import com.example.fatsecret.data.model.Ingredient;

import java.util.ArrayList;
import java.util.List;

public class MenuRepository {
    private static final String TAG = "MenuRepository";
    private final DatabaseHelper dbHelper;
    private final IngredientRepository ingredientRepository;

    public MenuRepository(Context context) {
        this.dbHelper = new DatabaseHelper(context);
        this.ingredientRepository = new IngredientRepository(context);
    }

    /**
     * Search menus by name
     */
    public List<Menu> searchMenus(String query) {
        List<Menu> menus = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        String selection = MenuContract.MenuEntry.COLUMN_NAME + " LIKE ?";
        String[] selectionArgs = {"%" + query + "%"};
        String orderBy = MenuContract.MenuEntry.COLUMN_NAME + " ASC";

        Cursor cursor = db.query(
                MenuContract.MenuEntry.TABLE_NAME,
                null,
                selection,
                selectionArgs,
                null,
                null,
                orderBy,
                "20" // Limit to 20 results
        );

        menus = MappingHelper.mapCursorToMenuList(cursor);

        // Load ingredients for each menu
        for (Menu menu : menus) {
            menu.setIngredients(getMenuIngredients(menu.getId()));
        }

        db.close();
        return menus;
    }

    /**
     * Get all menus
     */
    public List<Menu> getAllMenus() {
        List<Menu> menus = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        String orderBy = MenuContract.MenuEntry.COLUMN_NAME + " ASC";

        Cursor cursor = db.query(
                MenuContract.MenuEntry.TABLE_NAME,
                null,
                null,
                null,
                null,
                null,
                orderBy
        );

        menus = MappingHelper.mapCursorToMenuList(cursor);

        // Load ingredients for each menu
        for (Menu menu : menus) {
            menu.setIngredients(getMenuIngredients(menu.getId()));
        }

        db.close();
        return menus;
    }

    /**
     * Get menu by ID with ingredients
     */
    public Menu getMenuById(int id) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        String selection = MenuContract.MenuEntry.COLUMN_ID + " = ?";
        String[] selectionArgs = {String.valueOf(id)};

        Cursor cursor = db.query(
                MenuContract.MenuEntry.TABLE_NAME,
                null,
                selection,
                selectionArgs,
                null,
                null,
                null
        );

        Menu menu = null;
        if (cursor != null && cursor.moveToFirst()) {
            menu = MappingHelper.mapCursorToMenu(cursor);
            cursor.close();

            // Load ingredients
            menu.setIngredients(getMenuIngredients(menu.getId()));
        }

        db.close();
        return menu;
    }

    /**
     * Create new menu (Admin only)
     */
    public long createMenu(Menu menu) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(MenuContract.MenuEntry.COLUMN_USER_ID, menu.getCreatedBy());
        values.put(MenuContract.MenuEntry.COLUMN_NAME, menu.getName());
        values.put(MenuContract.MenuEntry.COLUMN_DESCRIPTION, menu.getDescription());
        values.put(MenuContract.MenuEntry.COLUMN_TOTAL_CALORIES, menu.getTotalCalories());
        values.put(MenuContract.MenuEntry.COLUMN_TOTAL_PROTEIN, menu.getTotalProtein());
        values.put(MenuContract.MenuEntry.COLUMN_TOTAL_CARBS, menu.getTotalCarbs());
        values.put(MenuContract.MenuEntry.COLUMN_TOTAL_FAT, menu.getTotalFat());
        values.put(MenuContract.MenuEntry.COLUMN_CREATED_BY, menu.getCreatedBy());
        values.put(MenuContract.MenuEntry.COLUMN_CREATED_AT, getCurrentTimestamp());
        values.put(MenuContract.MenuEntry.COLUMN_UPDATED_AT, getCurrentTimestamp());

        long menuId = db.insert(MenuContract.MenuEntry.TABLE_NAME, null, values);

        // Save menu ingredients
        if (menuId > 0 && menu.getIngredients() != null) {
            for (MenuIngredient menuIngredient : menu.getIngredients()) {
                menuIngredient.setMenuId((int) menuId);
                saveMenuIngredient(menuIngredient);
            }
        }

        db.close();

        Log.d(TAG, "Created menu: " + menu.getName() + " with ID: " + menuId);
        return menuId;
    }

    /**
     * Update menu (Admin only)
     */
    public boolean updateMenu(Menu menu) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(MenuContract.MenuEntry.COLUMN_NAME, menu.getName());
        values.put(MenuContract.MenuEntry.COLUMN_DESCRIPTION, menu.getDescription());
        values.put(MenuContract.MenuEntry.COLUMN_TOTAL_CALORIES, menu.getTotalCalories());
        values.put(MenuContract.MenuEntry.COLUMN_TOTAL_PROTEIN, menu.getTotalProtein());
        values.put(MenuContract.MenuEntry.COLUMN_TOTAL_CARBS, menu.getTotalCarbs());
        values.put(MenuContract.MenuEntry.COLUMN_TOTAL_FAT, menu.getTotalFat());
        values.put(MenuContract.MenuEntry.COLUMN_UPDATED_AT, getCurrentTimestamp());

        String whereClause = MenuContract.MenuEntry.COLUMN_ID + " = ?";
        String[] whereArgs = {String.valueOf(menu.getId())};

        int rowsAffected = db.update(MenuContract.MenuEntry.TABLE_NAME, values, whereClause, whereArgs);

        // Update menu ingredients
        if (rowsAffected > 0 && menu.getIngredients() != null) {
            // Delete existing ingredients
            deleteMenuIngredients(menu.getId());

            // Add new ingredients
            for (MenuIngredient menuIngredient : menu.getIngredients()) {
                menuIngredient.setMenuId(menu.getId());
                saveMenuIngredient(menuIngredient);
            }
        }

        db.close();

        Log.d(TAG, "Updated menu: " + menu.getName());
        return rowsAffected > 0;
    }

    /**
     * Delete menu (Admin only)
     */
    public boolean deleteMenu(int menuId) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        // Delete menu ingredients first (due to foreign key)
        deleteMenuIngredients(menuId);

        // Delete menu
        String whereClause = MenuContract.MenuEntry.COLUMN_ID + " = ?";
        String[] whereArgs = {String.valueOf(menuId)};

        int rowsAffected = db.delete(MenuContract.MenuEntry.TABLE_NAME, whereClause, whereArgs);
        db.close();

        Log.d(TAG, "Deleted menu with ID: " + menuId);
        return rowsAffected > 0;
    }

    /**
     * Get menu ingredients with ingredient details
     */
    private List<MenuIngredient> getMenuIngredients(int menuId) {
        List<MenuIngredient> menuIngredients = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        // Join with ingredients table to get ingredient details
        String query = "SELECT mi.*, i.* FROM " +
                MenuIngredientContract.MenuIngredientEntry.TABLE_NAME + " mi " +
                "INNER JOIN " + IngredientContract.IngredientEntry.TABLE_NAME + " i " +
                "ON mi." + MenuIngredientContract.MenuIngredientEntry.COLUMN_INGREDIENT_ID +
                " = i." + IngredientContract.IngredientEntry.COLUMN_ID +
                " WHERE mi." + MenuIngredientContract.MenuIngredientEntry.COLUMN_MENU_ID + " = ?";

        String[] selectionArgs = {String.valueOf(menuId)};

        Cursor cursor = db.rawQuery(query, selectionArgs);

        if (cursor != null && cursor.moveToFirst()) {
            do {
                MenuIngredient menuIngredient = MappingHelper.mapCursorToMenuIngredient(cursor);
                Ingredient ingredient = MappingHelper.mapCursorToIngredient(cursor);
                menuIngredient.setIngredient(ingredient);
                menuIngredients.add(menuIngredient);
            } while (cursor.moveToNext());
            cursor.close();
        }

        db.close();
        return menuIngredients;
    }

    /**
     * Save menu ingredient
     */
    private long saveMenuIngredient(MenuIngredient menuIngredient) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(MenuIngredientContract.MenuIngredientEntry.COLUMN_MENU_ID, menuIngredient.getMenuId());
        values.put(MenuIngredientContract.MenuIngredientEntry.COLUMN_INGREDIENT_ID, menuIngredient.getIngredientId());
        values.put(MenuIngredientContract.MenuIngredientEntry.COLUMN_QUANTITY_IN_GRAMS, menuIngredient.getQuantityInGrams());
        values.put(MenuIngredientContract.MenuIngredientEntry.COLUMN_CREATED_AT, getCurrentTimestamp());

        long result = db.insert(MenuIngredientContract.MenuIngredientEntry.TABLE_NAME, null, values);
        db.close();

        return result;
    }

    /**
     * Delete all ingredients for a menu
     */
    private void deleteMenuIngredients(int menuId) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        String whereClause = MenuIngredientContract.MenuIngredientEntry.COLUMN_MENU_ID + " = ?";
        String[] whereArgs = {String.valueOf(menuId)};

        db.delete(MenuIngredientContract.MenuIngredientEntry.TABLE_NAME, whereClause, whereArgs);
        db.close();
    }

    /**
     * Get current timestamp
     */
    private String getCurrentTimestamp() {
        return new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss", java.util.Locale.getDefault())
                .format(new java.util.Date());
    }
}