package com.example.fatsecret.data;

import static android.content.ContentValues.TAG;

import android.database.Cursor;
import android.util.Log;

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

        try {
            UserProfile profile = new UserProfile();

            // ✅ Basic fields (yang sudah ada)
            profile.setId(cursor.getInt(cursor.getColumnIndexOrThrow(UserProfileContract.UserProfileEntry.COLUMN_ID)));
            profile.setUserId(cursor.getInt(cursor.getColumnIndexOrThrow(UserProfileContract.UserProfileEntry.COLUMN_USER_ID)));
            profile.setHeight(cursor.getFloat(cursor.getColumnIndexOrThrow(UserProfileContract.UserProfileEntry.COLUMN_HEIGHT)));
            profile.setWeight(cursor.getFloat(cursor.getColumnIndexOrThrow(UserProfileContract.UserProfileEntry.COLUMN_WEIGHT)));
            profile.setTargetWeight(cursor.getFloat(cursor.getColumnIndexOrThrow(UserProfileContract.UserProfileEntry.COLUMN_TARGET_WEIGHT)));
            profile.setCreatedAt(cursor.getString(cursor.getColumnIndexOrThrow(UserProfileContract.UserProfileEntry.COLUMN_CREATED_AT)));
            profile.setUpdatedAt(cursor.getString(cursor.getColumnIndexOrThrow(UserProfileContract.UserProfileEntry.COLUMN_UPDATED_AT)));

            // ✅ ADD: Nutrition fields yang missing
            profile.setGender(getStringSafely(cursor, UserProfileContract.UserProfileEntry.COLUMN_GENDER));
            profile.setAge(getIntSafely(cursor, UserProfileContract.UserProfileEntry.COLUMN_AGE));
            profile.setActivityLevel(getStringSafely(cursor, UserProfileContract.UserProfileEntry.COLUMN_ACTIVITY_LEVEL));
            profile.setDailyCaloriesTarget(getFloatSafely(cursor, UserProfileContract.UserProfileEntry.COLUMN_DAILY_CALORIES_TARGET));
            profile.setDailyProteinTarget(getFloatSafely(cursor, UserProfileContract.UserProfileEntry.COLUMN_DAILY_PROTEIN_TARGET));
            profile.setDailyCarbsTarget(getFloatSafely(cursor, UserProfileContract.UserProfileEntry.COLUMN_DAILY_CARBS_TARGET));
            profile.setDailyFatTarget(getFloatSafely(cursor, UserProfileContract.UserProfileEntry.COLUMN_DAILY_FAT_TARGET));

            // ✅ Debug log untuk verify
            Log.d(TAG, "🔧 DEBUG: Mapped UserProfile:");
            Log.d(TAG, "Gender: " + profile.getGender() + ", Age: " + profile.getAge());
            Log.d(TAG, "Activity: " + profile.getActivityLevel());
            Log.d(TAG, "Calories: " + profile.getDailyCaloriesTarget());

            return profile;

        } catch (Exception e) {
            Log.e(TAG, "Error mapping cursor to UserProfile: " + e.getMessage(), e);
            return null;
        }
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
                cursor.getString(cursor.getColumnIndexOrThrow(IngredientContract.IngredientEntry.COLUMN_CREATED_AT)),
                cursor.getString(cursor.getColumnIndexOrThrow(IngredientContract.IngredientEntry.COLUMN_UPDATED_AT)),
                cursor.getFloat(cursor.getColumnIndexOrThrow(IngredientContract.IngredientEntry.COLUMN_CALORIES_PER_100G)),
                cursor.getFloat(cursor.getColumnIndexOrThrow(IngredientContract.IngredientEntry.COLUMN_PROTEIN_PER_100G)),
                cursor.getFloat(cursor.getColumnIndexOrThrow(IngredientContract.IngredientEntry.COLUMN_CARBS_PER_100G)),
                cursor.getFloat(cursor.getColumnIndexOrThrow(IngredientContract.IngredientEntry.COLUMN_FAT_PER_100G)),
                cursor.getInt(cursor.getColumnIndexOrThrow(IngredientContract.IngredientEntry.COLUMN_FDC_ID)),
                cursor.getString(cursor.getColumnIndexOrThrow(IngredientContract.IngredientEntry.COLUMN_API_SOURCE)),
                cursor.getString(cursor.getColumnIndexOrThrow(IngredientContract.IngredientEntry.COLUMN_LAST_UPDATED))
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

    // ✅ Helper methods untuk akses kolom yang aman
    private static float getFloatSafely(Cursor cursor, String columnName) {
        try {
            int columnIndex = cursor.getColumnIndex(columnName);
            if (columnIndex != -1 && !cursor.isNull(columnIndex)) {
                return cursor.getFloat(columnIndex);
            }
            return 0.0f; // Default kalau kolom belum ada
        } catch (Exception e) {
            return 0.0f;
        }
    }

    private static String getStringSafely(Cursor cursor, String columnName) {
        try {
            int columnIndex = cursor.getColumnIndex(columnName);
            if (columnIndex != -1 && !cursor.isNull(columnIndex)) {
                return cursor.getString(columnIndex);
            }
            return null; // Default kalau kolom belum ada
        } catch (Exception e) {
            return null;
        }
    }

    private static int getIntSafely(Cursor cursor, String columnName) {
        try {
            int columnIndex = cursor.getColumnIndex(columnName);
            if (columnIndex != -1 && !cursor.isNull(columnIndex)) {
                return cursor.getInt(columnIndex);
            }
            return 0; // Default kalau kolom belum ada
        } catch (Exception e) {
            return 0;
        }
    }
}