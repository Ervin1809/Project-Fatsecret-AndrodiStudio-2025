package com.example.fatsecret.utils;

import android.util.Log;
import com.example.fatsecret.data.model.UserProfile;

public class NutritionCalculator {
    private static final String TAG = "NutritionCalculator";

    // ✅ Activity Level Multipliers (berdasarkan penelitian ilmiah)
    public static final float SEDENTARY_MULTIPLIER = 1.2f;      // Duduk terus, minim olahraga
    public static final float LIGHT_MULTIPLIER = 1.375f;        // Olahraga ringan 1-3x/minggu
    public static final float MODERATE_MULTIPLIER = 1.55f;      // Olahraga sedang 3-5x/minggu
    public static final float ACTIVE_MULTIPLIER = 1.725f;       // Olahraga berat 6-7x/minggu
    public static final float VERY_ACTIVE_MULTIPLIER = 1.9f;    // Olahraga sangat berat + kerja fisik

    // ✅ Macro Distribution (persentase kalori harian)
    public static final float PROTEIN_PERCENTAGE = 0.25f;       // 25% dari total kalori
    public static final float CARBS_PERCENTAGE = 0.45f;         // 45% dari total kalori
    public static final float FAT_PERCENTAGE = 0.30f;           // 30% dari total kalori

    // ✅ Kalori per gram
    public static final float CALORIES_PER_GRAM_PROTEIN = 4f;
    public static final float CALORIES_PER_GRAM_CARBS = 4f;
    public static final float CALORIES_PER_GRAM_FAT = 9f;

    /**
     * Hitung semua target nutrisi harian dan update UserProfile
     */
    public static void calculateAndSetNutritionTargets(UserProfile profile) {
        if (profile == null) {
            Log.e(TAG, "❌ Profile is null");
            return;
        }

        if (!profile.hasCompleteNutritionData()) {
            Log.w(TAG, "⚠️ Incomplete nutrition data - cannot calculate targets");
            return;
        }

        try {
            // 1. Hitung BMR (Basal Metabolic Rate)
            float bmr = calculateBMR(profile);

            // 2. Hitung TDEE (Total Daily Energy Expenditure)
            float tdee = calculateTDEE(bmr, profile.getActivityLevel());

            // 3. Adjust kalori berdasarkan target berat
            float dailyCalories = adjustCaloriesForGoal(tdee, profile.getWeight(), profile.getTargetWeight());

            // 4. Hitung macro targets
            float proteinGrams = calculateProteinTarget(dailyCalories);
            float carbsGrams = calculateCarbsTarget(dailyCalories);
            float fatGrams = calculateFatTarget(dailyCalories);

            // 5. Set targets ke profile
            profile.setDailyCaloriesTarget(dailyCalories);
            profile.setDailyProteinTarget(proteinGrams);
            profile.setDailyCarbsTarget(carbsGrams);
            profile.setDailyFatTarget(fatGrams);

            Log.d(TAG, "✅ Nutrition targets calculated successfully:");
            Log.d(TAG, "BMR: " + bmr + " kcal");
            Log.d(TAG, "TDEE: " + tdee + " kcal");
            Log.d(TAG, "Daily Calories: " + dailyCalories + " kcal");
            Log.d(TAG, "Protein: " + proteinGrams + "g");
            Log.d(TAG, "Carbs: " + carbsGrams + "g");
            Log.d(TAG, "Fat: " + fatGrams + "g");

        } catch (Exception e) {
            Log.e(TAG, "❌ Error calculating nutrition targets: " + e.getMessage());
        }
    }

