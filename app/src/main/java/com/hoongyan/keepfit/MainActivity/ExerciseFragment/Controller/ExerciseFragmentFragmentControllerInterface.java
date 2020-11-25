package com.hoongyan.keepfit.MainActivity.ExerciseFragment.Controller;

import androidx.recyclerview.widget.RecyclerView;

public interface ExerciseFragmentFragmentControllerInterface {
    public void processNLP(String naturalLanguageText);

    public void addMealToFirebase(String mealName, double totalCal);

    public boolean isOnline();

    public RecyclerView.Adapter getRecycleViewAdapter();

    public double getCalVal(double met);
}
