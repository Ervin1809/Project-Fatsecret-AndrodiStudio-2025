package com.example.fatsecret.data.model;

public class UserProfile {
    // ✅ Existing fields
    private int id;
    private int userId;
    private float height;
    private float weight;
    private float targetWeight;
    private String createdAt;
    private String updatedAt;

    // ✅ NEW: Daily nutrition targets
    private float dailyCaloriesTarget;
    private float dailyProteinTarget;
    private float dailyCarbsTarget;
    private float dailyFatTarget;

    // ✅ NEW: User profile data for calculations
    private String gender; // "male" or "female"
    private int age;
    private String activityLevel; // "sedentary", "light", "moderate", "active", "very_active"

    // ✅ Constructors
    public UserProfile() {
    }

    public UserProfile(int userId, float height, float weight, float targetWeight) {
        this.userId = userId;
        this.height = height;
        this.weight = weight;
        this.targetWeight = targetWeight;
    }

    // ✅ Existing getters and setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public float getHeight() {
        return height;
    }

    public void setHeight(float height) {
        this.height = height;
    }

    public float getWeight() {
        return weight;
    }

    public void setWeight(float weight) {
        this.weight = weight;
    }

    public float getTargetWeight() {
        return targetWeight;
    }

    public void setTargetWeight(float targetWeight) {
        this.targetWeight = targetWeight;
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

    // ✅ NEW: Daily nutrition targets getters/setters
    public float getDailyCaloriesTarget() {
        return dailyCaloriesTarget;
    }

    public void setDailyCaloriesTarget(float dailyCaloriesTarget) {
        this.dailyCaloriesTarget = dailyCaloriesTarget;
    }

    public float getDailyProteinTarget() {
        return dailyProteinTarget;
    }

    public void setDailyProteinTarget(float dailyProteinTarget) {
        this.dailyProteinTarget = dailyProteinTarget;
    }

    public float getDailyCarbsTarget() {
        return dailyCarbsTarget;
    }

    public void setDailyCarbsTarget(float dailyCarbsTarget) {
        this.dailyCarbsTarget = dailyCarbsTarget;
    }

    public float getDailyFatTarget() {
        return dailyFatTarget;
    }

    public void setDailyFatTarget(float dailyFatTarget) {
        this.dailyFatTarget = dailyFatTarget;
    }

    // ✅ NEW: Profile data getters/setters
    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getActivityLevel() {
        return activityLevel;
    }

    public void setActivityLevel(String activityLevel) {
        this.activityLevel = activityLevel;
    }

    // ✅ Existing helper methods
    public float calculateBMI() {
        if (height <= 0 || weight <= 0) return 0;
        float heightInMeters = height / 100f;
        return weight / (heightInMeters * heightInMeters);
    }

    public String getBMICategory() {
        float bmi = calculateBMI();
        if (bmi < 18.5) return "Underweight";
        else if (bmi < 25.0) return "Normal";
        else if (bmi < 30.0) return "Overweight";
        else return "Obese";
    }

    // ✅ NEW: Calculate BMR using Mifflin-St Jeor equation
    public float calculateBMR() {
        if (weight <= 0 || height <= 0 || age <= 0 || gender == null) return 0;

        if ("male".equalsIgnoreCase(gender)) {
            // BMR = 10 * weight(kg) + 6.25 * height(cm) - 5 * age + 5
            return (10 * weight) + (6.25f * height) - (5 * age) + 5;
        } else if ("female".equalsIgnoreCase(gender)) {
            // BMR = 10 * weight(kg) + 6.25 * height(cm) - 5 * age - 161
            return (10 * weight) + (6.25f * height) - (5 * age) - 161;
        }
        return 0;
    }

    // ✅ NEW: Calculate TDEE (Total Daily Energy Expenditure)
    public float calculateTDEE() {
        float bmr = calculateBMR();
        if (bmr <= 0 || activityLevel == null) return 0;

        float activityMultiplier;
        switch (activityLevel.toLowerCase()) {
            case "sedentary":
                activityMultiplier = 1.2f; // Little/no exercise
                break;
            case "light":
                activityMultiplier = 1.375f; // Light exercise 1-3 days/week
                break;
            case "moderate":
                activityMultiplier = 1.55f; // Moderate exercise 3-5 days/week
                break;
            case "active":
                activityMultiplier = 1.725f; // Hard exercise 6-7 days/week
                break;
            case "very_active":
                activityMultiplier = 1.9f; // Very hard exercise, physical job
                break;
            default:
                activityMultiplier = 1.2f; // Default to sedentary
        }

        return bmr * activityMultiplier;
    }

    // ✅ NEW: Check if nutrition data is complete
    public boolean hasCompleteNutritionData() {
        return gender != null && !gender.isEmpty() &&
                age > 0 &&
                activityLevel != null && !activityLevel.isEmpty();
    }

    // ✅ NEW: Get activity level display name
    public String getActivityLevelDisplayName() {
        if (activityLevel == null) return "Not Set";

        switch (activityLevel.toLowerCase()) {
            case "sedentary":
                return "Sedentary (Little/no exercise)";
            case "light":
                return "Light (Exercise 1-3 days/week)";
            case "moderate":
                return "Moderate (Exercise 3-5 days/week)";
            case "active":
                return "Active (Exercise 6-7 days/week)";
            case "very_active":
                return "Very Active (Intense exercise)";
            default:
                return activityLevel;
        }
    }

    @Override
    public String toString() {
        return "UserProfile{" +
                "id=" + id +
                ", userId=" + userId +
                ", height=" + height +
                ", weight=" + weight +
                ", targetWeight=" + targetWeight +
                ", gender='" + gender + '\'' +
                ", age=" + age +
                ", activityLevel='" + activityLevel + '\'' +
                ", dailyCaloriesTarget=" + dailyCaloriesTarget +
                ", bmi=" + calculateBMI() +
                ", bmr=" + calculateBMR() +
                ", tdee=" + calculateTDEE() +
                '}';
    }
}