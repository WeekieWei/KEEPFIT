package com.hoongyan.keepfit.MainActivity.ExerciseFragment.Controller;

public interface ExerciseFragmentFragmentControllerInterface {
    public void processNLP(String naturalLanguageText);

    public void addMealToFirebase(String mealName, double totalCal);

    public boolean isOnline();
}
