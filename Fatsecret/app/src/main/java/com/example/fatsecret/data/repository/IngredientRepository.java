package com.example.fatsecret.data.repository;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.example.fatsecret.data.DatabaseHelper;
import com.example.fatsecret.data.MappingHelper;
import com.example.fatsecret.data.contract.IngredientContract;
import com.example.fatsecret.data.model.Ingredient;
import com.example.fatsecret.data.network.ApiService;
import com.example.fatsecret.data.model.UsdaFoodDetailResponse;
import com.example.fatsecret.data.model.UsdaSearchResponse;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class IngredientRepository {
    private static final String TAG = "IngredientRepository";
    private static final String USDA_API_KEY = "DEMO_KEY"; // TODO: Ganti dengan API key asli
    private static final String BASE_URL = "https://api.nal.usda.gov/fdc/v1/";

    private final DatabaseHelper dbHelper;
    private final ApiService apiService;

    public IngredientRepository(Context context) {
        this.dbHelper = new DatabaseHelper(context);

        // Setup Retrofit
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        this.apiService = retrofit.create(ApiService.class);
    }

    /**
     * ✅ UPDATED: Search ingredients with dynamic USDA search
     */
    public List<Ingredient> searchIngredients(String query) {
        Log.d(TAG, "Searching for: " + query);

        // 1. Search local database first
        List<Ingredient> localResults = searchLocalIngredients(query);
        Log.d(TAG, "Local search found: " + localResults.size() + " ingredients");

        // 2. If enough local results, return them
        if (localResults.size() >= 3) {
            Log.d(TAG, "Sufficient local results, skipping USDA search");
            return localResults;
        }

        // 3. Search USDA API for more results (DYNAMIC)
        try {
            List<Ingredient> usdaResults = searchUsdaIngredientsDynamic(query);

            // 4. Save USDA results to local database
            for (Ingredient ingredient : usdaResults) {
                saveIngredient(ingredient);
            }

            // 5. Combine results (local + new USDA)
            List<Ingredient> combinedResults = new ArrayList<>(localResults);
            combinedResults.addAll(usdaResults);

            Log.d(TAG, "Total results: " + combinedResults.size());
            return combinedResults;

        } catch (Exception e) {
            Log.e(TAG, "USDA search failed, returning local results only", e);
            return localResults;
        }
    }

    /**
     * ✅ NEW: Dynamic USDA search using search API
     */
    /**
     * ✅ UPDATED: Search USDA using dynamic search + original working nutrition extraction
     */
    /**
     * ✅ UPDATED: Search until we get 10 complete ingredients
     */
    private List<Ingredient> searchUsdaIngredientsDynamic(String query) throws Exception {
        Log.d(TAG, "🔍 Dynamic USDA search for: " + query + " (target: 10 complete ingredients)");

        List<Ingredient> completeIngredients = new ArrayList<>();

        // 1. Get FDC IDs using existing method
        List<Integer> fdcIds = searchUsdaFoodIds(query);

        if (fdcIds.isEmpty()) {
            Log.w(TAG, "No food IDs found for query: " + query);
            return completeIngredients;
        }

        Log.d(TAG, "📋 Found " + fdcIds.size() + " potential ingredients to check");

        // 2. ✅ NEW: Process until we have 10 complete ingredients
        int processedCount = 0;
        for (int fdcId : fdcIds) {
            // Stop if we have enough complete ingredients
            if (completeIngredients.size() >= 10) {
                Log.d(TAG, "✅ Target reached: 10 complete ingredients found");
                break;
            }

            // Safety limit to avoid too many API calls
            if (processedCount >= 20) {
                Log.w(TAG, "⚠️ Reached processing limit (20), stopping search");
                break;
            }

            processedCount++;
            Log.d(TAG, "🔍 Processing ingredient " + processedCount + " (FDC ID: " + fdcId +
                    ") - Current complete: " + completeIngredients.size());

            try {
                // ✅ Use existing method with validation
                Ingredient ingredient = getCompleteNutritionFromUsda(String.valueOf(fdcId));

                if (ingredient != null) {
                    completeIngredients.add(ingredient);
                    Log.d(TAG, "✅ Added complete ingredient: " + ingredient.getName() +
                            " (Total: " + completeIngredients.size() + "/10)");
                } else {
                    Log.d(TAG, "⏭️ Skipped incomplete ingredient (FDC ID: " + fdcId + ")");
                }

            } catch (Exception e) {
                Log.w(TAG, "❌ Error processing FDC ID " + fdcId + ": " + e.getMessage());
                // Continue to next ingredient
            }

            // Small delay to avoid overwhelming API
            try {
                Thread.sleep(150);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }

        Log.d(TAG, "🎯 Search completed: " + completeIngredients.size() + " complete ingredients found " +
                "(processed " + processedCount + " total)");

        return completeIngredients;
    }

    /**
     * ✅ NEW: Search USDA API for food IDs matching query
     */
    /**
     * ✅ UPDATED: Get more FDC IDs to ensure enough candidates
     */
    private List<Integer> searchUsdaFoodIds(String query) throws Exception {
        Log.d(TAG, "🔍 Searching USDA food IDs for: " + query);

        List<Integer> fdcIds = new ArrayList<>();

        try {
            // ✅ UPDATED: Increase page size for more candidates
            Call<UsdaSearchResponse> call = apiService.searchFoods(
                    query,                      // search query
                    USDA_API_KEY,              // api key
                    25,                        // ✅ INCREASED: page size (was 15)
                    1,                         // page number
                    "Foundation,SR Legacy"     // data types (most reliable)
            );

            Response<UsdaSearchResponse> response = call.execute();

            if (response.isSuccessful() && response.body() != null) {
                UsdaSearchResponse searchResponse = response.body();

                Log.d(TAG, "USDA search API success. Total hits: " + searchResponse.getTotalHits());

                if (searchResponse.getFoods() != null && !searchResponse.getFoods().isEmpty()) {
                    for (UsdaSearchResponse.FoodSearchResult food : searchResponse.getFoods()) {
                        fdcIds.add(food.getFdcId());

                        Log.d(TAG, "Found food: " + food.getDescription() + " (FDC: " + food.getFdcId() + ")");

                        // ✅ UPDATED: Increase limit for more candidates
                        if (fdcIds.size() >= 20) { // Was 10, now 20
                            Log.d(TAG, "Reached maximum food IDs limit (20)");
                            break;
                        }
                    }
                } else {
                    Log.w(TAG, "No foods found in USDA search response");
                }

                Log.d(TAG, "Extracted " + fdcIds.size() + " food IDs for query: " + query);

            } else {
                Log.w(TAG, "USDA search API call failed: " + response.code() + " " + response.message());
                throw new Exception("USDA search API failed: " + response.code());
            }

        } catch (Exception e) {
            Log.e(TAG, "USDA search API exception: " + e.getMessage());
            throw e;
        }

        return fdcIds;
    }

    private Ingredient getCompleteNutritionFromUsda(String fdcId) throws Exception {
        Log.d(TAG, "🧪 Getting complete nutrition for FDC ID: " + fdcId);

        String name = "";
        float calories = 0, protein = 0, carbs = 0, fat = 0;

        // 1. ✅ Get Calories (Energy) - nutrient code 208
        try {
            Call<UsdaFoodDetailResponse> caloriesCall = apiService.getCalories(fdcId, USDA_API_KEY, "208");
            Response<UsdaFoodDetailResponse> caloriesResponse = caloriesCall.execute();

            if (caloriesResponse.isSuccessful() && caloriesResponse.body() != null) {
                UsdaFoodDetailResponse response = caloriesResponse.body();
                name = response.getDescription(); // Get name from first call

                if (response.getFoodNutrients() != null && !response.getFoodNutrients().isEmpty()) {
                    calories = response.getFoodNutrients().get(0).getAmount();
                    Log.d(TAG, "✅ Got calories: " + calories);
                }
            }
        } catch (Exception e) {
            Log.w(TAG, "Failed to get calories for " + fdcId, e);
        }

        // ✅ EARLY VALIDATION: Skip if no calories or invalid name
        if (name.isEmpty() || calories <= 0) {
            Log.w(TAG, "⏭️ SKIPPING: No calories or name found for FDC ID: " + fdcId);
            return null;
        }

        // 2. ✅ Get Protein - nutrient code 203
        try {
            Call<UsdaFoodDetailResponse> proteinCall = apiService.getProtein(fdcId, USDA_API_KEY, "203");
            Response<UsdaFoodDetailResponse> proteinResponse = proteinCall.execute();

            if (proteinResponse.isSuccessful() && proteinResponse.body() != null) {
                UsdaFoodDetailResponse response = proteinResponse.body();

                if (response.getFoodNutrients() != null && !response.getFoodNutrients().isEmpty()) {
                    protein = response.getFoodNutrients().get(0).getAmount();
                    Log.d(TAG, "✅ Got protein: " + protein);
                }
            }
        } catch (Exception e) {
            Log.w(TAG, "Failed to get protein for " + fdcId, e);
        }

        // ✅ EARLY VALIDATION: Skip if no protein
        if (protein < 0) {
            Log.w(TAG, "⏭️ SKIPPING: No protein found for " + name);
            return null;
        }

        // 3. ✅ Get Fat - nutrient code 204
        try {
            Call<UsdaFoodDetailResponse> fatCall = apiService.getFat(fdcId, USDA_API_KEY, "204");
            Response<UsdaFoodDetailResponse> fatResponse = fatCall.execute();

            if (fatResponse.isSuccessful() && fatResponse.body() != null) {
                UsdaFoodDetailResponse response = fatResponse.body();

                if (response.getFoodNutrients() != null && !response.getFoodNutrients().isEmpty()) {
                    fat = response.getFoodNutrients().get(0).getAmount();
                    Log.d(TAG, "✅ Got fat: " + fat);
                }
            }
        } catch (Exception e) {
            Log.w(TAG, "Failed to get fat for " + fdcId, e);
        }

        // ✅ EARLY VALIDATION: Skip if no fat data
        if (fat < 0) {
            Log.w(TAG, "⏭️ SKIPPING: No fat found for " + name);
            return null;
        }

        // 4. ✅ Get Carbs - nutrient code 205
        try {
            Call<UsdaFoodDetailResponse> carbsCall = apiService.getCarbs(fdcId, USDA_API_KEY, "205");
            Response<UsdaFoodDetailResponse> carbsResponse = carbsCall.execute();

            if (carbsResponse.isSuccessful() && carbsResponse.body() != null) {
                UsdaFoodDetailResponse response = carbsResponse.body();

                if (response.getFoodNutrients() != null && !response.getFoodNutrients().isEmpty()) {
                    carbs = response.getFoodNutrients().get(0).getAmount();
                    Log.d(TAG, "✅ Got carbs: " + carbs);
                }
            }
        } catch (Exception e) {
            Log.w(TAG, "Failed to get carbs for " + fdcId, e);
        }

        // ✅ EARLY VALIDATION: Skip if no carbs data
        if (carbs < 0) {
            Log.w(TAG, "⏭️ SKIPPING: No carbs found for " + name);
            return null;
        }

        // 5. ✅ Create Ingredient - all nutrients validated
        if (!name.isEmpty() && calories > 0 && protein >= 0 && carbs >= 0 && fat >= 0) {
            Ingredient ingredient = new Ingredient(name, calories, protein, carbs, fat, Integer.parseInt(fdcId));
            ingredient.setApiSource("usda"); // Make sure this method exists

            Log.d(TAG, "🎉 COMPLETE INGREDIENT: " + name +
                    " (Cal: " + calories + ", P: " + protein + ", C: " + carbs + ", F: " + fat + ")");

            return ingredient;
        }

        Log.w(TAG, "❌ Incomplete nutrition data for: " + name);
        return null;
    }

    /**
     * ✅ IMPROVED: Extract nutrient value with better logging
     */

    /**
     * Search local database
     */
    private List<Ingredient> searchLocalIngredients(String query) {
        List<Ingredient> ingredients = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        String selection = IngredientContract.IngredientEntry.COLUMN_NAME + " LIKE ?";
        String[] selectionArgs = {"%" + query + "%"};
        String orderBy = IngredientContract.IngredientEntry.COLUMN_NAME + " ASC";

        Cursor cursor = db.query(
                IngredientContract.IngredientEntry.TABLE_NAME,
                null,
                selection,
                selectionArgs,
                null,
                null,
                orderBy,
                "20"
        );

        ingredients = MappingHelper.mapCursorToIngredientList(cursor);
        db.close();

        return ingredients;
    }

    /**
     * Save ingredient to database
     */
    public long saveIngredient(Ingredient ingredient) {
        // Check if already exists (by FDC ID for USDA ingredients)
        if (ingredient.isFromUSDA() && getIngredientByFdcId(ingredient.getFdcId()) != null) {
            Log.d(TAG, "Ingredient already exists: " + ingredient.getName());
            return -1; // Already exists
        }

        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(IngredientContract.IngredientEntry.COLUMN_NAME, ingredient.getName());
        values.put(IngredientContract.IngredientEntry.COLUMN_CALORIES_PER_100G, ingredient.getCaloriesPer100g());
        values.put(IngredientContract.IngredientEntry.COLUMN_PROTEIN_PER_100G, ingredient.getProteinPer100g());
        values.put(IngredientContract.IngredientEntry.COLUMN_CARBS_PER_100G, ingredient.getCarbsPer100g());
        values.put(IngredientContract.IngredientEntry.COLUMN_FAT_PER_100G, ingredient.getFatPer100g());
        values.put(IngredientContract.IngredientEntry.COLUMN_API_SOURCE, ingredient.getApiSource());
        values.put(IngredientContract.IngredientEntry.COLUMN_FDC_ID, ingredient.getFdcId());
        values.put(IngredientContract.IngredientEntry.COLUMN_CREATED_AT, getCurrentTimestamp());
        values.put(IngredientContract.IngredientEntry.COLUMN_UPDATED_AT, getCurrentTimestamp());
        values.put(IngredientContract.IngredientEntry.COLUMN_LAST_UPDATED, getCurrentTimestamp());

        long result = db.insert(IngredientContract.IngredientEntry.TABLE_NAME, null, values);
        db.close();

        Log.d(TAG, "✅ Saved ingredient: " + ingredient.getName() + " with ID: " + result);
        return result;
    }

    /**
     * Get ingredient by FDC ID
     */
    private Ingredient getIngredientByFdcId(int fdcId) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        String selection = IngredientContract.IngredientEntry.COLUMN_FDC_ID + " = ?";
        String[] selectionArgs = {String.valueOf(fdcId)};

        Cursor cursor = db.query(
                IngredientContract.IngredientEntry.TABLE_NAME,
                null,
                selection,
                selectionArgs,
                null,
                null,
                null
        );

        Ingredient ingredient = null;
        if (cursor != null && cursor.moveToFirst()) {
            ingredient = MappingHelper.mapCursorToIngredient(cursor);
            cursor.close();
        }

        db.close();
        return ingredient;
    }

    /**
     * Get ingredient by ID
     */
    public Ingredient getIngredientById(int id) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        String selection = IngredientContract.IngredientEntry.COLUMN_ID + " = ?";
        String[] selectionArgs = {String.valueOf(id)};

        Cursor cursor = db.query(
                IngredientContract.IngredientEntry.TABLE_NAME,
                null,
                selection,
                selectionArgs,
                null,
                null,
                null
        );

        Ingredient ingredient = null;
        if (cursor != null && cursor.moveToFirst()) {
            ingredient = MappingHelper.mapCursorToIngredient(cursor);
            cursor.close();
        }

        db.close();
        return ingredient;
    }

    /**
     * ✅ NEW: Test method for debugging dynamic search
     */
    public void testDynamicSearch(String query) {
        Log.d(TAG, "🧪 Testing dynamic search for: " + query);

        try {
            List<Integer> fdcIds = searchUsdaFoodIds(query);
            Log.d(TAG, "✅ Found " + fdcIds.size() + " food IDs:");

            for (int i = 0; i < Math.min(fdcIds.size(), 3); i++) {
                int fdcId = fdcIds.get(i);
                Log.d(TAG, "  - FDC ID: " + fdcId);

                // Test getting nutrition for first few
                try {
                    Ingredient ingredient = getCompleteNutritionFromUsda(String.valueOf(fdcId));
                    if (ingredient != null) {
                        Log.d(TAG, "    ✅ " + ingredient.getName());
                    }
                } catch (Exception e) {
                    Log.w(TAG, "    ❌ Failed to get nutrition: " + e.getMessage());
                }
            }

        } catch (Exception e) {
            Log.e(TAG, "❌ Dynamic search test failed: " + e.getMessage());
        }
    }

    /**
     * ✅ NEW: Get all ingredients from local database
     */
    public List<Ingredient> getAllIngredients() {
        Log.d(TAG, "📊 Getting all ingredients from database");

        List<Ingredient> ingredients = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        try {
            String orderBy = IngredientContract.IngredientEntry.COLUMN_NAME + " ASC";

            Cursor cursor = db.query(
                    IngredientContract.IngredientEntry.TABLE_NAME,
                    null,        // all columns
                    null,        // no WHERE clause
                    null,        // no WHERE args
                    null,        // no GROUP BY
                    null,        // no HAVING
                    orderBy,     // ORDER BY name
                    "100"        // LIMIT to 100 items
            );

            if (cursor != null) {
                ingredients = MappingHelper.mapCursorToIngredientList(cursor);
                cursor.close();
            }

            Log.d(TAG, "✅ Retrieved " + ingredients.size() + " ingredients from database");

        } catch (Exception e) {
            Log.e(TAG, "❌ Error getting all ingredients: " + e.getMessage());
        } finally {
            db.close();
        }

        return ingredients;
    }

    /**
     * ✅ NEW: Get recent ingredients (alternative)
     */
    public List<Ingredient> getRecentIngredients(int limit) {
        Log.d(TAG, "📊 Getting recent " + limit + " ingredients");

        List<Ingredient> ingredients = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        try {
            String orderBy = IngredientContract.IngredientEntry.COLUMN_UPDATED_AT + " DESC";

            Cursor cursor = db.query(
                    IngredientContract.IngredientEntry.TABLE_NAME,
                    null,
                    null,
                    null,
                    null,
                    null,
                    orderBy,
                    String.valueOf(limit)
            );

            if (cursor != null) {
                ingredients = MappingHelper.mapCursorToIngredientList(cursor);
                cursor.close();
            }

            Log.d(TAG, "✅ Retrieved " + ingredients.size() + " recent ingredients");

        } catch (Exception e) {
            Log.e(TAG, "❌ Error getting recent ingredients: " + e.getMessage());
        } finally {
            db.close();
        }

        return ingredients;
    }

    /**
     * ✅ NEW: Get ingredients count
     */
    public int getIngredientsCount() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        int count = 0;

        try {
            Cursor cursor = db.rawQuery("SELECT COUNT(*) FROM " +
                    IngredientContract.IngredientEntry.TABLE_NAME, null);

            if (cursor != null && cursor.moveToFirst()) {
                count = cursor.getInt(0);
                cursor.close();
            }

            Log.d(TAG, "📊 Total ingredients count: " + count);

        } catch (Exception e) {
            Log.e(TAG, "❌ Error counting ingredients: " + e.getMessage());
        } finally {
            db.close();
        }

        return count;
    }

    public void clearAllIngredients() {
        Log.d(TAG, "🗑️ Clearing all ingredients from database...");

        SQLiteDatabase db = dbHelper.getWritableDatabase();

        try {
            // Count before deletion
            Cursor countCursor = db.rawQuery("SELECT COUNT(*) FROM " +
                    IngredientContract.IngredientEntry.TABLE_NAME, null);

            int beforeCount = 0;
            if (countCursor.moveToFirst()) {
                beforeCount = countCursor.getInt(0);
            }
            countCursor.close();

            Log.d(TAG, "📊 Ingredients before deletion: " + beforeCount);

            // Delete all records
            int deletedRows = db.delete(IngredientContract.IngredientEntry.TABLE_NAME, null, null);

            Log.d(TAG, "✅ Deleted " + deletedRows + " ingredients from database");

            // Reset auto-increment counter
            db.execSQL("DELETE FROM sqlite_sequence WHERE name='" +
                    IngredientContract.IngredientEntry.TABLE_NAME + "'");

            Log.d(TAG, "✅ Database successfully cleared!");

        } catch (Exception e) {
            Log.e(TAG, "❌ Error clearing database: " + e.getMessage());
        } finally {
            db.close();
        }
    }

    /**
     * Clear ingredients with zero protein
     */
    public void clearZeroProteinIngredients() {
        Log.d(TAG, "🗑️ Clearing ingredients with zero protein...");

        SQLiteDatabase db = dbHelper.getWritableDatabase();

        try {
            // Delete zero protein records
            String whereClause = IngredientContract.IngredientEntry.COLUMN_PROTEIN_PER_100G + " = ?";
            String[] whereArgs = {"0"};

            int deletedRows = db.delete(IngredientContract.IngredientEntry.TABLE_NAME, whereClause, whereArgs);

            Log.d(TAG, "✅ Deleted " + deletedRows + " zero protein ingredients");

        } catch (Exception e) {
            Log.e(TAG, "❌ Error clearing zero protein ingredients: " + e.getMessage());
        } finally {
            db.close();
        }
    }

    private String getCurrentTimestamp() {
        return new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss", java.util.Locale.getDefault())
                .format(new java.util.Date());
    }
}