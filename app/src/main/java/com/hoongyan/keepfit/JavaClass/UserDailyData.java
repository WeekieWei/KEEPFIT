package com.hoongyan.keepfit.JavaClass;

import com.google.firebase.Timestamp;

public class UserDailyData {
    private int steps;
    private float netCal;
    private double weight;
    private Timestamp timestamp;

    public UserDailyData() {
        steps = 0;
        netCal = 0;
        weight = 0;
        timestamp = null;
    }

    public void setSteps(int steps) {
        this.steps = steps;
    }

    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }

    public float getNetCal() {
        return netCal;
    }

    public void setNetCal(float netCal) {
        this.netCal = netCal;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }

    public double getWeight() {
        return weight;
    }

    public void setWeight(double weight) {
        this.weight = weight;
    }

    public int getSteps() {
        return steps;
    }
}
