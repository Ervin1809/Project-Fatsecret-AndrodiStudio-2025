package com.example.fatsecret.data.model;

public class FoodLog {
    int id, userProfileId; // foreign key ke user_profiles.id
    String mealTime, note, createdAt, updatedAt; // waktu makan (format datetime), catatan

    public FoodLog(int id, int userProfileId, String mealTime, String note, String createdAt, String updatedAt) {
        this.id = id;
        this.userProfileId = userProfileId;
        this.mealTime = mealTime;
        this.note = note;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public FoodLog() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUserProfileId() {
        return userProfileId;
    }

    public void setUserProfileId(int userProfileId) {
        this.userProfileId = userProfileId;
    }

    public String getMealTime() {
        return mealTime;
    }

    public void setMealTime(String mealTime) {
        this.mealTime = mealTime;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
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
    }// waktu makan (format datetime)

}
