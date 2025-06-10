package com.example.fatsecret.ui.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fatsecret.R;
import com.example.fatsecret.data.model.User;
import com.example.fatsecret.data.model.FoodLog;
import com.example.fatsecret.data.model.FoodLogItem;
import com.example.fatsecret.data.model.UserProfile;
import com.example.fatsecret.data.repository.FoodLogRepository;
import com.example.fatsecret.data.repository.FoodLogItemRepository;
import com.example.fatsecret.data.repository.AuthRepository;
import com.example.fatsecret.data.viewmodel.AuthViewModel;
import com.example.fatsecret.ui.adapters.HistoryMealAdapter;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class HistoryFragment extends Fragment {
    private static final String TAG = "HistoryFragment";

    // UI Components
    private CalendarView calendarView;
    private TextView selectedDateText;
    private TextView dailySummaryText;
    private ProgressBar dailyProgressBar;
    private RecyclerView mealsRecyclerView;
    private ProgressBar loadingProgress;
    private TextView noMealsText;

    // Repositories (Direct database access)
    private FoodLogRepository foodLogRepository;
    private FoodLogItemRepository foodLogItemRepository;
    private AuthRepository authRepository;

    // AuthViewModel (only for current user)
    private AuthViewModel authViewModel;

    // Adapters
    private HistoryMealAdapter mealAdapter;

    // Background thread
    private ExecutorService executor;

    // Current data
    private String selectedDate;
    private int currentUserId;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.d(TAG, "🔧 Creating HistoryFragment view");
        View view = inflater.inflate(R.layout.fragment_history, container, false);

        initializeComponents();
        initializeViews(view);
        setupRecyclerView();
        setupCalendar();
        setupCurrentUser();

        // Set today as initial selected date
        selectedDate = getCurrentDate();
        updateSelectedDateUI();

        return view;
    }

    private void initializeComponents() {
        Log.d(TAG, "🔧 Initializing repositories");
        foodLogRepository = new FoodLogRepository(getContext());
        foodLogItemRepository = new FoodLogItemRepository(getContext());
        authRepository = new AuthRepository(getContext());
        authViewModel = new ViewModelProvider(requireActivity()).get(AuthViewModel.class);
        executor = Executors.newFixedThreadPool(2);
    }

    private void initializeViews(View view) {
        Log.d(TAG, "🔧 Initializing views");
        calendarView = view.findViewById(R.id.calendarView);
        selectedDateText = view.findViewById(R.id.tvSelectedDate);
        dailySummaryText = view.findViewById(R.id.tvDailySummary);
        dailyProgressBar = view.findViewById(R.id.progressBarDaily);
        mealsRecyclerView = view.findViewById(R.id.rvMeals);
        loadingProgress = view.findViewById(R.id.progressLoading);
        noMealsText = view.findViewById(R.id.tvNoMeals);
    }

    private void setupRecyclerView() {
        Log.d(TAG, "🔧 Setting up RecyclerView");
        mealAdapter = new HistoryMealAdapter();
        mealsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mealsRecyclerView.setAdapter(mealAdapter);
        mealsRecyclerView.setNestedScrollingEnabled(false);
    }

    private void setupCalendar() {
        Log.d(TAG, "🔧 Setting up Calendar");
        calendarView.setDate(System.currentTimeMillis());

        calendarView.setOnDateChangeListener((view, year, month, dayOfMonth) -> {
            selectedDate = String.format(Locale.getDefault(), "%04d-%02d-%02d", year, month + 1, dayOfMonth);
            Log.d(TAG, "📅 Selected date: " + selectedDate);

            updateSelectedDateUI();
            loadDataForSelectedDate();
        });
    }

    private void setupCurrentUser() {
        authViewModel.getCurrentUser().observe(this, user -> {
            if (user != null) {
                currentUserId = user.getId();
                Log.d(TAG, "👤 Current user: " + user.getName() + " (ID: " + currentUserId + ")");
                loadDataForSelectedDate();
            } else {
                Log.w(TAG, "❌ No current user found");
                showToast("Please log in to view history");
            }
        });
    }

    // ✅ SIMPLIFIED: Load data for selected date
    private void loadDataForSelectedDate() {
        if (currentUserId <= 0 || selectedDate == null) {
            Log.w(TAG, "❌ Cannot load data - userId: " + currentUserId + ", date: " + selectedDate);
            return;
        }

        Log.d(TAG, "🔄 Loading data for " + selectedDate + ", user: " + currentUserId);
        showLoading(true);

        executor.execute(() -> {
            try {
                // Get food logs for the date
                List<FoodLog> foodLogs = foodLogRepository.getFoodLogsByDateSync(selectedDate, currentUserId);
                Log.d(TAG, "📋 Found " + foodLogs.size() + " food logs");

                // Get all food log items for these logs
                List<FoodLogItem> allItems = new ArrayList<>();
                for (FoodLog log : foodLogs) {
                    List<FoodLogItem> items = foodLogItemRepository.getItemsByFoodLogIdSync(log.getId());
                    allItems.addAll(items);
                }
                Log.d(TAG, "🍽️ Found " + allItems.size() + " food items");

                // Calculate daily summary
                DailySummary summary = calculateDailySummary(foodLogs);

                // Update UI on main thread
                if (getActivity() != null) {
                    getActivity().runOnUiThread(() -> {
                        updateFoodLogsUI(foodLogs);
                        updateDailySummaryUI(summary);
                        showLoading(false);
                    });
                }

            } catch (Exception e) {
                Log.e(TAG, "❌ Error loading data: " + e.getMessage(), e);
                if (getActivity() != null) {
                    getActivity().runOnUiThread(() -> {
                        showToast("Failed to load food logs");
                        showLoading(false);
                    });
                }
            }
        });
    }

    // ✅ SIMPLIFIED: Calculate daily summary
    private DailySummary calculateDailySummary(List<FoodLog> foodLogs) {
        DailySummary summary = new DailySummary();

        // Calculate totals from food logs
        for (FoodLog log : foodLogs) {
            summary.totalCalories += log.getTotalCalories();
            summary.totalProtein += log.getTotalProtein();
            summary.totalCarbs += log.getTotalCarbs();
            summary.totalFat += log.getTotalFat();
        }

        // Get user targets (synchronous)
        try {
            UserProfile profile = authRepository.getUserProfileSync(currentUserId);
            if (profile != null) {
                summary.targetCalories = profile.getDailyCaloriesTarget();
                summary.targetProtein = profile.getDailyProteinTarget();
                summary.targetCarbs = profile.getDailyCarbsTarget();
                summary.targetFat = profile.getDailyFatTarget();
            }
        } catch (Exception e) {
            Log.w(TAG, "Could not get user profile, using defaults");
        }

        // Calculate progress
        summary.progressPercentage = summary.targetCalories > 0 ?
                Math.round((summary.totalCalories / summary.targetCalories) * 100) : 0;

        Log.d(TAG, "📊 Daily summary: " + summary.totalCalories + "/" + summary.targetCalories + " cal");
        return summary;
    }

    // ✅ Update UI methods
    private void updateFoodLogsUI(List<FoodLog> foodLogs) {
        if (foodLogs != null && !foodLogs.isEmpty()) {
            mealAdapter.updateLogs(foodLogs);
            mealsRecyclerView.setVisibility(View.VISIBLE);
            noMealsText.setVisibility(View.GONE);
            Log.d(TAG, "✅ Updated UI with " + foodLogs.size() + " food logs");
        } else {
            mealAdapter.updateLogs(null);
            mealsRecyclerView.setVisibility(View.GONE);
            noMealsText.setVisibility(View.VISIBLE);
            Log.d(TAG, "📭 No food logs for selected date");
        }
    }

    private void updateDailySummaryUI(DailySummary summary) {
        String summaryText = String.format(Locale.getDefault(),
                "Total: %.0f/%.0f cal (%.0f%% of target)",
                summary.totalCalories, summary.targetCalories,
                (summary.totalCalories / summary.targetCalories) * 100);

        dailySummaryText.setText(summaryText);
        dailyProgressBar.setProgress(summary.progressPercentage);
        Log.d(TAG, "📊 Updated daily summary: " + summaryText);
    }

    private void updateSelectedDateUI() {
        if (selectedDate != null) {
            String displayDate = formatDateForDisplay(selectedDate);
            selectedDateText.setText(displayDate);
        }
    }

    // ✅ Helper methods
    private void showLoading(boolean show) {
        if (loadingProgress != null) {
            loadingProgress.setVisibility(show ? View.VISIBLE : View.GONE);
        }
    }

    private void showToast(String message) {
        if (getContext() != null) {
            Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
        }
    }

    private String getCurrentDate() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        return sdf.format(new Date());
    }

    private String formatDateForDisplay(String date) {
        try {
            SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            SimpleDateFormat outputFormat = new SimpleDateFormat("EEEE, MMMM d, yyyy", Locale.getDefault());
            Date parsedDate = inputFormat.parse(date);

            String today = getCurrentDate();
            if (date.equals(today)) {
                return "📅 Today - " + outputFormat.format(parsedDate);
            } else {
                return "📅 " + outputFormat.format(parsedDate);
            }
        } catch (Exception e) {
            return "📅 " + date;
        }
    }

    // ✅ Simple data class for daily summary
    private static class DailySummary {
        float totalCalories = 0;
        float totalProtein = 0;
        float totalCarbs = 0;
        float totalFat = 0;
        float targetCalories = 2000;
        float targetProtein = 150;
        float targetCarbs = 250;
        float targetFat = 67;
        int progressPercentage = 0;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (executor != null && !executor.isShutdown()) {
            executor.shutdown();
        }
    }
}