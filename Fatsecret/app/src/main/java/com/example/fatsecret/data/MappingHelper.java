package com.example.fatsecret.data;

import android.database.Cursor;

import com.example.fatsecret.data.contract.UserContract;
import com.example.fatsecret.data.contract.UserProfileContract;
import com.example.fatsecret.data.contract.FoodLogContract;
import com.example.fatsecret.data.contract.FoodLogItemContract;
import com.example.fatsecret.data.contract.MenuContract;
import com.example.fatsecret.data.contract.IngredientContract;
import com.example.fatsecret.data.contract.MenuIngredientContract;

import com.example.fatsecret.data.model.User;
import com.example.fatsecret.data.model.UserProfile;
import com.example.fatsecret.data.model.FoodLog;
import com.example.fatsecret.data.model.FoodLogItem;
import com.example.fatsecret.data.model.Menu;
import com.example.fatsecret.data.model.Ingredient;
import com.example.fatsecret.data.model.MenuIngredient;

import java.util.ArrayList;
import java.util.List;

public class MappingHelper {

    // USER
    public static User mapCursorToUser(Cursor cursor) {
        if (cursor == null) return null;
        return new User(
                cursor.getInt(cursor.getColumnIndexOrThrow(UserContract.UserEntry.COLUMN_ID)),
                cursor.getString(cursor.getColumnIndexOrThrow(UserContract.UserEntry.COLUMN_NAME)),
                cursor.getString(cursor.getColumnIndexOrThrow(UserContract.UserEntry.COLUMN_EMAIL)),
                cursor.getString(cursor.getColumnIndexOrThrow(UserContract.UserEntry.COLUMN_PASSWORD)),
                cursor.getString(cursor.getColumnIndexOrThrow(UserContract.UserEntry.COLUMN_ROLE)),
                cursor.getString(cursor.getColumnIndexOrThrow(UserContract.UserEntry.COLUMN_PROFILE_PICTURE)),
                cursor.getString(cursor.getColumnIndexOrThrow(UserContract.UserEntry.COLUMN_CREATED_AT)),
                cursor.getString(cursor.getColumnIndexOrThrow(UserContract.UserEntry.COLUMN_UPDATED_AT))
        );
    }

    public static List<User> mapCursorToUserList(Cursor cursor) {
        List<User> users = new ArrayList<>();
        if (cursor != null && cursor.moveToFirst()) {
            do {
                users.add(mapCursorToUser(cursor));
            } while (cursor.moveToNext());
            cursor.close();
        }
        return users;
    }

    // USER PROFILE
    public static UserProfile mapCursorToUserProfile(Cursor cursor) {
        if (cursor == null) return null;
        return new UserProfile(
                cursor.getInt(cursor.getColumnIndexOrThrow(UserProfileContract.UserProfileEntry.COLUMN_ID)),
                cursor.getInt(cursor.getColumnIndexOrThrow(UserProfileContract.UserProfileEntry.COLUMN_USER_ID)),
                cursor.getFloat(cursor.getColumnIndexOrThrow(UserProfileContract.UserProfileEntry.COLUMN_HEIGHT)),
                cursor.getFloat(cursor.getColumnIndexOrThrow(UserProfileContract.UserProfileEntry.COLUMN_WEIGHT)),
                cursor.getFloat(cursor.getColumnIndexOrThrow(UserProfileContract.UserProfileEntry.COLUMN_TARGET_WEIGHT)),
                cursor.getString(cursor.getColumnIndexOrThrow(UserProfileContract.UserProfileEntry.COLUMN_CREATED_AT)),
                cursor.getString(cursor.getColumnIndexOrThrow(UserProfileContract.UserProfileEntry.COLUMN_UPDATED_AT))
        );
    }

    public static List<UserProfile> mapCursorToUserProfileList(Cursor cursor) {
        List<UserProfile> list = new ArrayList<>();
        if (cursor != null && cursor.moveToFirst()) {
            do {
                list.add(mapCursorToUserProfile(cursor));
            } while (cursor.moveToNext());
            cursor.close();
        }
        return list;
    }

