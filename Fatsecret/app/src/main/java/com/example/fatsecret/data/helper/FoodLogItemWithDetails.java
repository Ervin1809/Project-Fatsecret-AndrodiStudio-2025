package com.example.fatsecret.data.helper;

import com.example.fatsecret.data.model.FoodLogItem;
import com.example.fatsecret.data.model.Ingredient;

public class FoodLogItemWithDetails {
    private FoodLogItem foodLogItem;
    private String ingredientName;
    private Ingredient ingredient;

    public FoodLogItemWithDetails(FoodLogItem foodLogItem, String ingredientName) {
        this.foodLogItem = foodLogItem;
        this.ingredientName = ingredientName;
    }

    public FoodLogItemWithDetails(FoodLogItem foodLogItem, Ingredient ingredient) {
        this.foodLogItem = foodLogItem;
        this.ingredient = ingredient;
        this.ingredientName = ingredient != null ? ingredient.getName() : null;
    }

    // Getters
    public FoodLogItem getFoodLogItem() {
        return foodLogItem;
    }

    public String getIngredientName() {
        return ingredientName;
    }

    public Ingredient getIngredient() {
        return ingredient;
    }

    // Setters
    public void setFoodLogItem(FoodLogItem foodLogItem) {
        this.foodLogItem = foodLogItem;
    }

    public void setIngredientName(String ingredientName) {
        this.ingredientName = ingredientName;
    }

    public void setIngredient(Ingredient ingredient) {
        this.ingredient = ingredient;
        this.ingredientName = ingredient != null ? ingredient.getName() : null;
    }

    // Helper methods
    public float getCalories() {
        return foodLogItem != null ? foodLogItem.getCalculatedCalories() : 0;
    }

    public float getProtein() {
        return foodLogItem != null ? foodLogItem.getCalculatedProtein() : 0;
    }

    public float getCarbs() {
        return foodLogItem != null ? foodLogItem.getCalculatedCarbs() : 0;
    }

    public float getFat() {
        return foodLogItem != null ? foodLogItem.getCalculatedFat() : 0;
    }
}
