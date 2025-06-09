package com.example.fatsecret.utils;

public enum MealTime {
    BREAKFAST("breakfast", "🌅 Breakfast"),
    LUNCH("lunch", "☀️ Lunch"),
    DINNER("dinner", "🌆 Dinner"),
    SNACK("snack", "🍿 Snack");

    private final String value;
    private final String displayName;

    MealTime(String value, String displayName) {
        this.value = value;
        this.displayName = displayName;
    }

    public String getValue() { return value; }
    public String getDisplayName() { return displayName; }

    public static MealTime fromValue(String value) {
        for (MealTime mealTime : values()) {
            if (mealTime.value.equals(value)) {
                return mealTime;
            }
        }
        return BREAKFAST; // default
    }

    public static MealTime[] getAllMealTimes() {
        return values();
    }
}