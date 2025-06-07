package com.example.fatsecret.data.model;

public class Ingredient {
    private int id;
    private String name;
    private String createdAt;
    private String updatedAt;
    private float caloriesPer100g;
    private float proteinPer100g;
    private float carbsPer100g;
    private float fatPer100g;
    private int fdcId;          // Changed to int untuk match MappingHelper
    private String apiSource;   // "local" or "usda"
    private String lastUpdated;

    // Default Constructor
    public Ingredient() {
    }

    // Constructor yang match dengan MappingHelper
    public Ingredient(int id, String name, String createdAt, String updatedAt,
                      float caloriesPer100g, float proteinPer100g, float carbsPer100g,
                      float fatPer100g, int fdcId, String apiSource, String lastUpdated) {
        this.id = id;
        this.name = name;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.caloriesPer100g = caloriesPer100g;
        this.proteinPer100g = proteinPer100g;
        this.carbsPer100g = carbsPer100g;
        this.fatPer100g = fatPer100g;
        this.fdcId = fdcId;
        this.apiSource = apiSource;
        this.lastUpdated = lastUpdated;
    }

    // Constructor untuk create new ingredient (local)
    public Ingredient(String name, float caloriesPer100g, float proteinPer100g,
                      float carbsPer100g, float fatPer100g) {
        this.name = name;
        this.caloriesPer100g = caloriesPer100g;
        this.proteinPer100g = proteinPer100g;
        this.carbsPer100g = carbsPer100g;
        this.fatPer100g = fatPer100g;
        this.apiSource = "local";
        this.fdcId = 0; // Default 0 untuk local ingredients
        this.createdAt = getCurrentTimestamp();
        this.updatedAt = getCurrentTimestamp();
    }

    // Constructor untuk USDA ingredients
    public Ingredient(String name, float caloriesPer100g, float proteinPer100g,
                      float carbsPer100g, float fatPer100g, int fdcId) {
        this(name, caloriesPer100g, proteinPer100g, carbsPer100g, fatPer100g);
        this.apiSource = "usda";
        this.fdcId = fdcId;
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }

    public float getCaloriesPer100g() {
        return caloriesPer100g;
    }

    public void setCaloriesPer100g(float caloriesPer100g) {
        this.caloriesPer100g = caloriesPer100g;
    }

    public float getProteinPer100g() {
        return proteinPer100g;
    }

    public void setProteinPer100g(float proteinPer100g) {
        this.proteinPer100g = proteinPer100g;
    }

    public float getCarbsPer100g() {
        return carbsPer100g;
    }

    public void setCarbsPer100g(float carbsPer100g) {
        this.carbsPer100g = carbsPer100g;
    }

    public float getFatPer100g() {
        return fatPer100g;
    }

    public void setFatPer100g(float fatPer100g) {
        this.fatPer100g = fatPer100g;
    }

    public int getFdcId() {
        return fdcId;
    }

    public void setFdcId(int fdcId) {
        this.fdcId = fdcId;
    }

    public String getApiSource() {
        return apiSource;
    }

    public void setApiSource(String apiSource) {
        this.apiSource = apiSource;
    }

    public String getLastUpdated() {
        return lastUpdated;
    }

    public void setLastUpdated(String lastUpdated) {
        this.lastUpdated = lastUpdated;
    }

    // Helper methods
    public float getCaloriesForQuantity(float grams) {
        return (caloriesPer100g * grams) / 100f;
    }

    public float getProteinForQuantity(float grams) {
        return (proteinPer100g * grams) / 100f;
    }

    public float getCarbsForQuantity(float grams) {
        return (carbsPer100g * grams) / 100f;
    }

    public float getFatForQuantity(float grams) {
        return (fatPer100g * grams) / 100f;
    }

    public boolean isFromUSDA() {
        return "usda".equals(apiSource);
    }

    private String getCurrentTimestamp() {
        return new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss", java.util.Locale.getDefault())
                .format(new java.util.Date());
    }

    @Override
    public String toString() {
        return name + " (" + caloriesPer100g + " cal/100g)";
    }
}