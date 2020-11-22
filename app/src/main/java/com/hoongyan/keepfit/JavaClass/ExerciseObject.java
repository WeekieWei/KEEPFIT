package com.hoongyan.keepfit.JavaClass;

public class ExerciseObject {
    private String exerciseName;
    private double exerciseMET;
//    private double duration;


    public ExerciseObject(String exerciseName, double exerciseMET) {
        this.exerciseName = exerciseName;
        this.exerciseMET = exerciseMET;
    }

    public String getExerciseName() {
        return exerciseName;
    }

    public double getExerciseMET() {
        return exerciseMET;
    }
}
