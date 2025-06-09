package com.example.fatsecret.ui.fragments;

import android.app.AlertDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fatsecret.R;
import com.example.fatsecret.data.helper.FoodLogItemWithDetails;
import com.example.fatsecret.data.model.User;
import com.example.fatsecret.data.model.UserProfile;
import com.example.fatsecret.data.viewmodel.AuthViewModel;
import com.example.fatsecret.data.viewmodel.HomeViewModel;
import com.example.fatsecret.ui.adapters.FoodLogItemAdapter;
import com.example.fatsecret.utils.MealTime;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class HomeFragment extends Fragment {
    private static final String TAG = "HomeFragment";

    // ViewModel
    private HomeViewModel viewModel;

    // UI Components
    private TextView tvDateHeader, tvUserGreeting;
    private TextView tvCaloriesProgress, tvProteinProgress, tvCarbsProgress, tvFatProgress;
    private ProgressBar progressCalories, progressProtein, progressCarbs, progressFat;

    // Meal Components
    private RecyclerView rvBreakfastItems, rvLunchItems, rvDinnerItems, rvSnackItems;
    private TextView tvBreakfastTotal, tvLunchTotal, tvDinnerTotal, tvSnackTotal;
    private MaterialButton btnAddBreakfast, btnAddLunch, btnAddDinner, btnAddSnack;

    // Adapters
    private FoodLogItemAdapter breakfastAdapter;
    private FoodLogItemAdapter lunchAdapter;
    private FoodLogItemAdapter dinnerAdapter;
    private FoodLogItemAdapter snackAdapter;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initializeViews(view);
        setupViewModel();
        setupRecyclerViews();
        setupClickListeners();
        observeViewModel();
//        updateNutritionProgress();

        // Load data
        viewModel.loadTodayData();
    }

    private void initializeViews(View view) {
        // Header views
        tvDateHeader = view.findViewById(R.id.tvDateHeader);
        tvUserGreeting = view.findViewById(R.id.tvUserGreeting);

        // Progress views
        tvCaloriesProgress = view.findViewById(R.id.tvCaloriesProgress);
        tvProteinProgress = view.findViewById(R.id.tvProteinProgress);
        tvCarbsProgress = view.findViewById(R.id.tvCarbsProgress);
        tvFatProgress = view.findViewById(R.id.tvFatProgress);

        progressCalories = view.findViewById(R.id.progressCalories);
        progressProtein = view.findViewById(R.id.progressProtein);
        progressCarbs = view.findViewById(R.id.progressCarbs);
        progressFat = view.findViewById(R.id.progressFat);

        // Meal RecyclerViews
        rvBreakfastItems = view.findViewById(R.id.rvBreakfastItems);
        rvLunchItems = view.findViewById(R.id.rvLunchItems);
        rvDinnerItems = view.findViewById(R.id.rvDinnerItems);
        rvSnackItems = view.findViewById(R.id.rvSnackItems);

        // Meal totals
        tvBreakfastTotal = view.findViewById(R.id.tvBreakfastTotal);
        tvLunchTotal = view.findViewById(R.id.tvLunchTotal);
        tvDinnerTotal = view.findViewById(R.id.tvDinnerTotal);
        tvSnackTotal = view.findViewById(R.id.tvSnackTotal);

        // Add buttons
        btnAddBreakfast = view.findViewById(R.id.btnAddBreakfast);
        btnAddLunch = view.findViewById(R.id.btnAddLunch);
        btnAddDinner = view.findViewById(R.id.btnAddDinner);
        btnAddSnack = view.findViewById(R.id.btnAddSnack);

        // Set current date
        updateDateHeader();

        // Set user greeting
//        tvUserGreeting.setText("Hi, Ervin! 👋");
    }

    private void setupViewModel() {
        viewModel = new ViewModelProvider(this).get(HomeViewModel.class);
    }

    private void setupRecyclerViews() {
        // Initialize adapters
        breakfastAdapter = new FoodLogItemAdapter(new ArrayList<>());
        lunchAdapter = new FoodLogItemAdapter(new ArrayList<>());
        dinnerAdapter = new FoodLogItemAdapter(new ArrayList<>());
        snackAdapter = new FoodLogItemAdapter(new ArrayList<>());

        // Setup RecyclerViews
        setupRecyclerView(rvBreakfastItems, breakfastAdapter);
        setupRecyclerView(rvLunchItems, lunchAdapter);
        setupRecyclerView(rvDinnerItems, dinnerAdapter);
        setupRecyclerView(rvSnackItems, snackAdapter);

        // Set item click listeners
        breakfastAdapter.setOnItemClickListener(createItemClickListener());
        lunchAdapter.setOnItemClickListener(createItemClickListener());
        dinnerAdapter.setOnItemClickListener(createItemClickListener());
        snackAdapter.setOnItemClickListener(createItemClickListener());
    }

    private void setupRecyclerView(RecyclerView recyclerView, FoodLogItemAdapter adapter) {
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);
        recyclerView.setNestedScrollingEnabled(false);
    }

    private FoodLogItemAdapter.OnItemClickListener createItemClickListener() {
        return new FoodLogItemAdapter.OnItemClickListener() {
            @Override
            public void onEditClick(FoodLogItemWithDetails item) {
                showEditFoodDialog(item);
            }

            @Override
            public void onDeleteClick(FoodLogItemWithDetails item) {
                showDeleteConfirmation(item);
            }
        };
    }

    private void setupClickListeners() {
        btnAddBreakfast.setOnClickListener(v -> showAddFoodDialog(MealTime.BREAKFAST));
        btnAddLunch.setOnClickListener(v -> showAddFoodDialog(MealTime.LUNCH));
        btnAddDinner.setOnClickListener(v -> showAddFoodDialog(MealTime.DINNER));
        btnAddSnack.setOnClickListener(v -> showAddFoodDialog(MealTime.SNACK));
    }

    private void observeViewModel() {
        // Observe user profile
        viewModel.getUserProfile().observe(getViewLifecycleOwner(), this::updateUserInfo);

        // Observe daily nutrition
        viewModel.getDailyNutrition().observe(getViewLifecycleOwner(), this::updateNutritionProgress);

        // Observe meal items
        viewModel.getMealItems().observe(getViewLifecycleOwner(), this::updateMealItems);

        // Observe loading state
        viewModel.getIsLoading().observe(getViewLifecycleOwner(), isLoading -> {
            Log.d(TAG, "Loading: " + isLoading);
            // TODO: Add loading indicator if needed
        });

        // Observe error messages
        viewModel.getErrorMessage().observe(getViewLifecycleOwner(), errorMessage -> {
            if (errorMessage != null && !errorMessage.isEmpty()) {
                Toast.makeText(getContext(), errorMessage, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateDateHeader() {
        SimpleDateFormat sdf = new SimpleDateFormat("EEEE, MMMM d", Locale.getDefault());
        String todayDate = sdf.format(new Date());
        tvDateHeader.setText(todayDate);
    }

    private void updateUserInfo(UserProfile userProfile) {
        if (userProfile != null) {
            // User greeting sudah di-set di initializeViews()
            Log.d(TAG, "User profile loaded successfully");
        }
    }

    private void updateNutritionProgress(HomeViewModel.NutritionSummary nutrition) {
        if (nutrition == null) return;

        // Update calories
        tvCaloriesProgress.setText(String.format("%.0f / %.0f kcal",
                nutrition.consumedCalories, nutrition.targetCalories));
        progressCalories.setProgress(nutrition.getCaloriesPercentage());

        // Update protein
        tvProteinProgress.setText(String.format("%.1f / %.1fg",
                nutrition.consumedProtein, nutrition.targetProtein));
        progressProtein.setProgress(nutrition.getProteinPercentage());

        // Update carbs
        tvCarbsProgress.setText(String.format("%.1f / %.1fg",
                nutrition.consumedCarbs, nutrition.targetCarbs));
        progressCarbs.setProgress(nutrition.getCarbsPercentage());

        // Update fat
        tvFatProgress.setText(String.format("%.1f / %.1fg",
                nutrition.consumedFat, nutrition.targetFat));
        progressFat.setProgress(nutrition.getFatPercentage());
    }

    private void updateMealItems(Map<MealTime, List<FoodLogItemWithDetails>> mealItemsMap) {
        if (mealItemsMap == null) return;

        // Update each meal
        updateMealSection(MealTime.BREAKFAST, mealItemsMap.get(MealTime.BREAKFAST),
                breakfastAdapter, tvBreakfastTotal);
        updateMealSection(MealTime.LUNCH, mealItemsMap.get(MealTime.LUNCH),
                lunchAdapter, tvLunchTotal);
        updateMealSection(MealTime.DINNER, mealItemsMap.get(MealTime.DINNER),
                dinnerAdapter, tvDinnerTotal);
        updateMealSection(MealTime.SNACK, mealItemsMap.get(MealTime.SNACK),
                snackAdapter, tvSnackTotal);
    }

    private void updateMealSection(MealTime mealTime, List<FoodLogItemWithDetails> items,
                                   FoodLogItemAdapter adapter, TextView totalTextView) {
        if (items == null) items = new ArrayList<>();

        adapter.updateItems(items);

        // Calculate total calories
        float totalCalories = 0;
        for (FoodLogItemWithDetails item : items) {
            totalCalories += item.getCalories();
        }

        // Update total display
        if (totalCalories > 0) {
            totalTextView.setText(String.format("Total: %.0f kcal", totalCalories));
            totalTextView.setVisibility(View.VISIBLE);
        } else {
            totalTextView.setVisibility(View.GONE);
        }
    }

    private void showAddFoodDialog(MealTime mealTime) {
        // TODO: Implement AddFoodDialog
        Log.d(TAG, "Show add food dialog for: " + mealTime.getDisplayName());
        Toast.makeText(getContext(), "Add food to " + mealTime.getDisplayName(), Toast.LENGTH_SHORT).show();
    }

    private void showEditFoodDialog(FoodLogItemWithDetails item) {
        // TODO: Implement edit functionality
        Log.d(TAG, "Edit food item: " + item.getIngredientName());
        Toast.makeText(getContext(), "Edit: " + item.getIngredientName(), Toast.LENGTH_SHORT).show();
    }

    private void showDeleteConfirmation(FoodLogItemWithDetails item) {
        new AlertDialog.Builder(getContext())
                .setTitle("Delete Food Item")
                .setMessage("Are you sure you want to delete " + item.getIngredientName() + "?")
                .setPositiveButton("Delete", (dialog, which) -> {
                    viewModel.deleteFoodItem(item);
                })
                .setNegativeButton("Cancel", null)
                .show();
    }
}