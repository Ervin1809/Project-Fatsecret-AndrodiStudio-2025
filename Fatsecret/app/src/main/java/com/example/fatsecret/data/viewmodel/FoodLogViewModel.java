package com.example.fatsecret.data.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.fatsecret.data.model.FoodLog;
import com.example.fatsecret.data.repository.FoodLogRepository;

import java.util.List;

public class FoodLogViewModel extends AndroidViewModel {
    private final FoodLogRepository repository;
    private final MutableLiveData<List<FoodLog>> foodLogs = new MutableLiveData<>();

    public FoodLogViewModel(@NonNull Application application) {
        super(application);
        repository = new FoodLogRepository(application);
        loadAllFoodLogs();
    }

    public LiveData<List<FoodLog>> getFoodLogs() {
        return foodLogs;
    }

    public void loadAllFoodLogs() {
        foodLogs.setValue(repository.getAll());
    }

    public FoodLog getById(int id) {
        return repository.getById(id);
    }

    public void insert(FoodLog data) {
        repository.insert(data);
        loadAllFoodLogs();
    }

    public void update(FoodLog data) {
        repository.update(data);
        loadAllFoodLogs();
    }

    public void delete(int id) {
        repository.delete(id);
        loadAllFoodLogs();
    }
}