package com.example.fatsecret.data.model;

public class FoodLog {
    private int id;
    private int userId;                    // ✅ ADD: foreign key ke users.id
    private int userProfileId;             // ✅ KEEP: foreign key ke user_profiles.id
    private String date;                   // ✅ ADD: date (YYYY-MM-DD format)
    private String mealTime;               // ✅ KEEP: breakfast, lunch, dinner, snack
    private String note;                   // ✅ KEEP: optional note

    // ✅ ADD: Total nutrition fields (calculated from food_log_items)
    private float totalCalories;
    private float totalProtein;
    private float totalCarbs;
    private float totalFat;

    private String createdAt;
    private String updatedAt;

    // ✅ Constructors
    public FoodLog() {}

    public FoodLog(int userId, int userProfileId, String date, String mealTime) {
        this.userId = userId;
        this.userProfileId = userProfileId;
        this.date = date;
        this.mealTime = mealTime;
        this.totalCalories = 0;
        this.totalProtein = 0;
        this.totalCarbs = 0;
        this.totalFat = 0;
    }

    // ✅ All Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    public int getUserProfileId() { return userProfileId; }
    public void setUserProfileId(int userProfileId) { this.userProfileId = userProfileId; }

    public String getDate() { return date; }
    public void setDate(String date) { this.date = date; }

    public String getMealTime() { return mealTime; }
    public void setMealTime(String mealTime) { this.mealTime = mealTime; }

    public String getNote() { return note; }
    public void setNote(String note) { this.note = note; }

    // ✅ Total nutrition getters/setters
    public float getTotalCalories() { return totalCalories; }
    public void setTotalCalories(float totalCalories) { this.totalCalories = totalCalories; }

    public float getTotalProtein() { return totalProtein; }
    public void setTotalProtein(float totalProtein) { this.totalProtein = totalProtein; }

    public float getTotalCarbs() { return totalCarbs; }
    public void setTotalCarbs(float totalCarbs) { this.totalCarbs = totalCarbs; }

    public float getTotalFat() { return totalFat; }
    public void setTotalFat(float totalFat) { this.totalFat = totalFat; }

    public String getCreatedAt() { return createdAt; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }

    public String getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(String updatedAt) { this.updatedAt = updatedAt; }

    // ✅ Helper methods
    public boolean isEmpty() {
        return totalCalories == 0 && totalProtein == 0 && totalCarbs == 0 && totalFat == 0;
    }

    public void addNutrition(float calories, float protein, float carbs, float fat) {
        this.totalCalories += calories;
        this.totalProtein += protein;
        this.totalCarbs += carbs;
        this.totalFat += fat;
    }

    public void resetTotals() {
        this.totalCalories = 0;
        this.totalProtein = 0;
        this.totalCarbs = 0;
        this.totalFat = 0;
    }
}