    // FOOD LOG
    public static FoodLog mapCursorToFoodLog(Cursor cursor) {
        if (cursor == null) return null;
        return new FoodLog(
                cursor.getInt(cursor.getColumnIndexOrThrow(FoodLogContract.FoodLogEntry.COLUMN_ID)),
                cursor.getInt(cursor.getColumnIndexOrThrow(FoodLogContract.FoodLogEntry.COLUMN_USER_PROFILE_ID)),
                cursor.getString(cursor.getColumnIndexOrThrow(FoodLogContract.FoodLogEntry.COLUMN_MEAL_TIME)),
                cursor.getString(cursor.getColumnIndexOrThrow(FoodLogContract.FoodLogEntry.COLUMN_NOTE)),
                cursor.getString(cursor.getColumnIndexOrThrow(FoodLogContract.FoodLogEntry.COLUMN_CREATED_AT)),
                cursor.getString(cursor.getColumnIndexOrThrow(FoodLogContract.FoodLogEntry.COLUMN_UPDATED_AT))
        );
    }

    public static List<FoodLog> mapCursorToFoodLogList(Cursor cursor) {
        List<FoodLog> list = new ArrayList<>();
        if (cursor != null && cursor.moveToFirst()) {
            do {
                list.add(mapCursorToFoodLog(cursor));
            } while (cursor.moveToNext());
            cursor.close();
        }
        return list;
    }

    // FOOD LOG ITEM
    public static FoodLogItem mapCursorToFoodLogItem(Cursor cursor) {
        if (cursor == null) return null;
        // menu_id dan ingredient_id bisa null
        int menuIdIndex = cursor.getColumnIndexOrThrow(FoodLogItemContract.FoodLogItemEntry.COLUMN_MENU_ID);
        int ingredientIdIndex = cursor.getColumnIndexOrThrow(FoodLogItemContract.FoodLogItemEntry.COLUMN_INGREDIENT_ID);

        Integer menuId = cursor.isNull(menuIdIndex) ? null : cursor.getInt(menuIdIndex);
        Integer ingredientId = cursor.isNull(ingredientIdIndex) ? null : cursor.getInt(ingredientIdIndex);

        return new FoodLogItem(
                cursor.getInt(cursor.getColumnIndexOrThrow(FoodLogItemContract.FoodLogItemEntry.COLUMN_ID)),
                cursor.getInt(cursor.getColumnIndexOrThrow(FoodLogItemContract.FoodLogItemEntry.COLUMN_FOOD_LOG_ID)),
                cursor.getString(cursor.getColumnIndexOrThrow(FoodLogItemContract.FoodLogItemEntry.COLUMN_TYPE)),
                menuId,
                ingredientId,
                cursor.getFloat(cursor.getColumnIndexOrThrow(FoodLogItemContract.FoodLogItemEntry.COLUMN_QUANTITY_IN_GRAMS)),
                cursor.getFloat(cursor.getColumnIndexOrThrow(FoodLogItemContract.FoodLogItemEntry.COLUMN_CALORIES)),
                cursor.getFloat(cursor.getColumnIndexOrThrow(FoodLogItemContract.FoodLogItemEntry.COLUMN_PROTEIN)),
                cursor.getFloat(cursor.getColumnIndexOrThrow(FoodLogItemContract.FoodLogItemEntry.COLUMN_CARBS)),
                cursor.getFloat(cursor.getColumnIndexOrThrow(FoodLogItemContract.FoodLogItemEntry.COLUMN_FAT))
        );
    }

    public static List<FoodLogItem> mapCursorToFoodLogItemList(Cursor cursor) {
        List<FoodLogItem> list = new ArrayList<>();
        if (cursor != null && cursor.moveToFirst()) {
            do {
                list.add(mapCursorToFoodLogItem(cursor));
            } while (cursor.moveToNext());
            cursor.close();
        }
        return list;
    }

