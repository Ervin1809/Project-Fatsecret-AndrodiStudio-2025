package com.example.fatsecret.ui.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.fatsecret.data.model.Ingredient;
import com.example.fatsecret.data.repository.IngredientRepository;

import java.util.List;

public class IngredientViewModel extends AndroidViewModel {
    private final IngredientRepository repository;
    private final MutableLiveData<List<Ingredient>> ingredients = new MutableLiveData<>();

    public IngredientViewModel(@NonNull Application application) {
        super(application);
        repository = new IngredientRepository(application);
        loadAllIngredients();
    }

    public LiveData<List<Ingredient>> getIngredients() {
        return ingredients;
    }

    public void loadAllIngredients() {
        ingredients.setValue(repository.getAll());
    }

    public Ingredient getById(int id) {
        return repository.getById(id);
    }

    public void insert(Ingredient data) {
        repository.insert(data);
        loadAllIngredients();
    }

    public void update(Ingredient data) {
        repository.update(data);
        loadAllIngredients();
    }

    public void delete(int id) {
        repository.delete(id);
        loadAllIngredients();
    }
}