package com.hoongyan.keepfit.JavaClass;

import com.google.firebase.Timestamp;

public class FoodHistoryObject {
    private String mealName;
    private double totalCalorie;
    private Timestamp timestamp;

    public FoodHistoryObject(String mealName, double totalCalorie, Timestamp timestamp) {
        this.mealName = mealName;
        this.totalCalorie = totalCalorie;
        this.timestamp = timestamp;
    }

    public String getMealName() {
        return mealName;
    }

    public double getTotalCalorie() {
        return totalCalorie;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }
}
