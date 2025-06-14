package com.example.fatsecret.data.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.fatsecret.data.model.Menu;
import com.example.fatsecret.data.repository.MenuRepository;

import java.util.List;

public class MenuViewModel extends AndroidViewModel {
    private final MenuRepository repository;
    private final MutableLiveData<List<Menu>> menus = new MutableLiveData<>();

    public MenuViewModel(@NonNull Application application) {
        super(application);
        repository = new MenuRepository(application);
        loadAllMenus();
    }

    public LiveData<List<Menu>> getMenus() {
        return menus;
    }

    public void loadAllMenus() {
        menus.setValue(repository.getAllMenus());
    }

    public Menu getById(int id) {
        return repository.getMenuById(id);
    }

    public void insert(Menu data) {
        repository.createMenu(data);
        loadAllMenus();
    }

    public void update(Menu data) {
        repository.updateMenu(data);
        loadAllMenus();
    }

    public void delete(int id) {
        repository.deleteMenu(id);
        loadAllMenus();
    }
}