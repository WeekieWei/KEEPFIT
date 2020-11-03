package com.hoongyan.keepfit.UserProfileActivity.Controller;

import android.app.DatePickerDialog;

import com.google.android.material.button.MaterialButtonToggleGroup;
import com.google.android.material.textfield.TextInputLayout;

public interface UserProfileActivityControllerInterface {

    public void buttonGroupClickHandler(int genderCheckedId, int fatPercentageCheckedId);
    public DatePickerDialog.OnDateSetListener setDateChangeListener(final UserProfileActivityController.DateChangeListener dateChangeListener);
    public boolean validateField(TextInputLayout container);

    public void registerUserProfile(String firstName, String lastName, String dob, double weight,
                                    double height, double fat, double waist, double neck, double hip,
                                    int activityLevel);
}
