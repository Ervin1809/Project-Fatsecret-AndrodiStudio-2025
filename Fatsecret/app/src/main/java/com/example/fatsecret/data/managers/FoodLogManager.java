package com.example.fatsecret.data.managers;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.example.fatsecret.data.DatabaseHelper;
import com.example.fatsecret.data.model.FoodLogItem;
import com.example.fatsecret.data.model.Ingredient;
import com.example.fatsecret.utils.MealTime;
// ✅ Import your contracts
import com.example.fatsecret.data.contract.FoodLogContract.FoodLogEntry;
import com.example.fatsecret.data.contract.FoodLogItemContract.FoodLogItemEntry;
import com.example.fatsecret.data.contract.IngredientContract.IngredientEntry;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class FoodLogManager {
    private static final String TAG = "FoodLogManager";

    private DatabaseHelper dbHelper;
    private Context context;

    public FoodLogManager(Context context) {
        this.context = context;
        this.dbHelper = new DatabaseHelper(context);
    }

    // ✅ Main method: Add food to meal
    // ✅ MODIFY: Replace existing addFoodToMeal method in FoodLogManager.java
    public boolean addFoodToMeal(Ingredient ingredient, MealTime mealTime, double weightGrams, int userId) {
        if (ingredient == null) {
            Log.e(TAG, "❌ Ingredient is null");
            return false;
        }

        if (userId <= 0) {
            Log.e(TAG, "❌ Invalid user ID: " + userId);
            return false;
        }

        if (weightGrams <= 0) {
            Log.e(TAG, "❌ Invalid weight: " + weightGrams);
            return false;
        }

        SQLiteDatabase db = null;
        try {
            db = dbHelper.getWritableDatabase();
            db.beginTransaction();

            // Step 1: Get current date
            String currentDate = getCurrentDate();
            Log.d(TAG, "📅 Adding food for date: " + currentDate);
            Log.d(TAG, "👤 User ID: " + userId);
            Log.d(TAG, "🥘 Ingredient: " + ingredient.getName());
            Log.d(TAG, "⚖️ Weight: " + weightGrams + "g");

            // Step 2: Find or create FoodLog for today + mealTime
            long foodLogId = findOrCreateFoodLog(db, userId, currentDate, mealTime);
            Log.d(TAG, "🍽️ FoodLog ID: " + foodLogId);

            // Step 3: Save ingredient (if not exists)
            long ingredientId = saveIngredient(db, ingredient);
            Log.d(TAG, "🥘 Ingredient ID: " + ingredientId);

            // ✅ NEW STEP 4: Check if same ingredient already exists in this meal
            FoodLogItem existingItem = findExistingFoodLogItem(db, foodLogId, ingredientId);

            if (existingItem != null) {
                // ✅ MERGE: Update existing item with combined weight
                Log.d(TAG, "🔄 Found existing item, merging weights");
                boolean merged = mergeExistingFoodLogItem(db, existingItem, ingredient, weightGrams);

                if (merged) {
                    // Update FoodLog totals
                    updateFoodLogTotals(db, foodLogId);
                    db.setTransactionSuccessful();
                    Log.d(TAG, "🎉 Successfully merged " + ingredient.getName() + " in " + mealTime.getDisplayName());
                    return true;
                } else {
                    Log.e(TAG, "❌ Failed to merge existing item");
                    return false;
                }
            } else {
                // ✅ CREATE NEW: No existing item found, create new one
                Log.d(TAG, "➕ Creating new FoodLogItem");

                // Step 5: Calculate nutrition based on weight
                double factor = weightGrams / 100.0;
                double calculatedCalories = ingredient.getCaloriesPer100g() * factor;
                double calculatedProtein = ingredient.getProteinPer100g() * factor;
                double calculatedCarbs = ingredient.getCarbsPer100g() * factor;
                double calculatedFat = ingredient.getFatPer100g() * factor;

                Log.d(TAG, "📊 Calculated nutrition for " + weightGrams + "g:");
                Log.d(TAG, "  Calories: " + calculatedCalories);

                // Step 6: Create FoodLogItem
                long foodLogItemId = createFoodLogItem(db, foodLogId, ingredientId, weightGrams,
                        calculatedCalories, calculatedProtein, calculatedCarbs, calculatedFat);
                Log.d(TAG, "📋 FoodLogItem ID: " + foodLogItemId);

                if (foodLogItemId == -1) {
                    Log.e(TAG, "❌ Failed to create FoodLogItem");
                    return false;
                }

                // Step 7: Update FoodLog totals
                updateFoodLogTotals(db, foodLogId);

                db.setTransactionSuccessful();
                Log.d(TAG, "🎉 Successfully added " + ingredient.getName() + " to " + mealTime.getDisplayName());
                return true;
            }

        } catch (Exception e) {
            Log.e(TAG, "❌ Error adding food to meal: " + e.getMessage(), e);
            return false;
        } finally {
            if (db != null) {
                db.endTransaction();
                db.close();
            }
        }
    }

    // ✅ Find existing FoodLog or create new one
    private long findOrCreateFoodLog(SQLiteDatabase db, int userId, String date, MealTime mealTime) {
        // ✅ Use correct table and column names
        String query = "SELECT " + FoodLogEntry.COLUMN_ID +
                " FROM " + FoodLogEntry.TABLE_NAME +
                " WHERE " + FoodLogEntry.COLUMN_USER_ID + " = ? AND " +
                FoodLogEntry.COLUMN_DATE + " = ? AND " +
                FoodLogEntry.COLUMN_MEAL_TIME + " = ?";

        Cursor cursor = db.rawQuery(query, new String[]{
                String.valueOf(userId), date, mealTime.getValue()
        });

        if (cursor.moveToFirst()) {
            long existingId = cursor.getLong(0);
            cursor.close();
            Log.d(TAG, "📋 Found existing FoodLog: " + existingId);
            return existingId;
        }
        cursor.close();

        // Create new FoodLog
        ContentValues values = new ContentValues();
        values.put(FoodLogEntry.COLUMN_USER_ID, userId);
        values.put(FoodLogEntry.COLUMN_USER_PROFILE_ID, (Integer) null);
        values.put(FoodLogEntry.COLUMN_DATE, date);
        values.put(FoodLogEntry.COLUMN_MEAL_TIME, mealTime.getValue());
        values.put(FoodLogEntry.COLUMN_NOTE, (String) null);
        values.put(FoodLogEntry.COLUMN_TOTAL_CALORIES, 0.0);
        values.put(FoodLogEntry.COLUMN_TOTAL_PROTEIN, 0.0);
        values.put(FoodLogEntry.COLUMN_TOTAL_CARBS, 0.0);
        values.put(FoodLogEntry.COLUMN_TOTAL_FAT, 0.0);
        values.put(FoodLogEntry.COLUMN_CREATED_AT, getCurrentDateTime());
        values.put(FoodLogEntry.COLUMN_UPDATED_AT, getCurrentDateTime());

        long newId = db.insert(FoodLogEntry.TABLE_NAME, null, values);
        Log.d(TAG, "➕ Created new FoodLog: " + newId);
        return newId;
    }

    // ✅ Save ingredient to database (if not exists)
    private long saveIngredient(SQLiteDatabase db, Ingredient ingredient) {
        // Check if ingredient already exists by name
        String query = "SELECT " + IngredientEntry.COLUMN_ID +
                " FROM " + IngredientEntry.TABLE_NAME +
                " WHERE " + IngredientEntry.COLUMN_NAME + " = ? AND " +
                IngredientEntry.COLUMN_API_SOURCE + " = ?";

        Cursor cursor = db.rawQuery(query, new String[]{
                ingredient.getName(), ingredient.getApiSource()
        });

        if (cursor.moveToFirst()) {
            long existingId = cursor.getLong(0);
            cursor.close();
            Log.d(TAG, "🥘 Found existing ingredient: " + existingId);
            return existingId;
        }
        cursor.close();

        // Create new ingredient
        ContentValues values = new ContentValues();
        values.put(IngredientEntry.COLUMN_NAME, ingredient.getName());
        values.put(IngredientEntry.COLUMN_CALORIES_PER_100G, ingredient.getCaloriesPer100g());
        values.put(IngredientEntry.COLUMN_PROTEIN_PER_100G, ingredient.getProteinPer100g());
        values.put(IngredientEntry.COLUMN_CARBS_PER_100G, ingredient.getCarbsPer100g());
        values.put(IngredientEntry.COLUMN_FAT_PER_100G, ingredient.getFatPer100g());
        values.put(IngredientEntry.COLUMN_API_SOURCE, ingredient.getApiSource());
        values.put(IngredientEntry.COLUMN_CREATED_AT, getCurrentDateTime());
        values.put(IngredientEntry.COLUMN_UPDATED_AT, getCurrentDateTime());

        long newId = db.insert(IngredientEntry.TABLE_NAME, null, values);
        Log.d(TAG, "➕ Created new ingredient: " + newId);
        return newId;
    }

    // ✅ FIXED: Create FoodLogItem with correct column constants
    private long createFoodLogItem(SQLiteDatabase db, long foodLogId, long ingredientId,
                                   double weightGrams, double calories, double protein,
                                   double carbs, double fat) {
        ContentValues values = new ContentValues();
        values.put(FoodLogItemEntry.COLUMN_FOOD_LOG_ID, foodLogId);
        values.put(FoodLogItemEntry.COLUMN_INGREDIENT_ID, ingredientId);
        values.put(FoodLogItemEntry.COLUMN_WEIGHT_GRAMS, weightGrams);
        values.put(FoodLogItemEntry.COLUMN_CALCULATED_CALORIES, calories);
        values.put(FoodLogItemEntry.COLUMN_CALCULATED_PROTEIN, protein);
        values.put(FoodLogItemEntry.COLUMN_CALCULATED_CARBS, carbs);
        values.put(FoodLogItemEntry.COLUMN_CALCULATED_FAT, fat);
        values.put(FoodLogItemEntry.COLUMN_CREATED_AT, getCurrentDateTime());
        values.put(FoodLogItemEntry.COLUMN_UPDATED_AT, getCurrentDateTime());

        return db.insert(FoodLogItemEntry.TABLE_NAME, null, values);
    }

    // ✅ FIXED: Update FoodLog totals with correct column constants
    private void updateFoodLogTotals(SQLiteDatabase db, long foodLogId) {
        String query = "SELECT " +
                "SUM(" + FoodLogItemEntry.COLUMN_CALCULATED_CALORIES + ") as total_calories, " +
                "SUM(" + FoodLogItemEntry.COLUMN_CALCULATED_PROTEIN + ") as total_protein, " +
                "SUM(" + FoodLogItemEntry.COLUMN_CALCULATED_CARBS + ") as total_carbs, " +
                "SUM(" + FoodLogItemEntry.COLUMN_CALCULATED_FAT + ") as total_fat " +
                "FROM " + FoodLogItemEntry.TABLE_NAME +
                " WHERE " + FoodLogItemEntry.COLUMN_FOOD_LOG_ID + " = ?";

        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(foodLogId)});

        if (cursor.moveToFirst()) {
            double totalCalories = cursor.getDouble(0);
            double totalProtein = cursor.getDouble(1);
            double totalCarbs = cursor.getDouble(2);
            double totalFat = cursor.getDouble(3);

            ContentValues values = new ContentValues();
            values.put(FoodLogEntry.COLUMN_TOTAL_CALORIES, totalCalories);
            values.put(FoodLogEntry.COLUMN_TOTAL_PROTEIN, totalProtein);
            values.put(FoodLogEntry.COLUMN_TOTAL_CARBS, totalCarbs);
            values.put(FoodLogEntry.COLUMN_TOTAL_FAT, totalFat);
            values.put(FoodLogEntry.COLUMN_UPDATED_AT, getCurrentDateTime());

            db.update(FoodLogEntry.TABLE_NAME, values,
                    FoodLogEntry.COLUMN_ID + " = ?",
                    new String[]{String.valueOf(foodLogId)});

            Log.d(TAG, "📊 Updated totals - Calories: " + totalCalories +
                    ", Protein: " + totalProtein + ", Carbs: " + totalCarbs + ", Fat: " + totalFat);
        }
        cursor.close();
    }

    // ✅ NEW: Find existing FoodLogItem with same ingredient in same meal
    private FoodLogItem findExistingFoodLogItem(SQLiteDatabase db, long foodLogId, long ingredientId) {
        String query = "SELECT * FROM " + FoodLogItemEntry.TABLE_NAME +
                " WHERE " + FoodLogItemEntry.COLUMN_FOOD_LOG_ID + " = ? AND " +
                FoodLogItemEntry.COLUMN_INGREDIENT_ID + " = ? LIMIT 1";

        Cursor cursor = db.rawQuery(query, new String[]{
                String.valueOf(foodLogId), String.valueOf(ingredientId)
        });

        FoodLogItem existingItem = null;

        if (cursor.moveToFirst()) {
            existingItem = new FoodLogItem();
            existingItem.setId(cursor.getInt(cursor.getColumnIndexOrThrow(FoodLogItemEntry.COLUMN_ID)));
            existingItem.setFoodLogId(cursor.getInt(cursor.getColumnIndexOrThrow(FoodLogItemEntry.COLUMN_FOOD_LOG_ID)));
            existingItem.setIngredientId(cursor.getInt(cursor.getColumnIndexOrThrow(FoodLogItemEntry.COLUMN_INGREDIENT_ID)));
            existingItem.setWeightGrams(cursor.getFloat(cursor.getColumnIndexOrThrow(FoodLogItemEntry.COLUMN_WEIGHT_GRAMS)));
            existingItem.setCalculatedCalories(cursor.getFloat(cursor.getColumnIndexOrThrow(FoodLogItemEntry.COLUMN_CALCULATED_CALORIES)));
            existingItem.setCalculatedProtein(cursor.getFloat(cursor.getColumnIndexOrThrow(FoodLogItemEntry.COLUMN_CALCULATED_PROTEIN)));
            existingItem.setCalculatedCarbs(cursor.getFloat(cursor.getColumnIndexOrThrow(FoodLogItemEntry.COLUMN_CALCULATED_CARBS)));
            existingItem.setCalculatedFat(cursor.getFloat(cursor.getColumnIndexOrThrow(FoodLogItemEntry.COLUMN_CALCULATED_FAT)));

            Log.d(TAG, "🔍 Found existing item ID: " + existingItem.getId() +
                    " with weight: " + existingItem.getWeightGrams() + "g");
        }

        cursor.close();
        return existingItem;
    }

    // ✅ NEW: Merge new weight with existing FoodLogItem
    private boolean mergeExistingFoodLogItem(SQLiteDatabase db, FoodLogItem existingItem,
                                             Ingredient ingredient, double additionalWeight) {
        try {
            // Calculate new combined weight
            double oldWeight = existingItem.getWeightGrams();
            double newTotalWeight = oldWeight + additionalWeight;

            Log.d(TAG, "🔄 Merging weights: " + oldWeight + "g + " + additionalWeight + "g = " + newTotalWeight + "g");

            // Calculate new nutrition based on total weight
            double factor = newTotalWeight / 100.0;
            double newCalories = ingredient.getCaloriesPer100g() * factor;
            double newProtein = ingredient.getProteinPer100g() * factor;
            double newCarbs = ingredient.getCarbsPer100g() * factor;
            double newFat = ingredient.getFatPer100g() * factor;

            Log.d(TAG, "📊 New totals after merge:");
            Log.d(TAG, "  Weight: " + newTotalWeight + "g");
            Log.d(TAG, "  Calories: " + newCalories);

            // Update the existing item
            ContentValues values = new ContentValues();
            values.put(FoodLogItemEntry.COLUMN_WEIGHT_GRAMS, newTotalWeight);
            values.put(FoodLogItemEntry.COLUMN_CALCULATED_CALORIES, newCalories);
            values.put(FoodLogItemEntry.COLUMN_CALCULATED_PROTEIN, newProtein);
            values.put(FoodLogItemEntry.COLUMN_CALCULATED_CARBS, newCarbs);
            values.put(FoodLogItemEntry.COLUMN_CALCULATED_FAT, newFat);
            values.put(FoodLogItemEntry.COLUMN_UPDATED_AT, getCurrentDateTime());

            int updatedRows = db.update(FoodLogItemEntry.TABLE_NAME, values,
                    FoodLogItemEntry.COLUMN_ID + " = ?",
                    new String[]{String.valueOf(existingItem.getId())});

            if (updatedRows > 0) {
                Log.d(TAG, "✅ Successfully merged FoodLogItem ID: " + existingItem.getId());
                return true;
            } else {
                Log.e(TAG, "❌ No rows updated for FoodLogItem ID: " + existingItem.getId());
                return false;
            }

        } catch (Exception e) {
            Log.e(TAG, "❌ Error merging FoodLogItem: " + e.getMessage(), e);
            return false;
        }
    }

    // ✅ Helper methods
    private String getCurrentDate() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        return sdf.format(new Date());
    }

    private String getCurrentDateTime() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        return sdf.format(new Date());
    }
}