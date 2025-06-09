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

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fatsecret.R;
import com.example.fatsecret.data.viewmodel.IngredientViewModel;
import com.example.fatsecret.ui.adapters.IngredientAdapter;
import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;

public class SearchFragment extends Fragment {
    private EditText etSearch;
    private MaterialButton btnSearch, btnRetry;
    private ProgressBar progressBar;
    private RecyclerView rvSearchResults;
    private LinearLayout layoutEmptyState, layoutErrorState;
    private TextView tvErrorMessage;

    private IngredientViewModel ingredientViewModel;
    private IngredientAdapter searchAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search, container, false);

        initViews(view);
        setupViewModel();
        setupClickListeners();
        setupRecyclerView();
//        testClearDatabase(); // aktifkan jika ingin menguji pembersihan database

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
                // Update RecyclerView adapter with results
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
        // Search button
        btnSearch.setOnClickListener(v -> performSearch());

        // Retry button
        btnRetry.setOnClickListener(v -> performSearch());

        // Search on Enter key
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
        rvSearchResults.setAdapter(searchAdapter);
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

        // Clear error
        etSearch.setError(null);

        // Hide keyboard
        hideKeyboard();

        // Perform search
        ingredientViewModel.searchIngredients(query);
    }

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
    private void testClearDatabase() {
//        IngredientViewModel viewModel = new ViewModelProvider(this).get(IngredientViewModel.class);
        Log.d("SearchFragment", "🧪 Testing database clear...");

        // Option 1: Clear all
        ingredientViewModel.clearDatabase();

        // Option 2: Clear only zero protein data
        // viewModel.clearCorruptData();

        Toast.makeText(getContext(), "Database clearing... check logcat", Toast.LENGTH_SHORT).show();
    }
}