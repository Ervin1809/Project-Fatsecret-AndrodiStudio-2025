package com.example.fatsecret.data.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.fatsecret.data.model.FoodLogItem;
import com.example.fatsecret.data.repository.FoodLogItemRepository;

import java.util.List;

public class FoodLogItemViewModel extends AndroidViewModel {
    private final FoodLogItemRepository repository;
    private final MutableLiveData<List<FoodLogItem>> foodLogItems = new MutableLiveData<>();

    public FoodLogItemViewModel(@NonNull Application application) {
        super(application);
        repository = new FoodLogItemRepository(application);
        loadAllFoodLogItems();
    }

    public LiveData<List<FoodLogItem>> getFoodLogItems() {
        return foodLogItems;
    }

    public void loadAllFoodLogItems() {
        foodLogItems.setValue(repository.getAll());
    }

    public FoodLogItem getById(int id) {
        return repository.getById(id);
    }

    public void insert(FoodLogItem data) {
        repository.insert(data);
        loadAllFoodLogItems();
    }

    public void update(FoodLogItem data) {
        repository.update(data);
        loadAllFoodLogItems();
    }

    public void delete(int id) {
        repository.delete(id);
        loadAllFoodLogItems();
    }
}