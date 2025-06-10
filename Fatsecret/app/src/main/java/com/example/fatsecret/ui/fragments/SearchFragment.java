package com.example.fatsecret.ui.fragments;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
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
import com.example.fatsecret.data.managers.FoodLogManager;
import com.example.fatsecret.data.model.Ingredient;
import com.example.fatsecret.data.model.User;
import com.example.fatsecret.data.viewmodel.AuthViewModel;
import com.example.fatsecret.data.viewmodel.IngredientViewModel;
import com.example.fatsecret.ui.adapters.IngredientAdapter;
import com.example.fatsecret.ui.dialogs.WeightInputDialog;
import com.example.fatsecret.utils.MealTime;
import com.google.android.material.button.MaterialButton;

public class SearchFragment extends Fragment implements WeightInputDialog.OnWeightConfirmedListener{
    private static final String TAG = "SearchFragment";

    private EditText etSearch;
    private MaterialButton btnSearch, btnRetry;
    private ProgressBar progressBar;
    private RecyclerView rvSearchResults;
    private LinearLayout layoutEmptyState, layoutErrorState;
    private TextView tvErrorMessage;

    private IngredientViewModel ingredientViewModel;
    private IngredientAdapter searchAdapter;

    // ✅ Simple mode detection
    private boolean isAddingFoodMode = false;
    private String targetMealType = null;

    private AuthViewModel authViewModel;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        authViewModel = new ViewModelProvider(requireActivity()).get(AuthViewModel.class);

        // ✅ Check if we're in "adding food" mode
        if (getArguments() != null) {
            isAddingFoodMode = getArguments().getBoolean("is_adding_food", false);
            targetMealType = getArguments().getString("target_meal");

            if (isAddingFoodMode && targetMealType != null) {
                Log.d(TAG, "🍽️ Add Food Mode: " + targetMealType);
            } else {
                Log.d(TAG, "🔍 Browse Mode: Just searching ingredients");
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search, container, false);

        initViews(view);
        setupViewModel();
        setupClickListeners();
        setupRecyclerView();

        return view;
    }

    private void initViews(View view) {
        etSearch = view.findViewById(R.id.etSearch);
        btnSearch = view.findViewById(R.id.btnSearch);
        btnRetry = view.findViewById(R.id.btnRetry);
        progressBar = view.findViewById(R.id.progressBar);
        rvSearchResults = view.findViewById(R.id.rvSearchResults);
        layoutEmptyState = view.findViewById(R.id.layoutEmptyState);
        layoutErrorState = view.findViewById(R.id.layoutErrorState);
        tvErrorMessage = view.findViewById(R.id.tvErrorMessage);
    }

    private void setupViewModel() {
        ingredientViewModel = new ViewModelProvider(this).get(IngredientViewModel.class);

        // Observe search results
        ingredientViewModel.getSearchResults().observe(getViewLifecycleOwner(), ingredients -> {
            if (ingredients != null && !ingredients.isEmpty()) {
                showSearchResults();
                searchAdapter.setIngredients(ingredients);
            } else {
                showEmptyState();
            }
        });

        // Observe loading state
        ingredientViewModel.getIsLoading().observe(getViewLifecycleOwner(), isLoading -> {
            if (isLoading) {
                showLoading();
            } else {
                hideLoading();
            }
        });

        // Observe error messages
        ingredientViewModel.getErrorMessage().observe(getViewLifecycleOwner(), error -> {
            if (error != null && !error.isEmpty()) {
                showError(error);
            }
        });
    }

