package com.hoongyan.keepfit.JavaClass;

public class UserProfile {
    private String firstName, lastName, gender, dob;
    private double weight, height, fat, calRequired, bmi;
    private int activityLevel, weightAdjust;

    public UserProfile(){};

    public UserProfile(String firstName, String lastName, String gender, String dob, double weight, double height, double fat, double calRequired, double bmi, int weightAdjust, int activityLevel) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.gender = gender;
        this.dob = dob;
        this.weight = weight;
        this.height = height;
        this.fat = fat;
        this.calRequired = calRequired;
        this.bmi = bmi;
        this.weightAdjust = weightAdjust;
        this.activityLevel = activityLevel;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getGender() {
        return gender;
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

    public double getCalRequired() {
        return calRequired;
    }

    public double getBmi() {
        return bmi;
    }

    public int getWeightAdjust() {
        return weightAdjust;
    }

    public int getActivityLevel() {
        return activityLevel;
    }
}
