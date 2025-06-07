package com.example.fatsecret.data.model;

import android.os.Parcel;
import android.os.Parcelable;

public class Ingredient implements Parcelable {
    private int id;
    private String name;
    private double calories;
    private double protein;
    private double carbohydrates;
    private double fat;
    private String unit;
    private String description;

    // Fields untuk USDA API integration
    private String fdcId;           // USDA Food Data Central ID
    private String apiSource;       // "usda", "local", atau "manual"
    private String lastUpdated;     // Format: "2025-06-07 05:04:23"
    private String brandOwner;      // Brand name dari USDA (opsional)
    private String ingredientList;  // Ingredient list dari USDA (opsional)

    // Constructor default
    public Ingredient() {}

    // Constructor untuk input manual (admin/user)
    public Ingredient(String name, double calories, double protein, double carbohydrates, double fat, String unit) {
        this.name = name;
        this.calories = calories;
        this.protein = protein;
        this.carbohydrates = carbohydrates;
        this.fat = fat;
        this.unit = unit;
        this.apiSource = "manual";
        this.lastUpdated = getCurrentTimestamp();
    }

    // Constructor untuk data dari USDA API
    public Ingredient(String fdcId, String name, double calories, double protein, double carbohydrates, double fat, String description, String brandOwner) {
        this.fdcId = fdcId;
        this.name = name;
        this.calories = calories;
        this.protein = protein;
        this.carbohydrates = carbohydrates;
        this.fat = fat;
        this.unit = "100g"; // USDA default per 100g
        this.description = description;
        this.brandOwner = brandOwner;
        this.apiSource = "usda";
        this.lastUpdated = getCurrentTimestamp();
    }

    // Constructor lengkap
    public Ingredient(int id, String name, double calories, double protein, double carbohydrates, double fat, String unit, String description, String fdcId, String apiSource, String lastUpdated, String brandOwner, String ingredientList) {
        this.id = id;
        this.name = name;
        this.calories = calories;
        this.protein = protein;
        this.carbohydrates = carbohydrates;
        this.fat = fat;
        this.unit = unit;
        this.description = description;
        this.fdcId = fdcId;
        this.apiSource = apiSource;
        this.lastUpdated = lastUpdated;
        this.brandOwner = brandOwner;
        this.ingredientList = ingredientList;
    }

    // Helper method untuk timestamp
    private String getCurrentTimestamp() {
        return java.time.LocalDateTime.now().format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }

    // Getter setter semua field
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public double getCalories() { return calories; }
    public void setCalories(double calories) { this.calories = calories; }

    public double getProtein() { return protein; }
    public void setProtein(double protein) { this.protein = protein; }

    public double getCarbohydrates() { return carbohydrates; }
    public void setCarbohydrates(double carbohydrates) { this.carbohydrates = carbohydrates; }

    public double getFat() { return fat; }
    public void setFat(double fat) { this.fat = fat; }

    public String getUnit() { return unit; }
    public void setUnit(String unit) { this.unit = unit; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getFdcId() { return fdcId; }
    public void setFdcId(String fdcId) { this.fdcId = fdcId; }

    public String getApiSource() { return apiSource; }
    public void setApiSource(String apiSource) { this.apiSource = apiSource; }

    public String getLastUpdated() { return lastUpdated; }
    public void setLastUpdated(String lastUpdated) { this.lastUpdated = lastUpdated; }

    public String getBrandOwner() { return brandOwner; }
    public void setBrandOwner(String brandOwner) { this.brandOwner = brandOwner; }

    public String getIngredientList() { return ingredientList; }
    public void setIngredientList(String ingredientList) { this.ingredientList = ingredientList; }

    // Parcelable implementation
    protected Ingredient(Parcel in) {
        id = in.readInt();
        name = in.readString();
        calories = in.readDouble();
        protein = in.readDouble();
        carbohydrates = in.readDouble();
        fat = in.readDouble();
        unit = in.readString();
        description = in.readString();
        fdcId = in.readString();
        apiSource = in.readString();
        lastUpdated = in.readString();
        brandOwner = in.readString();
        ingredientList = in.readString();
    }

    public static final Creator<Ingredient> CREATOR = new Creator<Ingredient>() {
        @Override
        public Ingredient createFromParcel(Parcel in) {
            return new Ingredient(in);
        }

        @Override
        public Ingredient[] newArray(int size) {
            return new Ingredient[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(name);
        dest.writeDouble(calories);
        dest.writeDouble(protein);
        dest.writeDouble(carbohydrates);
        dest.writeDouble(fat);
        dest.writeString(unit);
        dest.writeString(description);
        dest.writeString(fdcId);
        dest.writeString(apiSource);
        dest.writeString(lastUpdated);
        dest.writeString(brandOwner);
        dest.writeString(ingredientList);
    }
}