    // MENU
    public static Menu mapCursorToMenu(Cursor cursor) {
        if (cursor == null) return null;
        return new Menu(
                cursor.getInt(cursor.getColumnIndexOrThrow(MenuContract.MenuEntry.COLUMN_ID)),
                cursor.getString(cursor.getColumnIndexOrThrow(MenuContract.MenuEntry.COLUMN_NAME)),
                cursor.getString(cursor.getColumnIndexOrThrow(MenuContract.MenuEntry.COLUMN_DESCRIPTION)),
                cursor.getFloat(cursor.getColumnIndexOrThrow(MenuContract.MenuEntry.COLUMN_TOTAL_CALORIES)),
                cursor.getFloat(cursor.getColumnIndexOrThrow(MenuContract.MenuEntry.COLUMN_TOTAL_PROTEIN)),
                cursor.getFloat(cursor.getColumnIndexOrThrow(MenuContract.MenuEntry.COLUMN_TOTAL_CARBS)),
                cursor.getFloat(cursor.getColumnIndexOrThrow(MenuContract.MenuEntry.COLUMN_TOTAL_FAT)),
                cursor.getInt(cursor.getColumnIndexOrThrow(MenuContract.MenuEntry.COLUMN_CREATED_BY)),
                cursor.getString(cursor.getColumnIndexOrThrow(MenuContract.MenuEntry.COLUMN_CREATED_AT)),
                cursor.getString(cursor.getColumnIndexOrThrow(MenuContract.MenuEntry.COLUMN_UPDATED_AT))
        );
    }

    public static List<Menu> mapCursorToMenuList(Cursor cursor) {
        List<Menu> list = new ArrayList<>();
        if (cursor != null && cursor.moveToFirst()) {
            do {
                list.add(mapCursorToMenu(cursor));
            } while (cursor.moveToNext());
            cursor.close();
        }
        return list;
    }

    // INGREDIENT
    public static Ingredient mapCursorToIngredient(Cursor cursor) {
        if (cursor == null) return null;
        return new Ingredient(
                cursor.getInt(cursor.getColumnIndexOrThrow(IngredientContract.IngredientEntry.COLUMN_ID)),
                cursor.getString(cursor.getColumnIndexOrThrow(IngredientContract.IngredientEntry.COLUMN_NAME)),
                cursor.getFloat(cursor.getColumnIndexOrThrow(IngredientContract.IngredientEntry.COLUMN_CALORIES_PER_100G)),
                cursor.getFloat(cursor.getColumnIndexOrThrow(IngredientContract.IngredientEntry.COLUMN_PROTEIN_PER_100G)),
                cursor.getFloat(cursor.getColumnIndexOrThrow(IngredientContract.IngredientEntry.COLUMN_CARBS_PER_100G)),
                cursor.getFloat(cursor.getColumnIndexOrThrow(IngredientContract.IngredientEntry.COLUMN_FAT_PER_100G)),
                cursor.getString(cursor.getColumnIndexOrThrow(IngredientContract.IngredientEntry.COLUMN_CREATED_AT)),
                cursor.getString(cursor.getColumnIndexOrThrow(IngredientContract.IngredientEntry.COLUMN_UPDATED_AT))
        );
    }

    public static List<Ingredient> mapCursorToIngredientList(Cursor cursor) {
        List<Ingredient> list = new ArrayList<>();
        if (cursor != null && cursor.moveToFirst()) {
            do {
                list.add(mapCursorToIngredient(cursor));
            } while (cursor.moveToNext());
            cursor.close();
        }
        return list;
    }

    // MENU INGREDIENT
    public static MenuIngredient mapCursorToMenuIngredient(Cursor cursor) {
        if (cursor == null) return null;
        return new MenuIngredient(
                cursor.getInt(cursor.getColumnIndexOrThrow(MenuIngredientContract.MenuIngredientEntry.COLUMN_ID)),
                cursor.getInt(cursor.getColumnIndexOrThrow(MenuIngredientContract.MenuIngredientEntry.COLUMN_MENU_ID)),
                cursor.getInt(cursor.getColumnIndexOrThrow(MenuIngredientContract.MenuIngredientEntry.COLUMN_INGREDIENT_ID)),
                cursor.getFloat(cursor.getColumnIndexOrThrow(MenuIngredientContract.MenuIngredientEntry.COLUMN_QUANTITY_IN_GRAMS))
        );
    }

    public static List<MenuIngredient> mapCursorToMenuIngredientList(Cursor cursor) {
        List<MenuIngredient> list = new ArrayList<>();
        if (cursor != null && cursor.moveToFirst()) {
            do {
                list.add(mapCursorToMenuIngredient(cursor));
            } while (cursor.moveToNext());
            cursor.close();
        }
        return list;
    }
}