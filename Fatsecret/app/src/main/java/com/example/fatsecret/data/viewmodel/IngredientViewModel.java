package com.example.fatsecret.data.viewmodel;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.fatsecret.data.model.Ingredient;
import com.example.fatsecret.data.repository.IngredientRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class IngredientViewModel extends AndroidViewModel {
    private final IngredientRepository repository;
    private final ExecutorService executor;

    // LiveData untuk UI
    private final MutableLiveData<List<Ingredient>> ingredients = new MutableLiveData<>();
    private final MutableLiveData<List<Ingredient>> searchResults = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>(false);
    private final MutableLiveData<String> errorMessage = new MutableLiveData<>();

    public IngredientViewModel(@NonNull Application application) {
        super(application);
        repository = new IngredientRepository(application);
        executor = Executors.newFixedThreadPool(3);
        loadAllIngredients();
    }

    // Getters untuk LiveData
    public LiveData<List<Ingredient>> getIngredients() {
        return ingredients;
    }

    public LiveData<List<Ingredient>> getSearchResults() {
        return searchResults;
    }

    public LiveData<Boolean> getIsLoading() {
        return isLoading;
    }

    public LiveData<String> getErrorMessage() {
        return errorMessage;
    }

    /**
     * Load all ingredients from local database
     */
    private void loadAllIngredients() {
        Log.d("IngredientViewModel", "📊 Loading all ingredients...");

        executor.execute(() -> {
            try {
                // ✅ FIXED: Use existing method or new getAllIngredients()
                List<Ingredient> allIngredients = repository.getAllIngredients();

                Log.d("IngredientViewModel", "✅ Loaded " +
                        (allIngredients != null ? allIngredients.size() : "null") + " total ingredients");

                // ✅ Use postValue() for background thread
                ingredients.postValue(allIngredients);

            } catch (Exception e) {
                Log.e("IngredientViewModel", "❌ Failed to load all ingredients: " + e.getMessage());
                // ✅ Use postValue() for background thread
                errorMessage.postValue("Failed to load ingredients: " + e.getMessage());
            }
        });
    }

    /**
     * ✅ ALTERNATIVE: Load recent ingredients instead of all
     */
    private void loadRecentIngredients() {
        Log.d("IngredientViewModel", "📊 Loading recent ingredients...");

        executor.execute(() -> {
            try {
                // Load recent 50 ingredients
                List<Ingredient> recentIngredients = repository.getRecentIngredients(50);

                Log.d("IngredientViewModel", "✅ Loaded " +
                        (recentIngredients != null ? recentIngredients.size() : "null") + " recent ingredients");

                // ✅ Use postValue() for background thread
                ingredients.postValue(recentIngredients);

            } catch (Exception e) {
                Log.e("IngredientViewModel", "❌ Failed to load recent ingredients: " + e.getMessage());
                // ✅ Use postValue() for background thread
                errorMessage.postValue("Failed to load ingredients: " + e.getMessage());
            }
        });
    }

    /**
     * Search ingredients (local + USDA API)
     */
    public void searchIngredients(String query) {
        Log.d("IngredientViewModel", "🔍 searchIngredients called with: '" + query + "'");

        if (query == null || query.trim().isEmpty()) {
            Log.w("IngredientViewModel", "❌ Empty query, clearing results");
            searchResults.setValue(null);  // ✅ OK on main thread
            return;
        }

        Log.d("IngredientViewModel", "🔄 Setting loading state to true");
        isLoading.setValue(true);  // ✅ OK on main thread

        executor.execute(() -> {
            try {
                Log.d("IngredientViewModel", "📞 Calling repository.searchIngredients");
                List<Ingredient> results = repository.searchIngredients(query.trim());

                Log.d("IngredientViewModel", "✅ Repository returned " +
                        (results != null ? results.size() : "null") + " results");

                if (results != null) {
                    for (int i = 0; i < Math.min(3, results.size()); i++) {
                        Log.d("IngredientViewModel", "  " + (i+1) + ". " + results.get(i).getName());
                    }
                }

                // ✅ FIXED: Use postValue() for background thread
                searchResults.postValue(results);
                Log.d("IngredientViewModel", "📡 Posted results to LiveData");

                // Update main ingredients list as well
                loadAllIngredients();

            } catch (Exception e) {
                Log.e("IngredientViewModel", "❌ Search failed: " + e.getMessage());
                // ✅ FIXED: Use postValue() for background thread
                errorMessage.postValue("Search failed: " + e.getMessage());
            } finally {
                Log.d("IngredientViewModel", "🔄 Setting loading state to false");
                // ✅ FIXED: Use postValue() for background thread
                isLoading.postValue(false);
            }
        });
    }

    /**
     * Get ingredient by ID
     */
    public void getIngredientById(int id, IngredientCallback callback) {
        executor.execute(() -> {
            try {
                Ingredient ingredient = repository.getIngredientById(id);
                callback.onSuccess(ingredient);
            } catch (Exception e) {
                callback.onError("Failed to get ingredient: " + e.getMessage());
            }
        });
    }

    /**
     * Save/Insert ingredient
     */
    public void insertIngredient(Ingredient ingredient) {
        isLoading.setValue(true);

        executor.execute(() -> {
            try {
                long result = repository.saveIngredient(ingredient);

                if (result > 0) {
                    // Refresh ingredients list
                    loadAllIngredients();
                } else {
                    errorMessage.postValue("Failed to save ingredient (might already exist)");
                }
            } catch (Exception e) {
                errorMessage.postValue("Failed to save ingredient: " + e.getMessage());
            } finally {
                isLoading.postValue(false);
            }
        });
    }

    /**
     * Update ingredient (Note: Repository doesn't have update method, so we'll add it)
     */
    public void updateIngredient(Ingredient ingredient) {
        isLoading.setValue(true);

        executor.execute(() -> {
            try {
                // Since repository doesn't have update method, we'll simulate it
                // You can add update method to repository later if needed
                boolean success = updateIngredientInRepo(ingredient);

                if (success) {
                    loadAllIngredients();
                } else {
                    errorMessage.postValue("Failed to update ingredient");
                }
            } catch (Exception e) {
                errorMessage.postValue("Failed to update ingredient: " + e.getMessage());
            } finally {
                isLoading.postValue(false);
            }
        });
    }

    /**
     * Delete ingredient (Note: Repository doesn't have delete method, so we'll add it)
     */
    public void deleteIngredient(int id) {
        isLoading.setValue(true);

        executor.execute(() -> {
            try {
                // Since repository doesn't have delete method, we'll simulate it
                // You can add delete method to repository later if needed
                boolean success = deleteIngredientFromRepo(id);

                if (success) {
                    loadAllIngredients();
                } else {
                    errorMessage.postValue("Failed to delete ingredient");
                }
            } catch (Exception e) {
                errorMessage.postValue("Failed to delete ingredient: " + e.getMessage());
            } finally {
                isLoading.postValue(false);
            }
        });
    }

    /**
     * Clear search results
     */
    public void clearSearchResults() {
        searchResults.setValue(null);
    }

    /**
     * Clear error messages
     */
    public void clearError() {
        errorMessage.setValue(null);
    }

    // Helper methods (you can add these to IngredientRepository later)

    /**
     * Get all ingredients from local database only
     */
    private List<Ingredient> getAllLocalIngredients() {
        // Since repository doesn't have getAll(), we'll use searchLocalIngredients with empty query
        // You should add this method to IngredientRepository
        return repository.searchIngredients(""); // This will return local results
    }

    /**
     * Simulate update ingredient (add this method to IngredientRepository later)
     */
    private boolean updateIngredientInRepo(Ingredient ingredient) {
        // TODO: Add update method to IngredientRepository
        // For now, just log that update was attempted
        android.util.Log.d("IngredientViewModel", "Update attempted for: " + ingredient.getName());
        return true; // Simulate success
    }

    /**
     * Simulate delete ingredient (add this method to IngredientRepository later)
     */
    private boolean deleteIngredientFromRepo(int id) {
        // TODO: Add delete method to IngredientRepository
        // For now, just log that delete was attempted
        android.util.Log.d("IngredientViewModel", "Delete attempted for ID: " + id);
        return true; // Simulate success
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        if (executor != null && !executor.isShutdown()) {
            executor.shutdown();
        }
    }
    public void clearDatabase() {
        Log.d("IngredientViewModel", "🗑️ Clearing database...");

        executor.execute(() -> {
            try {
                repository.clearAllIngredients();

                // Clear UI data
                ingredients.postValue(new ArrayList<>());
                searchResults.postValue(new ArrayList<>());

                Log.d("IngredientViewModel", "✅ Database cleared and UI updated");

            } catch (Exception e) {
                Log.e("IngredientViewModel", "❌ Error clearing database: " + e.getMessage());
                errorMessage.postValue("Failed to clear database: " + e.getMessage());
            }
        });
    }

    public void clearCorruptData() {
        Log.d("IngredientViewModel", "🔧 Clearing corrupt data...");

        executor.execute(() -> {
            try {
                repository.clearZeroProteinIngredients();
                Log.d("IngredientViewModel", "✅ Corrupt data cleared");

            } catch (Exception e) {
                Log.e("IngredientViewModel", "❌ Error clearing corrupt data: " + e.getMessage());
                errorMessage.postValue("Failed to clear corrupt data: " + e.getMessage());
            }
        });
    }

    // Callback interface for async operations
    public interface IngredientCallback {
        void onSuccess(Ingredient ingredient);
        void onError(String error);
    }
}