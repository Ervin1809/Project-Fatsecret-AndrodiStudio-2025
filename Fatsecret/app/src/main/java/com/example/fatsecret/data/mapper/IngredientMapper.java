package com.example.fatsecret.data.mapper;

import android.util.Log;

import com.example.fatsecret.data.model.Ingredient;
import com.example.fatsecret.data.model.UsdaFoodDetailResponse;

import java.util.List;

public class IngredientMapper {

    public static Ingredient fromUsda(UsdaFoodDetailResponse usda) {
        Ingredient ingredient = new Ingredient();
        ingredient.setFdcId(usda.getFdcId());
        ingredient.setName(usda.getDescription());
        ingredient.setApiSource("usda");

        if (usda.getFoodNutrients() != null && usda.getFoodNutrients().size() >= 4) {
            List<UsdaFoodDetailResponse.FoodNutrient> nutrients = usda.getFoodNutrients();

            // Log semua nilai untuk analisis
            Log.d("USDA_ANALYSIS", "Total nutrients: " + nutrients.size());
            for (int i = 0; i < nutrients.size(); i++) {
                Log.d("USDA_ANALYSIS", "Index " + i + ": " + nutrients.get(i).getAmount());
            }

            // Identifikasi berdasarkan karakteristik nilai
            identifyAndMapNutrients(nutrients, ingredient);

        } else {
            Log.e("USDA_MAPPING", "Insufficient nutrient data");
        }

        // Log final result
        Log.d("FINAL_INGREDIENT",
                "Cal: " + ingredient.getCaloriesPer100g() +
                        " | Protein: " + ingredient.getProteinPer100g() +
                        " | Carbs: " + ingredient.getCarbsPer100g() +
                        " | Fat: " + ingredient.getFatPer100g());

        return ingredient;
    }

    private static void identifyAndMapNutrients(List<UsdaFoodDetailResponse.FoodNutrient> nutrients, Ingredient ingredient) {
        // Cari nilai tertinggi (biasanya kalori)
        float maxValue = 0;
        int calorieIndex = -1;

        for (int i = 0; i < nutrients.size(); i++) {
            float amount = nutrients.get(i).getAmount();
            if (amount > maxValue) {
                maxValue = amount;
                calorieIndex = i;
            }
        }

        // Set kalori (nilai tertinggi)
        if (calorieIndex != -1) {
            ingredient.setCaloriesPer100g(nutrients.get(calorieIndex).getAmount());
            Log.d("MAPPED", "Calories (index " + calorieIndex + "): " + nutrients.get(calorieIndex).getAmount());
        }

        // Mapping sisa nutrisi berdasarkan range nilai yang masuk akal
        for (int i = 0; i < nutrients.size(); i++) {
            if (i == calorieIndex) continue; // Skip yang sudah di-set sebagai kalori

            float amount = nutrients.get(i).getAmount();

            // Protein: biasanya 5-30g per 100g
            if (amount >= 5 && amount <= 50 && ingredient.getProteinPer100g() == 0) {
                ingredient.setProteinPer100g(amount);
                Log.d("MAPPED", "Protein (index " + i + "): " + amount);
            }
            // Fat: biasanya 0-50g per 100g
            else if (amount >= 0 && amount <= 50 && ingredient.getFatPer100g() == 0) {
                ingredient.setFatPer100g(amount);
                Log.d("MAPPED", "Fat (index " + i + "): " + amount);
            }
            // Carbs: biasanya 0-80g per 100g
            else if (amount >= 0 && amount <= 80 && ingredient.getCarbsPer100g() == 0) {
                ingredient.setCarbsPer100g(amount);
                Log.d("MAPPED", "Carbs (index " + i + "): " + amount);
            }
        }

        // Fallback: jika masih ada yang kosong, map berdasarkan urutan sisa
        mapRemainingNutrients(nutrients, ingredient, calorieIndex);
    }

    private static void mapRemainingNutrients(List<UsdaFoodDetailResponse.FoodNutrient> nutrients, Ingredient ingredient, int calorieIndex) {
        int mappedCount = 0;

        for (int i = 0; i < nutrients.size(); i++) {
            if (i == calorieIndex) continue;

            float amount = nutrients.get(i).getAmount();

            if (ingredient.getProteinPer100g() == 0 && mappedCount == 0) {
                ingredient.setProteinPer100g(amount);
                Log.d("FALLBACK_MAPPED", "Protein (index " + i + "): " + amount);
                mappedCount++;
            } else if (ingredient.getFatPer100g() == 0 && mappedCount == 1) {
                ingredient.setFatPer100g(amount);
                Log.d("FALLBACK_MAPPED", "Fat (index " + i + "): " + amount);
                mappedCount++;
            } else if (ingredient.getCarbsPer100g() == 0 && mappedCount == 2) {
                ingredient.setCarbsPer100g(amount);
                Log.d("FALLBACK_MAPPED", "Carbs (index " + i + "): " + amount);
                break;
            }
        }
    }
}