    /**
     * Hitung BMR menggunakan Mifflin-St Jeor equation
     */
    public static float calculateBMR(UserProfile profile) {
        if (profile.getWeight() <= 0 || profile.getHeight() <= 0 ||
                profile.getAge() <= 0 || profile.getGender() == null) {
            return 0;
        }

        float bmr;
        if ("male".equalsIgnoreCase(profile.getGender())) {
            // BMR Pria = 10 * berat(kg) + 6.25 * tinggi(cm) - 5 * umur + 5
            bmr = (10 * profile.getWeight()) + (6.25f * profile.getHeight()) - (5 * profile.getAge()) + 5;
        } else if ("female".equalsIgnoreCase(profile.getGender())) {
            // BMR Wanita = 10 * berat(kg) + 6.25 * tinggi(cm) - 5 * umur - 161
            bmr = (10 * profile.getWeight()) + (6.25f * profile.getHeight()) - (5 * profile.getAge()) - 161;
        } else {
            // Default ke average kalau gender tidak jelas
            float maleBMR = (10 * profile.getWeight()) + (6.25f * profile.getHeight()) - (5 * profile.getAge()) + 5;
            float femaleBMR = (10 * profile.getWeight()) + (6.25f * profile.getHeight()) - (5 * profile.getAge()) - 161;
            bmr = (maleBMR + femaleBMR) / 2;
        }

        return Math.max(bmr, 1000); // Minimum 1000 kalori untuk keamanan
    }

    /**
     * Hitung TDEE berdasarkan BMR dan activity level
     */
    public static float calculateTDEE(float bmr, String activityLevel) {
        if (bmr <= 0 || activityLevel == null) return bmr;

        float multiplier;
        switch (activityLevel.toLowerCase()) {
            case "sedentary":
                multiplier = SEDENTARY_MULTIPLIER;
                break;
            case "light":
                multiplier = LIGHT_MULTIPLIER;
                break;
            case "moderate":
                multiplier = MODERATE_MULTIPLIER;
                break;
            case "active":
                multiplier = ACTIVE_MULTIPLIER;
                break;
            case "very_active":
                multiplier = VERY_ACTIVE_MULTIPLIER;
                break;
            default:
                multiplier = MODERATE_MULTIPLIER; // Default ke moderate
        }

        return bmr * multiplier;
    }

    /**
     * Adjust kalori harian berdasarkan goal (turun/naik/maintain berat)
     */
    public static float adjustCaloriesForGoal(float tdee, float currentWeight, float targetWeight) {
        float weightDiff = targetWeight - currentWeight;

        if (Math.abs(weightDiff) < 1) {
            // Maintain berat (selisih < 1kg)
            return tdee;
        } else if (weightDiff < 0) {
            // Turun berat - deficit 500 kalori per hari (turun ~0.5kg/minggu)
            return Math.max(tdee - 500, tdee * 0.8f); // Max deficit 20% dari TDEE
        } else {
            // Naik berat - surplus 300 kalori per hari (naik ~0.3kg/minggu)
            return tdee + 300;
        }
    }

    /**
     * Hitung target protein harian (gram)
     */
    public static float calculateProteinTarget(float dailyCalories) {
        float proteinCalories = dailyCalories * PROTEIN_PERCENTAGE;
        return proteinCalories / CALORIES_PER_GRAM_PROTEIN;
    }

    /**
     * Hitung target karbohidrat harian (gram)
     */
    public static float calculateCarbsTarget(float dailyCalories) {
        float carbsCalories = dailyCalories * CARBS_PERCENTAGE;
        return carbsCalories / CALORIES_PER_GRAM_CARBS;
    }

    /**
     * Hitung target lemak harian (gram)
     */
    public static float calculateFatTarget(float dailyCalories) {
        float fatCalories = dailyCalories * FAT_PERCENTAGE;
        return fatCalories / CALORIES_PER_GRAM_FAT;
    }

    /**
     * Get activity level options untuk UI
     */
    public static String[] getActivityLevelOptions() {
        return new String[]{
                "sedentary",
                "light",
                "moderate",
                "active",
                "very_active"
        };
    }

    /**
     * Get activity level display names untuk UI
     */
    public static String[] getActivityLevelDisplayNames() {
        return new String[]{
                "Sedentary (Duduk terus, minim olahraga)",
                "Light (Olahraga ringan 1-3x/minggu)",
                "Moderate (Olahraga sedang 3-5x/minggu)",
                "Active (Olahraga berat 6-7x/minggu)",
                "Very Active (Olahraga sangat berat + kerja fisik)"
        };
    }
}