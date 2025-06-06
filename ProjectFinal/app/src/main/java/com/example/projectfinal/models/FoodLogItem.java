package com.example.projectfinal.models;

public class FoodLogItem {
    int id, foodLogId; // foreign key ke food_logs.id
    String type;              // enum: "menu" atau "ingredient"
    Integer menuId, ingredientId;   // nullable, jika type == "menu" // nullable, jika type == "ingredient"
    float quantityInGrams, calories, protein, carbs, fat; // jumlah dalam gram

    public FoodLogItem(int id, int foodLogId, String type, Integer menuId, Integer ingredientId, float quantityInGrams, float calories, float protein, float carbs, float fat) {
        this.id = id;
        this.foodLogId = foodLogId;
        this.type = type;
        this.menuId = menuId;
        this.ingredientId = ingredientId;
        this.quantityInGrams = quantityInGrams;
        this.calories = calories;
        this.protein = protein;
        this.carbs = carbs;
        this.fat = fat;
    }

    public FoodLogItem() {
    }

    public Integer getMenuId() {
        return menuId;
    }

    public void setMenuId(Integer menuId) {
        this.menuId = menuId;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getFoodLogId() {
        return foodLogId;
    }

    public void setFoodLogId(int foodLogId) {
        this.foodLogId = foodLogId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Integer getIngredientId() {
        return ingredientId;
    }

    public void setIngredientId(Integer ingredientId) {
        this.ingredientId = ingredientId;
    }

    public float getQuantityInGrams() {
        return quantityInGrams;
    }

    public void setQuantityInGrams(float quantityInGrams) {
        this.quantityInGrams = quantityInGrams;
    }

    public float getCalories() {
        return calories;
    }

    public void setCalories(float calories) {
        this.calories = calories;
    }

    public float getProtein() {
        return protein;
    }

    public void setProtein(float protein) {
        this.protein = protein;
    }

    public float getCarbs() {
        return carbs;
    }

    public void setCarbs(float carbs) {
        this.carbs = carbs;
    }

    public float getFat() {
        return fat;
    }

    public void setFat(float fat) {
        this.fat = fat;
    }

}
