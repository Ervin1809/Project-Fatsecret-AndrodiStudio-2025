package com.example.fatsecret.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.fatsecret.data.contract.UserContract;
import com.example.fatsecret.data.contract.UserProfileContract;
import com.example.fatsecret.data.contract.FoodLogContract;
import com.example.fatsecret.data.contract.FoodLogItemContract;
import com.example.fatsecret.data.contract.MenuContract;
import com.example.fatsecret.data.contract.IngredientContract;
import com.example.fatsecret.data.contract.MenuIngredientContract;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "nutritiontracker.db";
    private static final int DATABASE_VERSION = 1;

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onConfigure(SQLiteDatabase db) {
        super.onConfigure(db);
        db.setForeignKeyConstraintsEnabled(true);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(UserContract.SQL_CREATE_TABLE);
        db.execSQL(UserProfileContract.SQL_CREATE_TABLE);
        db.execSQL(FoodLogContract.SQL_CREATE_TABLE);
        db.execSQL(FoodLogItemContract.SQL_CREATE_TABLE);
        db.execSQL(MenuContract.SQL_CREATE_TABLE);
        db.execSQL(IngredientContract.SQL_CREATE_TABLE);
        db.execSQL(MenuIngredientContract.SQL_CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(MenuIngredientContract.SQL_DROP_TABLE);
        db.execSQL(FoodLogItemContract.SQL_DROP_TABLE);
        db.execSQL(FoodLogContract.SQL_DROP_TABLE);
        db.execSQL(UserProfileContract.SQL_DROP_TABLE);
        db.execSQL(MenuContract.SQL_DROP_TABLE);
        db.execSQL(IngredientContract.SQL_DROP_TABLE);
        db.execSQL(UserContract.SQL_DROP_TABLE);

        onCreate(db);
    }
}
