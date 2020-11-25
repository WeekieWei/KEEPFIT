package com.hoongyan.keepfit.MainActivity.FoodFragment.Controller;



public interface FoodFragmentControllerInterface {

    public void processNLP(String naturalLanguageText);

    public void addMealToFirebase(String mealName, double totalCal, int slot);

    public boolean isOnline();

    public void requestUpdateCalSlots();
}
