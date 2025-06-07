package com.example.fatsecret.data.model;

public class UserProfile {
    int id, userId; // foreign key ke tabel users
    float height, weight, targetWeight; // tinggi dalam cm, berat dalam kg
    String createdAt, updatedAt; // format datetime

    public UserProfile(int id, int userId, float height, float weight, float targetWeight, String createdAt, String updatedAt) {
        this.id = id;
        this.userId = userId;
        this.height = height;
        this.weight = weight;
        this.targetWeight = targetWeight;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }
    public UserProfile() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public float getHeight() {
        return height;
    }

    public void setHeight(float height) {
        this.height = height;
    }

    public float getWeight() {
        return weight;
    }

    public void setWeight(float weight) {
        this.weight = weight;
    }

    public float getTargetWeight() {
        return targetWeight;
    }

    public void setTargetWeight(float targetWeight) {
        this.targetWeight = targetWeight;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }
}
