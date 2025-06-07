package com.example.fatsecret.data.model;

import java.util.List;

public class Menu {
    private int id;
    private String name;
    private String description;
    private float totalCalories;
    private float totalProtein;
    private float totalCarbs;
    private float totalFat;
    private int createdBy;     // Admin user ID
    private String createdAt;
    private String updatedAt;

    // For displaying menu with ingredients
    private List<MenuIngredient> ingredients;

    // Default Constructor
    public Menu() {}

    // Constructor yang match dengan MappingHelper
    public Menu(int id, String name, String description, float totalCalories,
                float totalProtein, float totalCarbs, float totalFat,
                int createdBy, String createdAt, String updatedAt) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.totalCalories = totalCalories;
        this.totalProtein = totalProtein;
        this.totalCarbs = totalCarbs;
        this.totalFat = totalFat;
        this.createdBy = createdBy;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    // Constructor untuk create new menu
    public Menu(String name, String description, int createdBy) {
        this.name = name;
        this.description = description;
        this.createdBy = createdBy;
        this.totalCalories = 0;
        this.totalProtein = 0;
        this.totalCarbs = 0;
        this.totalFat = 0;
        this.createdAt = getCurrentTimestamp();
        this.updatedAt = getCurrentTimestamp();
    }

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public float getTotalCalories() { return totalCalories; }
    public void setTotalCalories(float totalCalories) { this.totalCalories = totalCalories; }

    public float getTotalProtein() { return totalProtein; }
    public void setTotalProtein(float totalProtein) { this.totalProtein = totalProtein; }

    public float getTotalCarbs() { return totalCarbs; }
    public void setTotalCarbs(float totalCarbs) { this.totalCarbs = totalCarbs; }

    public float getTotalFat() { return totalFat; }
    public void setTotalFat(float totalFat) { this.totalFat = totalFat; }

    public int getCreatedBy() { return createdBy; }
    public void setCreatedBy(int createdBy) { this.createdBy = createdBy; }

    public String getCreatedAt() { return createdAt; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }

    public String getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(String updatedAt) { this.updatedAt = updatedAt; }

    public List<MenuIngredient> getIngredients() { return ingredients; }
    public void setIngredients(List<MenuIngredient> ingredients) {
        this.ingredients = ingredients;
        // Auto calculate nutrition ketika ingredients di-set
        calculateTotalNutrition();
    }

    // Helper methods
    public void calculateTotalNutrition() {
        if (ingredients == null || ingredients.isEmpty()) {
            totalCalories = totalProtein = totalCarbs = totalFat = 0;
            return;
        }

        totalCalories = 0;
        totalProtein = 0;
        totalCarbs = 0;
        totalFat = 0;

        for (MenuIngredient menuIngredient : ingredients) {
            float quantity = menuIngredient.getQuantityInGrams();
            Ingredient ingredient = menuIngredient.getIngredient();

            if (ingredient != null) {
                totalCalories += ingredient.getCaloriesForQuantity(quantity);
                totalProtein += ingredient.getProteinForQuantity(quantity);
                totalCarbs += ingredient.getCarbsForQuantity(quantity);
                totalFat += ingredient.getFatForQuantity(quantity);
            }
        }
    }

    private String getCurrentTimestamp() {
        return new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss", java.util.Locale.getDefault())
                .format(new java.util.Date());
    }

    @Override
    public String toString() {
        return name + " (" + totalCalories + " cal)";
    }
}