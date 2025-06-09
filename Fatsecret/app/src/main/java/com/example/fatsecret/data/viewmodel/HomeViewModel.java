package com.example.fatsecret.data.viewmodel;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.fatsecret.data.helper.FoodLogItemHelper;
import com.example.fatsecret.data.helper.FoodLogItemWithDetails;
import com.example.fatsecret.data.model.FoodLog;
import com.example.fatsecret.data.model.FoodLogItem;
import com.example.fatsecret.data.model.Ingredient;
import com.example.fatsecret.data.model.User;
import com.example.fatsecret.data.model.UserProfile;
import com.example.fatsecret.data.repository.AuthRepository;
import com.example.fatsecret.data.repository.FoodLogItemRepository;
import com.example.fatsecret.data.repository.FoodLogRepository;
import com.example.fatsecret.data.repository.IngredientRepository;
import com.example.fatsecret.utils.MealTime;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class HomeViewModel extends AndroidViewModel {
    private static final String TAG = "HomeViewModel";

    private AuthRepository authRepository;
    private FoodLogRepository foodLogRepository;
    private FoodLogItemRepository foodLogItemRepository;
    private IngredientRepository ingredientRepository;

    // LiveData untuk UI
    private MutableLiveData<UserProfile> userProfile = new MutableLiveData<>();
    private MutableLiveData<List<FoodLog>> todayFoodLogs = new MutableLiveData<>();
    private MutableLiveData<Map<MealTime, List<FoodLogItemWithDetails>>> mealItems = new MutableLiveData<>();
    private MutableLiveData<NutritionSummary> dailyNutrition = new MutableLiveData<>();
    private MutableLiveData<String> errorMessage = new MutableLiveData<>();
    private MutableLiveData<Boolean> isLoading = new MutableLiveData<>();

    // Current user cache
    private User currentUser;

    public HomeViewModel(@NonNull Application application) {
        super(application);

        authRepository = new AuthRepository(getApplication());
        foodLogRepository = new FoodLogRepository(getApplication());
        foodLogItemRepository = new FoodLogItemRepository(getApplication());
        ingredientRepository = new IngredientRepository(getApplication());

        // Initialize dengan empty data
        mealItems.postValue(new HashMap<>());
        dailyNutrition.postValue(new NutritionSummary());
    }

    // Getters untuk LiveData
    public LiveData<UserProfile> getUserProfile() {
        return userProfile;
    }

    public LiveData<List<FoodLog>> getTodayFoodLogs() {
        return todayFoodLogs;
    }

    public LiveData<Map<MealTime, List<FoodLogItemWithDetails>>> getMealItems() {
        return mealItems;
    }

    public LiveData<NutritionSummary> getDailyNutrition() {
        return dailyNutrition;
    }

    public LiveData<String> getErrorMessage() {
        return errorMessage;
    }

    public LiveData<Boolean> getIsLoading() {
        return isLoading;
    }

    // Load data untuk hari ini
    public void loadTodayData() {
        isLoading.postValue(true);

        authRepository.getCurrentUser(new AuthRepository.UserCallback() {
            @Override
            public void onResult(User user) {
                if (user == null) {
                    errorMessage.postValue("User not logged in");
                    isLoading.postValue(false);
                    return;
                }

                currentUser = user;
                loadUserProfileAndData(user.getId());
            }
        });
    }

    private void loadUserProfileAndData(int userId) {
        authRepository.getUserProfile(userId, new AuthRepository.ProfileCallback() {
            @Override
            public void onSuccess(UserProfile profile) {
                userProfile.postValue(profile);
                loadTodayFoodLogs(userId);
            }

            @Override
            public void onError(Exception error) {
                Log.e(TAG, "Error loading user profile: " + error.getMessage());
                errorMessage.postValue("Failed to load user profile");
                isLoading.postValue(false);
            }
        });
    }

    private void loadTodayFoodLogs(int userId) {
        try {
            List<FoodLog> allFoodLogs = foodLogRepository.getAll();
            List<FoodLog> todayLogs = filterTodayFoodLogs(allFoodLogs, userId);

            todayFoodLogs.postValue(todayLogs);
            loadMealItemsWithDetails(todayLogs);
            calculateDailyNutrition(todayLogs);
            isLoading.postValue(false);

        } catch (Exception e) {
            Log.e(TAG, "Error loading today food logs: " + e.getMessage(), e);
            errorMessage.postValue("Failed to load food logs");
            isLoading.postValue(false);
        }
    }

    private List<FoodLog> filterTodayFoodLogs(List<FoodLog> allLogs, int userId) {
        String todayDate = getCurrentDate();
        List<FoodLog> todayLogs = new ArrayList<>();

        for (FoodLog log : allLogs) {
            if (log.getUserId() == userId && todayDate.equals(log.getDate())) {
                todayLogs.add(log);
            }
        }

        return todayLogs;
    }

    private void loadMealItemsWithDetails(List<FoodLog> foodLogs) {
        Map<MealTime, List<FoodLogItemWithDetails>> mealItemsMap = new HashMap<>();

        for (MealTime mealTime : MealTime.values()) {
            mealItemsMap.put(mealTime, new ArrayList<>());
        }

        for (FoodLog foodLog : foodLogs) {
            MealTime mealTime = MealTime.fromValue(foodLog.getMealTime());
            List<FoodLogItem> items = getFoodLogItemsByFoodLogId(foodLog.getId());

            List<FoodLogItemWithDetails> itemsWithDetails = new ArrayList<>();
            for (FoodLogItem item : items) {
                Ingredient ingredient = null;
                if (item.getIngredientId() != null) {
                    ingredient = ingredientRepository.getIngredientById(item.getIngredientId());
                }

                FoodLogItemWithDetails itemWithDetails = new FoodLogItemWithDetails(item, ingredient);
                itemsWithDetails.add(itemWithDetails);
            }

            mealItemsMap.put(mealTime, itemsWithDetails);
        }

        mealItems.postValue(mealItemsMap);
    }

    private List<FoodLogItem> getFoodLogItemsByFoodLogId(int foodLogId) {
        try {
            FoodLogItemHelper helper = new FoodLogItemHelper(getApplication());
            return helper.getFoodLogItemsByFoodLogId(foodLogId);
        } catch (Exception e) {
            Log.e(TAG, "Error getting food log items: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    private void calculateDailyNutrition(List<FoodLog> foodLogs) {
        float totalCalories = 0;
        float totalProtein = 0;
        float totalCarbs = 0;
        float totalFat = 0;

        for (FoodLog foodLog : foodLogs) {
            totalCalories += foodLog.getTotalCalories();
            totalProtein += foodLog.getTotalProtein();
            totalCarbs += foodLog.getTotalCarbs();
            totalFat += foodLog.getTotalFat();
        }

        NutritionSummary summary = new NutritionSummary();
        summary.consumedCalories = totalCalories;
        summary.consumedProtein = totalProtein;
        summary.consumedCarbs = totalCarbs;
        summary.consumedFat = totalFat;

        UserProfile profile = userProfile.getValue();
        if (profile != null) {
            summary.targetCalories = profile.getDailyCaloriesTarget();
            summary.targetProtein = profile.getDailyProteinTarget();
            summary.targetCarbs = profile.getDailyCarbsTarget();
            summary.targetFat = profile.getDailyFatTarget();
        }

        dailyNutrition.postValue(summary);
    }

    public void addFoodToMeal(MealTime mealTime, int ingredientId, float weightGrams) {
        if (currentUser == null) {
            errorMessage.postValue("User not logged in");
            return;
        }

        try {
            Ingredient ingredient = ingredientRepository.getIngredientById(ingredientId);
            if (ingredient == null) {
                errorMessage.postValue("Ingredient not found");
                return;
            }

            String todayDate = getCurrentDate();
            FoodLog foodLog = findOrCreateFoodLog(currentUser.getId(), todayDate, mealTime);

            FoodLogItem foodLogItem = new FoodLogItem();
            foodLogItem.setFoodLogId(foodLog.getId());
            foodLogItem.setIngredientId(ingredientId);
            foodLogItem.calculateNutritionFromIngredient(ingredient, weightGrams);

            long itemId = foodLogItemRepository.insert(foodLogItem);
            if (itemId > 0) {
                updateFoodLogTotals(foodLog);
                loadTodayData();
            } else {
                errorMessage.postValue("Failed to add food item");
            }

        } catch (Exception e) {
            Log.e(TAG, "Error adding food to meal: " + e.getMessage(), e);
            errorMessage.postValue("Failed to add food: " + e.getMessage());
        }
    }

    public void deleteFoodItem(FoodLogItemWithDetails itemWithDetails) {
        try {
            FoodLogItem item = itemWithDetails.getFoodLogItem();
            int deletedRows = foodLogItemRepository.delete(item.getId());

            if (deletedRows > 0) {
                FoodLog foodLog = foodLogRepository.getById(item.getFoodLogId());
                if (foodLog != null) {
                    updateFoodLogTotals(foodLog);
                }
                loadTodayData();
            } else {
                errorMessage.postValue("Failed to delete food item");
            }

        } catch (Exception e) {
            Log.e(TAG, "Error deleting food item: " + e.getMessage(), e);
            errorMessage.postValue("Failed to delete food: " + e.getMessage());
        }
    }

    private FoodLog findOrCreateFoodLog(int userId, String date, MealTime mealTime) {
        List<FoodLog> allLogs = foodLogRepository.getAll();

        for (FoodLog log : allLogs) {
            if (log.getUserId() == userId &&
                    date.equals(log.getDate()) &&
                    mealTime.getValue().equals(log.getMealTime())) {
                return log;
            }
        }

        FoodLog newLog = new FoodLog();
        newLog.setUserId(userId);
        newLog.setDate(date);
        newLog.setMealTime(mealTime.getValue());
        newLog.setTotalCalories(0);
        newLog.setTotalProtein(0);
        newLog.setTotalCarbs(0);
        newLog.setTotalFat(0);

        long logId = foodLogRepository.insert(newLog);
        newLog.setId((int) logId);

        return newLog;
    }

    private void updateFoodLogTotals(FoodLog foodLog) {
        List<FoodLogItem> items = getFoodLogItemsByFoodLogId(foodLog.getId());

        float totalCalories = 0;
        float totalProtein = 0;
        float totalCarbs = 0;
        float totalFat = 0;

        for (FoodLogItem item : items) {
            totalCalories += item.getCalculatedCalories();
            totalProtein += item.getCalculatedProtein();
            totalCarbs += item.getCalculatedCarbs();
            totalFat += item.getCalculatedFat();
        }

        foodLog.setTotalCalories(totalCalories);
        foodLog.setTotalProtein(totalProtein);
        foodLog.setTotalCarbs(totalCarbs);
        foodLog.setTotalFat(totalFat);

        foodLogRepository.update(foodLog);
    }

    private String getCurrentDate() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        return sdf.format(new Date());
    }

    public static class NutritionSummary {
        public float consumedCalories = 0;
        public float consumedProtein = 0;
        public float consumedCarbs = 0;
        public float consumedFat = 0;

        public float targetCalories = 0;
        public float targetProtein = 0;
        public float targetCarbs = 0;
        public float targetFat = 0;

        public int getCaloriesPercentage() {
            return targetCalories > 0 ? (int) ((consumedCalories / targetCalories) * 100) : 0;
        }

        public int getProteinPercentage() {
            return targetProtein > 0 ? (int) ((consumedProtein / targetProtein) * 100) : 0;
        }

        public int getCarbsPercentage() {
            return targetCarbs > 0 ? (int) ((consumedCarbs / targetCarbs) * 100) : 0;
        }

        public int getFatPercentage() {
            return targetFat > 0 ? (int) ((consumedFat / targetFat) * 100) : 0;
        }

        public float getRemainingCalories() {
            return Math.max(0, targetCalories - consumedCalories);
        }

        public float getRemainingProtein() {
            return Math.max(0, targetProtein - consumedProtein);
        }

        public float getRemainingCarbs() {
            return Math.max(0, targetCarbs - consumedCarbs);
        }

        public float getRemainingFat() {
            return Math.max(0, targetFat - consumedFat);
        }
    }
}