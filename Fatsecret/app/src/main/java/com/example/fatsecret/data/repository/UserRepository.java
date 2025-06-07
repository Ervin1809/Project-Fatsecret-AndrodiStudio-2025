package com.example.fatsecret.data.repository;

import android.content.Context;
import com.example.fatsecret.data.helper.UserHelper;
import com.example.fatsecret.data.model.User;
import java.util.List;

public class UserRepository {
    private final UserHelper helper;

    public UserRepository(Context context) {
        helper = new UserHelper(context);
    }

    public long insertUser(User user) {
        return helper.insertUser(user);
    }

    public User getUserById(int id) {
        return helper.getUserById(id);
    }

    public List<User> getAllUsers() {
        return helper.getAllUsers();
    }

    public int updateUser(User user) {
        return helper.updateUser(user);
    }

    public int deleteUser(int id) {
        return helper.deleteUser(id);
    }
}