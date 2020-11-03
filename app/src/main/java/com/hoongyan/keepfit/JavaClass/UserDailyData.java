package com.hoongyan.keepfit.JavaClass;

import com.google.firebase.Timestamp;

public class UserDailyData {
    private int steps;
    private Timestamp timestamp;

    public UserDailyData() {
        steps = 0;
        timestamp = null;
    }

    public void setSteps(int steps) {
        this.steps = steps;
    }

    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }

    public int getSteps() {
        return steps;
    }
}
