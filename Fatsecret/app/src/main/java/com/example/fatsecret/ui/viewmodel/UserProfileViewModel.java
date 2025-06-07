package com.example.fatsecret.ui.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.fatsecret.data.model.UserProfile;
import com.example.fatsecret.data.repository.UserProfileRepository;

import java.util.List;

public class UserProfileViewModel extends AndroidViewModel {
    private final UserProfileRepository repository;
    private final MutableLiveData<List<UserProfile>> userProfiles = new MutableLiveData<>();

    public UserProfileViewModel(@NonNull Application application) {
        super(application);
        repository = new UserProfileRepository(application);
        loadAllUserProfiles();
    }

    public LiveData<List<UserProfile>> getUserProfiles() {
        return userProfiles;
    }

    public void loadAllUserProfiles() {
        userProfiles.setValue(repository.getAll());
    }

    public UserProfile getById(int id) {
        return repository.getById(id);
    }

    public void insert(UserProfile data) {
        repository.insert(data);
        loadAllUserProfiles();
    }

    public void update(UserProfile data) {
        repository.update(data);
        loadAllUserProfiles();
    }

    public void delete(int id) {
        repository.delete(id);
        loadAllUserProfiles();
    }
}