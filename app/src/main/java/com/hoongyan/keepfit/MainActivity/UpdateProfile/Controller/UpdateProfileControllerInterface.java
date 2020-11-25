package com.hoongyan.keepfit.MainActivity.UpdateProfile.Controller;

import android.app.DatePickerDialog;

import com.google.android.material.textfield.TextInputLayout;
import com.hoongyan.keepfit.JavaClass.UserProfile;

public interface UpdateProfileControllerInterface {
    public DatePickerDialog.OnDateSetListener setDateChangeListener(final UpdateProfileController.DateChangeListener dateChangeListener);

    public boolean validateField(TextInputLayout container);

    public boolean validateWH(TextInputLayout weightContainer, TextInputLayout heightContainer);

    public void updateUserProfile(int gender, String firstName, String lastName, String dob, double weight,
                                  double height, int activityLevel);

    public UserProfile getCurrentData();
}
