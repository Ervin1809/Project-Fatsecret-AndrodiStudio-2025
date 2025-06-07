package com.example.fatsecret.data.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.fatsecret.data.model.User;
import com.example.fatsecret.data.repository.UserRepository;

import java.util.List;

public class UserViewModel extends AndroidViewModel {
    private final UserRepository repository;
    private final MutableLiveData<List<User>> users = new MutableLiveData<>();

    public UserViewModel(@NonNull Application application) {
        super(application);
        repository = new UserRepository(application);
        loadAllUsers();
    }

    public LiveData<List<User>> getUsers() {
        return users;
    }

    public void loadAllUsers() {
        users.setValue(repository.getAllUsers());
    }

    public User getUserById(int id) {
        return repository.getUserById(id);
    }

    public void insertUser(User user) {
        repository.insertUser(user);
        loadAllUsers();
    }

    public void updateUser(User user) {
        repository.updateUser(user);
        loadAllUsers();
    }

    public void deleteUser(int id) {
        repository.deleteUser(id);
        loadAllUsers();
    }
}