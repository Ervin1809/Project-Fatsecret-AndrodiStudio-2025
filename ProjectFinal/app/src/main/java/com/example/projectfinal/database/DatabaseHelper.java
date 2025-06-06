package com.example.projectfinal.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "nutrition_tracker.db";
    public static final int DATABASE_VERSION = 1;

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Tabel users
        db.execSQL("CREATE TABLE users (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "name TEXT," +
                "email TEXT," +
                "password TEXT," +
                "role TEXT," +
                "profile_picture TEXT," +
                "created_at TEXT," +
                "updated_at TEXT)");

        // Tabel user_profiles
        db.execSQL("CREATE TABLE user_profiles (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "user_id INTEGER UNIQUE," +
                "height REAL," +
                "weight REAL," +
                "target_weight REAL," +
                "created_at TEXT," +
                "updated_at TEXT," +
                "FOREIGN KEY(user_id) REFERENCES users(id))");

        // Tabel ingredients
        db.execSQL("CREATE TABLE ingredients (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "name TEXT," +
                "calories_per_100g REAL," +
                "protein_per_100g REAL," +
                "carbs_per_100g REAL," +
                "fat_per_100g REAL," +
                "created_at TEXT," +
                "updated_at TEXT)");

        // Tabel menus
        db.execSQL("CREATE TABLE menus (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "name TEXT," +
                "description TEXT," +
                "total_calories REAL," +
                "total_protein REAL," +
                "total_carbs REAL," +
                "total_fat REAL," +
                "created_by INTEGER," +
                "created_at TEXT," +
                "updated_at TEXT," +
                "FOREIGN KEY(created_by) REFERENCES users(id))");

        // Tabel menu_ingredients
        db.execSQL("CREATE TABLE menu_ingredients (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "menu_id INTEGER," +
                "ingredient_id INTEGER," +
                "quantity_in_grams REAL," +
                "FOREIGN KEY(menu_id) REFERENCES menus(id)," +
                "FOREIGN KEY(ingredient_id) REFERENCES ingredients(id))");

        // Tabel food_logs
        db.execSQL("CREATE TABLE food_logs (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "user_profile_id INTEGER," +
                "meal_time TEXT," +
                "note TEXT," +
                "created_at TEXT," +
                "updated_at TEXT," +
                "FOREIGN KEY(user_profile_id) REFERENCES user_profiles(id))");

        // Tabel food_log_items
        db.execSQL("CREATE TABLE food_log_items (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "food_log_id INTEGER," +
                "type TEXT," +  // enum: 'menu' or 'ingredient'
                "menu_id INTEGER," +
                "ingredient_id INTEGER," +
                "quantity_in_grams REAL," +
                "calories REAL," +
                "protein REAL," +
                "carbs REAL," +
                "fat REAL," +
                "FOREIGN KEY(food_log_id) REFERENCES food_logs(id)," +
                "FOREIGN KEY(menu_id) REFERENCES menus(id)," +
                "FOREIGN KEY(ingredient_id) REFERENCES ingredients(id))");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Jika perlu upgrade, hapus dan buat ulang
        db.execSQL("DROP TABLE IF EXISTS food_log_items");
        db.execSQL("DROP TABLE IF EXISTS food_logs");
        db.execSQL("DROP TABLE IF EXISTS menu_ingredients");
        db.execSQL("DROP TABLE IF EXISTS menus");
        db.execSQL("DROP TABLE IF EXISTS ingredients");
        db.execSQL("DROP TABLE IF EXISTS user_profiles");
        db.execSQL("DROP TABLE IF EXISTS users");

        onCreate(db);
    }
}
