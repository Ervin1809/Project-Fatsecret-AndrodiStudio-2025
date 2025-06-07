package com.example.fatsecret.data.repository;

import android.util.Log;

import com.example.fatsecret.data.model.Ingredient;
import com.example.fatsecret.data.model.UsdaFoodDetailResponse;
import com.example.fatsecret.data.network.ApiConfig;
import com.example.fatsecret.data.network.ApiService;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UsdaIngredientRepository {
    private ApiService apiService = ApiConfig.getApiService();
    private static final String API_KEY = "bbBcmZB4VlUAgYlLew03Yt1ojYum5nML7jt1T8MQ";

    public void fetchIngredientByFdcId(String fdcId, IngredientCallback callback) {
        Ingredient ingredient = new Ingredient();
        ingredient.setApiSource("usda");

        // Counter untuk track semua request selesai
        final int[] completedRequests = {0};
        final int totalRequests = 5; // 4 nutrisi + 1 basic info

        // 1. Get basic info (name, fdcId)
        getBasicInfo(fdcId, ingredient, completedRequests, totalRequests, callback);

        // 2. Get Calories (208)
        getCalories(fdcId, ingredient, completedRequests, totalRequests, callback);

        // 3. Get Protein (203)
        getProtein(fdcId, ingredient, completedRequests, totalRequests, callback);

        // 4. Get Fat (204)
        getFat(fdcId, ingredient, completedRequests, totalRequests, callback);

        // 5. Get Carbs (205)
        getCarbs(fdcId, ingredient, completedRequests, totalRequests, callback);
    }

    private void getBasicInfo(String fdcId, Ingredient ingredient, int[] completedRequests, int totalRequests, IngredientCallback callback) {
        apiService.getCalories(fdcId, API_KEY, "208").enqueue(new Callback<UsdaFoodDetailResponse>() {
            @Override
            public void onResponse(Call<UsdaFoodDetailResponse> call, Response<UsdaFoodDetailResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ingredient.setFdcId(Integer.parseInt(fdcId));
                    ingredient.setName(response.body().getDescription());
                    Log.d("BASIC_INFO", "Name: " + response.body().getDescription());
                }
                checkAndComplete(completedRequests, totalRequests, ingredient, callback);
            }

            @Override
            public void onFailure(Call<UsdaFoodDetailResponse> call, Throwable t) {
                Log.e("BASIC_INFO_ERROR", "Failed to get basic info", t);
                checkAndComplete(completedRequests, totalRequests, ingredient, callback);
            }
        });
    }

    private void getCalories(String fdcId, Ingredient ingredient, int[] completedRequests, int totalRequests, IngredientCallback callback) {
        apiService.getCalories(fdcId, API_KEY, "208").enqueue(new Callback<UsdaFoodDetailResponse>() {
            @Override
            public void onResponse(Call<UsdaFoodDetailResponse> call, Response<UsdaFoodDetailResponse> response) {
                if (response.isSuccessful() && response.body() != null &&
                        response.body().getFoodNutrients() != null &&
                        !response.body().getFoodNutrients().isEmpty()) {

                    float calories = response.body().getFoodNutrients().get(0).getAmount();
                    ingredient.setCaloriesPer100g(calories);
                    Log.d("CALORIES", "Value: " + calories);
                }
                checkAndComplete(completedRequests, totalRequests, ingredient, callback);
            }

            @Override
            public void onFailure(Call<UsdaFoodDetailResponse> call, Throwable t) {
                Log.e("CALORIES_ERROR", "Failed to get calories", t);
                checkAndComplete(completedRequests, totalRequests, ingredient, callback);
            }
        });
    }

    private void getProtein(String fdcId, Ingredient ingredient, int[] completedRequests, int totalRequests, IngredientCallback callback) {
        apiService.getProtein(fdcId, API_KEY, "203").enqueue(new Callback<UsdaFoodDetailResponse>() {
            @Override
            public void onResponse(Call<UsdaFoodDetailResponse> call, Response<UsdaFoodDetailResponse> response) {
                if (response.isSuccessful() && response.body() != null &&
                        response.body().getFoodNutrients() != null &&
                        !response.body().getFoodNutrients().isEmpty()) {

                    float protein = response.body().getFoodNutrients().get(0).getAmount();
                    ingredient.setProteinPer100g(protein);
                    Log.d("PROTEIN", "Value: " + protein);
                }
                checkAndComplete(completedRequests, totalRequests, ingredient, callback);
            }

            @Override
            public void onFailure(Call<UsdaFoodDetailResponse> call, Throwable t) {
                Log.e("PROTEIN_ERROR", "Failed to get protein", t);
                checkAndComplete(completedRequests, totalRequests, ingredient, callback);
            }
        });
    }

    private void getFat(String fdcId, Ingredient ingredient, int[] completedRequests, int totalRequests, IngredientCallback callback) {
        apiService.getFat(fdcId, API_KEY, "204").enqueue(new Callback<UsdaFoodDetailResponse>() {
            @Override
            public void onResponse(Call<UsdaFoodDetailResponse> call, Response<UsdaFoodDetailResponse> response) {
                if (response.isSuccessful() && response.body() != null &&
                        response.body().getFoodNutrients() != null &&
                        !response.body().getFoodNutrients().isEmpty()) {

                    float fat = response.body().getFoodNutrients().get(0).getAmount();
                    ingredient.setFatPer100g(fat);
                    Log.d("FAT", "Value: " + fat);
                }
                checkAndComplete(completedRequests, totalRequests, ingredient, callback);
            }

            @Override
            public void onFailure(Call<UsdaFoodDetailResponse> call, Throwable t) {
                Log.e("FAT_ERROR", "Failed to get fat", t);
                checkAndComplete(completedRequests, totalRequests, ingredient, callback);
            }
        });
    }

    private void getCarbs(String fdcId, Ingredient ingredient, int[] completedRequests, int totalRequests, IngredientCallback callback) {
        apiService.getCarbs(fdcId, API_KEY, "205").enqueue(new Callback<UsdaFoodDetailResponse>() {
            @Override
            public void onResponse(Call<UsdaFoodDetailResponse> call, Response<UsdaFoodDetailResponse> response) {
                if (response.isSuccessful() && response.body() != null &&
                        response.body().getFoodNutrients() != null &&
                        !response.body().getFoodNutrients().isEmpty()) {

                    float carbs = response.body().getFoodNutrients().get(0).getAmount();
                    ingredient.setCarbsPer100g(carbs);
                    Log.d("CARBS", "Value: " + carbs);
                }
                checkAndComplete(completedRequests, totalRequests, ingredient, callback);
            }

            @Override
            public void onFailure(Call<UsdaFoodDetailResponse> call, Throwable t) {
                Log.e("CARBS_ERROR", "Failed to get carbs", t);
                checkAndComplete(completedRequests, totalRequests, ingredient, callback);
            }
        });
    }

    private synchronized void checkAndComplete(int[] completedRequests, int totalRequests, Ingredient ingredient, IngredientCallback callback) {
        completedRequests[0]++;
        Log.d("PROGRESS", "Completed: " + completedRequests[0] + "/" + totalRequests);

        if (completedRequests[0] >= totalRequests) {
            Log.d("FINAL_RESULT",
                    "Name: " + ingredient.getName() +
                            " | Cal: " + ingredient.getCaloriesPer100g() +
                            " | Protein: " + ingredient.getProteinPer100g() +
                            " | Fat: " + ingredient.getFatPer100g() +
                            " | Carbs: " + ingredient.getCarbsPer100g());

            callback.onSuccess(ingredient);
        }
    }

    public interface IngredientCallback {
        void onSuccess(Ingredient ingredient);
        void onError(Exception e);
    }
}