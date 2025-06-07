package com.example.fatsecret.data.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.fatsecret.data.model.MenuIngredient;
import com.example.fatsecret.data.repository.MenuIngredientRepository;

import java.util.List;

public class MenuIngredientViewModel extends AndroidViewModel {
    private final MenuIngredientRepository repository;
    private final MutableLiveData<List<MenuIngredient>> menuIngredients = new MutableLiveData<>();

    public MenuIngredientViewModel(@NonNull Application application) {
        super(application);
        repository = new MenuIngredientRepository(application);
        loadAllMenuIngredients();
    }

    public LiveData<List<MenuIngredient>> getMenuIngredients() {
        return menuIngredients;
    }

    public void loadAllMenuIngredients() {
        menuIngredients.setValue(repository.getAll());
    }

    public MenuIngredient getById(int id) {
        return repository.getById(id);
    }

    public void insert(MenuIngredient data) {
        repository.insert(data);
        loadAllMenuIngredients();
    }

    public void update(MenuIngredient data) {
        repository.update(data);
        loadAllMenuIngredients();
    }

    public void delete(int id) {
        repository.delete(id);
        loadAllMenuIngredients();
    }
}