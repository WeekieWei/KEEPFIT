package com.hoongyan.keepfit.JavaClass;

public class UserProfile {
    private String firstName, lastName, dob;
    private double weight, height, fat;
    private int activityLevel;

    public UserProfile(String firstName, String lastName, String dob, double weight, double height, double fat, int activityLevel) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.dob = dob;
        this.weight = weight;
        this.height = height;
        this.fat = fat;
        this.activityLevel = activityLevel;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getDob() {
        return dob;
    }

    public double getWeight() {
        return weight;
    }

    public double getHeight() {
        return height;
    }

    public double getFat() {
        return fat;
    }

    public int getActivityLevel() {
        return activityLevel;
    }
}
