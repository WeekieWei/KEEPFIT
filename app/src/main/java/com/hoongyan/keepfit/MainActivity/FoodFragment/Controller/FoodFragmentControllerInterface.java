package com.hoongyan.keepfit.MainActivity.FoodFragment.Controller;


public interface FoodFragmentControllerInterface {

    public void processNLP(String naturalLanguageText);

    public void addMealToFirebase(String mealName, double totalCal);

    public boolean isOnline();
}
