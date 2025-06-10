package com.example.fatsecret.data.model;

public class FoodLogItem {
    private int id;
    private int foodLogId;
    private Integer ingredientId; // Nullable
    private float weightGrams;
    private float calculatedCalories;
    private float calculatedProtein;
    private float calculatedCarbs;
    private float calculatedFat;
    private String createdAt;
    private String updatedAt;

    // ✅ Constructors
    public FoodLogItem() {}

    public FoodLogItem(int foodLogId, int ingredientId, float weightGrams) {
        this.foodLogId = foodLogId;
        this.ingredientId = ingredientId;
        this.weightGrams = weightGrams;
    }

    // ✅ Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getFoodLogId() { return foodLogId; }
    public void setFoodLogId(int foodLogId) { this.foodLogId = foodLogId; }

    public Integer getIngredientId() { return ingredientId; }
    public void setIngredientId(Integer ingredientId) { this.ingredientId = ingredientId; }

    public float getWeightGrams() { return weightGrams; }
    public void setWeightGrams(float weightGrams) { this.weightGrams = weightGrams; }

    public float getCalculatedCalories() { return calculatedCalories; }
    public void setCalculatedCalories(float calculatedCalories) { this.calculatedCalories = calculatedCalories; }

    public float getCalculatedProtein() { return calculatedProtein; }
    public void setCalculatedProtein(float calculatedProtein) { this.calculatedProtein = calculatedProtein; }

    public float getCalculatedCarbs() { return calculatedCarbs; }
    public void setCalculatedCarbs(float calculatedCarbs) { this.calculatedCarbs = calculatedCarbs; }

    public float getCalculatedFat() { return calculatedFat; }
    public void setCalculatedFat(float calculatedFat) { this.calculatedFat = calculatedFat; }

    public String getCreatedAt() { return createdAt; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }

    public String getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(String updatedAt) { this.updatedAt = updatedAt; }

    // ✅ Helper method untuk calculate nutrition dari ingredient
    public void calculateNutritionFromIngredient(Ingredient ingredient, float weightGrams) {
        if (ingredient == null) return;

        float ratio = weightGrams / 100.0f; // Convert to per 100g ratio

        this.weightGrams = weightGrams;
        this.calculatedCalories = ingredient.getCaloriesPer100g() * ratio;
        this.calculatedProtein = ingredient.getProteinPer100g() * ratio;
        this.calculatedCarbs = ingredient.getCarbsPer100g() * ratio;
        this.calculatedFat = ingredient.getFatPer100g() * ratio;
    }


}