    private void setupClickListeners() {
        btnSearch.setOnClickListener(v -> performSearch());
        btnRetry.setOnClickListener(v -> performSearch());

        etSearch.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                performSearch();
                return true;
            }
            return false;
        });
    }

    private void setupRecyclerView() {
        rvSearchResults.setLayoutManager(new LinearLayoutManager(getContext()));
        searchAdapter = new IngredientAdapter();

        // ✅ Set mode ke adapter
        searchAdapter.setMode(isAddingFoodMode, targetMealType);

        // ✅ Set click listener
        searchAdapter.setOnIngredientClickListener(new IngredientAdapter.OnIngredientClickListener() {
            @Override
            public void onIngredientClick(Ingredient ingredient) {
                // Handle item click (show details)
                Log.d(TAG, "🔍 Ingredient clicked: " + ingredient.getName());
                Toast.makeText(getContext(),
                        "Details: " + ingredient.getName() + " - " + ingredient.getCaloriesPer100g() + " kcal",
                        Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onAddFoodClick(Ingredient ingredient) {
                // ✅ Call method yang ada di SearchFragment
                onAddFoodClicked(ingredient);
            }
        });

        rvSearchResults.setAdapter(searchAdapter);
    }

    // ✅ Method ini di level SearchFragment, bukan di dalam anonymous class
    private void onAddFoodClicked(Ingredient ingredient) {
        Log.d(TAG, "🍽️ Add food clicked: " + ingredient.getName() + " to " + targetMealType);

        // Show weight input dialog
        showWeightInputDialog(ingredient);
    }

    // ✅ Method ini juga di level SearchFragment
    private void showWeightInputDialog(Ingredient ingredient) {
        try {
            MealTime mealTime = MealTime.fromValue(targetMealType);
            WeightInputDialog dialog = WeightInputDialog.newInstance(ingredient, mealTime);
            dialog.show(getChildFragmentManager(), "WeightInputDialog");

            Log.d(TAG, "✅ Weight dialog shown for: " + ingredient.getName());
        } catch (Exception e) {
            Log.e(TAG, "❌ Error showing weight dialog: " + e.getMessage());
            Toast.makeText(getContext(), "Error showing dialog", Toast.LENGTH_SHORT).show();
        }
    }

    private void performSearch() {
        String query = etSearch.getText().toString().trim();

        if (TextUtils.isEmpty(query)) {
            etSearch.setError("Please enter food name");
            etSearch.requestFocus();
            return;
        }

        if (query.length() < 2) {
            etSearch.setError("Please enter at least 2 characters");
            etSearch.requestFocus();
            return;
        }

        etSearch.setError(null);
        hideKeyboard();

        // ✅ Log mode info
        Log.d(TAG, "🔍 Searching: " + query + (isAddingFoodMode ? " (Add Food Mode for " + targetMealType + ")" : " (Browse Mode)"));

        ingredientViewModel.searchIngredients(query);
    }

    // Rest of your existing methods (unchanged)
    private void showLoading() {
        progressBar.setVisibility(View.VISIBLE);
        rvSearchResults.setVisibility(View.GONE);
        layoutEmptyState.setVisibility(View.GONE);
        layoutErrorState.setVisibility(View.GONE);
        btnSearch.setEnabled(false);
        btnSearch.setText("Searching...");
    }

    private void hideLoading() {
        progressBar.setVisibility(View.GONE);
        btnSearch.setEnabled(true);
        btnSearch.setText("Search");
    }

    private void showSearchResults() {
        rvSearchResults.setVisibility(View.VISIBLE);
        layoutEmptyState.setVisibility(View.GONE);
        layoutErrorState.setVisibility(View.GONE);
    }

    private void showEmptyState() {
        rvSearchResults.setVisibility(View.GONE);
        layoutEmptyState.setVisibility(View.VISIBLE);
        layoutErrorState.setVisibility(View.GONE);
    }

    private void showError(String errorMessage) {
        rvSearchResults.setVisibility(View.GONE);
        layoutEmptyState.setVisibility(View.GONE);
        layoutErrorState.setVisibility(View.VISIBLE);
        tvErrorMessage.setText(errorMessage);
    }

    private void hideKeyboard() {
        if (getActivity() != null) {
            android.view.inputmethod.InputMethodManager imm =
                    (android.view.inputmethod.InputMethodManager) getActivity().getSystemService(android.content.Context.INPUT_METHOD_SERVICE);
            if (imm != null && etSearch != null) {
                imm.hideSoftInputFromWindow(etSearch.getWindowToken(), 0);
            }
        }
    }

    @Override
    public void onWeightConfirmed(Ingredient ingredient, MealTime mealType, double weightInGrams) {
        Log.d(TAG, "✅ Weight confirmed: " + ingredient.getName() +
                " (" + weightInGrams + "g) to " + mealType.getDisplayName());

        // TODO: Save to database (next step)
        saveFoodToMeal(ingredient, mealType, weightInGrams);
    }

    private void saveFoodToMeal(Ingredient ingredient, MealTime mealType, double weightInGrams) {
        Log.d(TAG, "💾 Saving food to meal: " + ingredient.getName() +
                " (" + weightInGrams + "g) to " + mealType.getDisplayName());

        // Get current user ID from AuthViewModel
        User currentUser = authViewModel.getCurrentUser().getValue();

        if (currentUser == null) {
            Toast.makeText(getContext(),
                    "❌ Error: User not logged in",
                    Toast.LENGTH_SHORT).show();
            Log.e(TAG, "❌ No current user found");
            return;
        }

        int currentUserId = currentUser.getId();
        String username = currentUser.getEmail();

        Log.d(TAG, "👤 Current user: " + username + " (ID: " + currentUserId + ")");

        // Use FoodLogManager to save
        FoodLogManager foodLogManager = new FoodLogManager(getContext());

        // Run in background thread
        new Thread(() -> {
            boolean success = foodLogManager.addFoodToMeal(ingredient, mealType, weightInGrams, currentUserId);

            // Update UI on main thread
            if (getActivity() != null) {
                getActivity().runOnUiThread(() -> {
                    if (success) {
                        // ✅ Enhanced success message
                        Toast.makeText(getContext(),
                                "✅ " + ingredient.getName() + " (" + weightInGrams + "g) added to " + mealType.getDisplayName(),
                                Toast.LENGTH_SHORT).show();

                        // Navigate back to home
                        getActivity().onBackPressed();
                    } else {
                        Toast.makeText(getContext(),
                                "❌ Failed to add food. Please try again.",
                                Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }).start();
    }
}