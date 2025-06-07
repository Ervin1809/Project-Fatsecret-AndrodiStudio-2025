package com.example.fatsecret.data.repository;

import android.content.Context;
import com.example.fatsecret.data.helper.UserProfileHelper;
import com.example.fatsecret.data.model.UserProfile;
import java.util.List;

public class UserProfileRepository {
    private final UserProfileHelper helper;

    public UserProfileRepository(Context context) {
        helper = new UserProfileHelper(context);
    }

    public long insert(UserProfile data) {
        return helper.insert(data);
    }

    public UserProfile getById(int id) {
        return helper.getById(id);
    }

    public List<UserProfile> getAll() {
        return helper.getAll();
    }

    public int update(UserProfile data) {
        return helper.update(data);
    }

    public int delete(int id) {
        return helper.delete(id);
    }
}