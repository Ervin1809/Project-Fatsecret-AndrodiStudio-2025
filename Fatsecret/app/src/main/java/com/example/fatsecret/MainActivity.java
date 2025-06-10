package com.example.fatsecret;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;

import com.example.fatsecret.data.viewmodel.AuthViewModel;
import com.example.fatsecret.ui.LoginActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {
    private Toolbar toolbar;
    private BottomNavigationView bottomNavigation;
    private AuthViewModel authViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        authViewModel = new ViewModelProvider(this).get(AuthViewModel.class);

        // Check if user is logged in
        if (!authViewModel.isLoggedIn()) {
            navigateToLogin();
            return;
        }

        initViews();
        setupToolbar();
        setupBottomNavigation();

        // ✅ REMOVED: No need to load default fragment
        // Navigation Component handles this automatically via app:startDestination
    }

    private void initViews() {
        toolbar = findViewById(R.id.toolbar);
        bottomNavigation = findViewById(R.id.bottom_navigation);
    }

    private void setupToolbar() {
        setSupportActionBar(toolbar);
    }

    private void setupBottomNavigation() {
        // Get NavController
        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager()
                .findFragmentById(R.id.nav_host_fragment);

        if (navHostFragment != null) {
            NavController navController = navHostFragment.getNavController();

            // Setup BottomNavigation with NavController
            NavigationUI.setupWithNavController(bottomNavigation, navController);

            // Update toolbar title and bottom nav visibility when destination changes
            navController.addOnDestinationChangedListener((controller, destination, arguments) -> {

                // ✅ Check if we're in add food mode
                boolean isAddFoodMode = arguments != null && arguments.getBoolean("is_adding_food", false);

                if (destination.getId() == R.id.homeFragment) {
                    toolbar.setTitle("Home");
                    showBottomNavigation(); // Always show for home

                } else if (destination.getId() == R.id.searchFragment) {
                    if (isAddFoodMode) {
                        // ✅ Add Food Mode - Hide bottom nav, custom title
                        String mealType = arguments.getString("target_meal", "");
                        toolbar.setTitle("Add Food to " + mealType.substring(0, 1).toUpperCase() + mealType.substring(1));
                        hideBottomNavigation();
                    } else {
                        // ✅ Normal Search Mode - Show bottom nav
                        toolbar.setTitle("Search Food");
                        showBottomNavigation();
                    }

                } else if (destination.getId() == R.id.profileFragment) {
                    toolbar.setTitle("Profile");
                    showBottomNavigation(); // Always show for profile
                }
            });
        }
    }

    // ✅ REMOVED: loadFragment method not needed with Navigation Component
    // Navigation Component handles fragment transactions automatically

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_logout) {
            logout();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void logout() {
        authViewModel.logout();
        navigateToLogin();
    }

    private void navigateToLogin() {
        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    // ✅ ADD: Methods to control bottom navigation visibility
    public void hideBottomNavigation() {
        if (bottomNavigation != null) {
            bottomNavigation.setVisibility(View.GONE);
        }
    }

    public void showBottomNavigation() {
        if (bottomNavigation != null) {
            bottomNavigation.setVisibility(View.VISIBLE);
        }
    }

    // ✅ ADD: Check if we're in add food mode
    public boolean isInAddFoodMode() {
        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager()
                .findFragmentById(R.id.nav_host_fragment);

        if (navHostFragment != null) {
            NavController navController = navHostFragment.getNavController();
            Bundle arguments = navController.getCurrentDestination() != null ?
                    navController.getCurrentBackStackEntry().getArguments() : null;

            return arguments != null && arguments.getBoolean("is_adding_food", false);
        }
        return false;
    }
}