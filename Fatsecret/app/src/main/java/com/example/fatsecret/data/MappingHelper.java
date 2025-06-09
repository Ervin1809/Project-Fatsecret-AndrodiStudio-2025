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

        try {
            FoodLog foodLog = new FoodLog();

            // ✅ Basic fields
            foodLog.setId(cursor.getInt(cursor.getColumnIndexOrThrow(FoodLogContract.FoodLogEntry.COLUMN_ID)));
            foodLog.setUserId(cursor.getInt(cursor.getColumnIndexOrThrow(FoodLogContract.FoodLogEntry.COLUMN_USER_ID)));
            foodLog.setUserProfileId(cursor.getInt(cursor.getColumnIndexOrThrow(FoodLogContract.FoodLogEntry.COLUMN_USER_PROFILE_ID)));
            foodLog.setDate(cursor.getString(cursor.getColumnIndexOrThrow(FoodLogContract.FoodLogEntry.COLUMN_DATE)));
            foodLog.setMealTime(cursor.getString(cursor.getColumnIndexOrThrow(FoodLogContract.FoodLogEntry.COLUMN_MEAL_TIME)));

            // ✅ Optional note field
            int noteIndex = cursor.getColumnIndex(FoodLogContract.FoodLogEntry.COLUMN_NOTE);
            if (noteIndex != -1 && !cursor.isNull(noteIndex)) {
                foodLog.setNote(cursor.getString(noteIndex));
            }

            // ✅ Total nutrition fields
            foodLog.setTotalCalories(cursor.getFloat(cursor.getColumnIndexOrThrow(FoodLogContract.FoodLogEntry.COLUMN_TOTAL_CALORIES)));
            foodLog.setTotalProtein(cursor.getFloat(cursor.getColumnIndexOrThrow(FoodLogContract.FoodLogEntry.COLUMN_TOTAL_PROTEIN)));
            foodLog.setTotalCarbs(cursor.getFloat(cursor.getColumnIndexOrThrow(FoodLogContract.FoodLogEntry.COLUMN_TOTAL_CARBS)));
            foodLog.setTotalFat(cursor.getFloat(cursor.getColumnIndexOrThrow(FoodLogContract.FoodLogEntry.COLUMN_TOTAL_FAT)));

            // ✅ Timestamps
            foodLog.setCreatedAt(cursor.getString(cursor.getColumnIndexOrThrow(FoodLogContract.FoodLogEntry.COLUMN_CREATED_AT)));
            foodLog.setUpdatedAt(cursor.getString(cursor.getColumnIndexOrThrow(FoodLogContract.FoodLogEntry.COLUMN_UPDATED_AT)));

            return foodLog;

        } catch (Exception e) {
            Log.e("MappingHelper", "Error mapping cursor to FoodLog: " + e.getMessage(), e);
            return null;
        }
    }

    // ✅ List mapping method
    public static List<FoodLog> mapCursorToFoodLogList(Cursor cursor) {
        List<FoodLog> list = new ArrayList<>();

        if (cursor == null) {
            return list;
        }

        try {
            if (cursor.moveToFirst()) {
                do {
                    FoodLog foodLog = mapCursorToFoodLog(cursor);
                    if (foodLog != null) {
                        list.add(foodLog);
                    }
                } while (cursor.moveToNext());
            }
        } catch (Exception e) {
            Log.e("MappingHelper", "Error mapping cursor to FoodLog list: " + e.getMessage(), e);
        } finally {
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
        }

        return list;
    }

    // ✅ FIXED: Update mapCursorToFoodLogItem sesuai contract baru
    public static FoodLogItem mapCursorToFoodLogItem(Cursor cursor) {
        if (cursor == null) return null;

        try {
            // ✅ Update column names sesuai contract baru
            FoodLogItem item = new FoodLogItem();

            item.setId(cursor.getInt(cursor.getColumnIndexOrThrow(FoodLogItemContract.FoodLogItemEntry.COLUMN_ID)));
            item.setFoodLogId(cursor.getInt(cursor.getColumnIndexOrThrow(FoodLogItemContract.FoodLogItemEntry.COLUMN_FOOD_LOG_ID)));

            // ✅ Handle ingredient_id (bisa null)
            int ingredientIdIndex = cursor.getColumnIndex(FoodLogItemContract.FoodLogItemEntry.COLUMN_INGREDIENT_ID);
            if (ingredientIdIndex != -1 && !cursor.isNull(ingredientIdIndex)) {
                item.setIngredientId(cursor.getInt(ingredientIdIndex));
            }

            // ✅ Set weight dan calculated nutrition
            item.setWeightGrams(cursor.getFloat(cursor.getColumnIndexOrThrow(FoodLogItemContract.FoodLogItemEntry.COLUMN_WEIGHT_GRAMS)));
            item.setCalculatedCalories(cursor.getFloat(cursor.getColumnIndexOrThrow(FoodLogItemContract.FoodLogItemEntry.COLUMN_CALCULATED_CALORIES)));
            item.setCalculatedProtein(cursor.getFloat(cursor.getColumnIndexOrThrow(FoodLogItemContract.FoodLogItemEntry.COLUMN_CALCULATED_PROTEIN)));
            item.setCalculatedCarbs(cursor.getFloat(cursor.getColumnIndexOrThrow(FoodLogItemContract.FoodLogItemEntry.COLUMN_CALCULATED_CARBS)));
            item.setCalculatedFat(cursor.getFloat(cursor.getColumnIndexOrThrow(FoodLogItemContract.FoodLogItemEntry.COLUMN_CALCULATED_FAT)));

            // ✅ Set timestamps
            int createdAtIndex = cursor.getColumnIndex(FoodLogItemContract.FoodLogItemEntry.COLUMN_CREATED_AT);
            if (createdAtIndex != -1 && !cursor.isNull(createdAtIndex)) {
                item.setCreatedAt(cursor.getString(createdAtIndex));
            }

            int updatedAtIndex = cursor.getColumnIndex(FoodLogItemContract.FoodLogItemEntry.COLUMN_UPDATED_AT);
            if (updatedAtIndex != -1 && !cursor.isNull(updatedAtIndex)) {
                item.setUpdatedAt(cursor.getString(updatedAtIndex));
            }

            return item;

        } catch (Exception e) {
            Log.e("MappingHelper", "Error mapping cursor to FoodLogItem: " + e.getMessage(), e);
            return null;
        }
    }

    // ✅ List mapping method
    public static List<FoodLogItem> mapCursorToFoodLogItemList(Cursor cursor) {
        List<FoodLogItem> list = new ArrayList<>();

        if (cursor == null) {
            return list;
        }

        try {
            if (cursor.moveToFirst()) {
                do {
                    FoodLogItem item = mapCursorToFoodLogItem(cursor);
                    if (item != null) {
                        list.add(item);
                    }
                } while (cursor.moveToNext());
            }
        } catch (Exception e) {
            Log.e("MappingHelper", "Error mapping cursor to FoodLogItem list: " + e.getMessage(), e);
        } finally {
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
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