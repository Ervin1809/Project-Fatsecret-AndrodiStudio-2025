package com.example.fatsecret.ui.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fatsecret.R;
import com.example.fatsecret.data.viewmodel.AuthViewModel;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.card.MaterialCardView;

public class HomeFragment extends Fragment {
    private TextView tvUserName;
    private MaterialCardView cardSearchFood, cardProfile;
    private RecyclerView rvRecentFoods;
    private LinearLayout layoutEmptyState;

    private AuthViewModel authViewModel;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        initViews(view);
        setupViewModel();
        setupClickListeners();
        loadUserData();
        setupRecentFoods();

        return view;
    }

    private void initViews(View view) {
        tvUserName = view.findViewById(R.id.tvUserName);
        cardSearchFood = view.findViewById(R.id.cardSearchFood);
        cardProfile = view.findViewById(R.id.cardProfile);
        rvRecentFoods = view.findViewById(R.id.rvRecentFoods);
        layoutEmptyState = view.findViewById(R.id.layoutEmptyState);
    }

    private void setupViewModel() {
        authViewModel = new ViewModelProvider(requireActivity()).get(AuthViewModel.class);
    }

    private void setupClickListeners() {
        // Search Food card
        cardSearchFood.setOnClickListener(v -> {
            // Navigate to Search fragment
            BottomNavigationView bottomNav = requireActivity().findViewById(R.id.bottom_navigation);
            bottomNav.setSelectedItemId(R.id.nav_search);
        });

        // Profile card
        cardProfile.setOnClickListener(v -> {
            // Navigate to Profile fragment
            BottomNavigationView bottomNav = requireActivity().findViewById(R.id.bottom_navigation);
            bottomNav.setSelectedItemId(R.id.nav_profile);
        });
    }

    private void loadUserData() {
        authViewModel.getCurrentUser().observe(getViewLifecycleOwner(), user -> {
            if (user != null) {
                tvUserName.setText(user.getName());
            } else {
                tvUserName.setText("User");
            }
        });
    }

    private void setupRecentFoods() {
        // Setup RecyclerView
        rvRecentFoods.setLayoutManager(new LinearLayoutManager(getContext()));

        // TODO: Implement RecentFoodsAdapter when we have food search functionality
        // For now, show empty state
        showEmptyState();
    }

    private void showEmptyState() {
        rvRecentFoods.setVisibility(View.GONE);
        layoutEmptyState.setVisibility(View.VISIBLE);
    }

    private void showRecentFoods() {
        rvRecentFoods.setVisibility(View.VISIBLE);
        layoutEmptyState.setVisibility(View.GONE);
    }
}