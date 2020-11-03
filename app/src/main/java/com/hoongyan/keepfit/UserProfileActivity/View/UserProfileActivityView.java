package com.hoongyan.keepfit.UserProfileActivity.View;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.button.MaterialButtonToggleGroup;
import com.google.android.material.textfield.TextInputLayout;
import com.hoongyan.keepfit.MainActivity.MainActivity;
import com.hoongyan.keepfit.R;
import com.hoongyan.keepfit.UserProfileActivity.Controller.UserProfileActivityController;
import com.hoongyan.keepfit.UserProfileActivity.UserProfileActivity;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class UserProfileActivityView implements UserProfileActivityViewInterface {

    //MVC Components
    private UserProfileActivityController userProfileActivityController;

    //Data Field
    private Activity userProfileActivity;
    private View rootView;
    private String[] activityLevelOptions;
    private Map<String, Integer> activityLevelMap;
    private ArrayAdapter<String> activityLevelOptionsAdapter;

    //Views
    private TextInputLayout firstNameContainer, lastNameContainer, dobContainer, weightContainer,
            heightContainer, fatPercentageContainer, waistContainer, neckContainer, hipContainer,
            activityLevelContainer;

    private AutoCompleteTextView activityLevelSelection;

    private MaterialButtonToggleGroup genderButtonGroup, fatPercentageButtonGroup;

    private MaterialButton createProfileButton;



    public UserProfileActivityView(Activity activity, ViewGroup viewGroup) {
        this.userProfileActivity = activity;
        rootView = LayoutInflater.from(userProfileActivity).inflate(R.layout.activity_user_profile, viewGroup);
        activityLevelOptions = new String[] {
                "Bed Ridden Individual",
                "Sedentary Occupation",
                "Mostly Sedentary Occupation & Some Movement",
                "Active Occupation with Prolonged Standing",
                "Performing Strenuous Labor Work"};
        activityLevelMap = new HashMap<>();
        activityLevelMap.put("Bed Ridden Individual", 1);
        activityLevelMap.put("Sedentary Occupation", 2);
        activityLevelMap.put("Mostly Sedentary Occupation & Some Movement", 3);
        activityLevelMap.put("Active Occupation with Prolonged Standing", 4);
        activityLevelMap.put("Performing Strenuous Labor Work", 5);
        activityLevelOptionsAdapter = new ArrayAdapter<>(rootView.getContext(), R.layout.support_simple_spinner_dropdown_item, activityLevelOptions);
        userProfileActivityController = new UserProfileActivityController(this, activity);

    }

    @Override
    public void initializeViews() {
        firstNameContainer = rootView.findViewById(R.id.firstNameContainer);
        lastNameContainer = rootView.findViewById(R.id.lastNameContainer);
        dobContainer = rootView.findViewById(R.id.dobContainer);
        weightContainer = rootView.findViewById(R.id.weightContainer);
        heightContainer = rootView.findViewById(R.id.heightContainer);
        fatPercentageContainer = rootView.findViewById(R.id.fatPercentageContainer);
        waistContainer = rootView.findViewById(R.id.waistContainer);
        neckContainer = rootView.findViewById(R.id.neckContainer);
        hipContainer = rootView.findViewById(R.id.hipContainer);
        activityLevelContainer = rootView.findViewById(R.id.activityLevelContainer);
        activityLevelSelection = rootView.findViewById(R.id.activityLevelSelection);
        activityLevelSelection.setAdapter(activityLevelOptionsAdapter);

        genderButtonGroup = rootView.findViewById(R.id.genderButtonGroup);
        fatPercentageButtonGroup = rootView.findViewById(R.id.fatPercentageButtonGroup);

        createProfileButton = rootView.findViewById(R.id.createProfileButton);

        waistContainer.setVisibility(View.GONE);
        neckContainer.setVisibility(View.GONE);
        hipContainer.setVisibility(View.GONE);
    }

    @Override
    public void attachListenersToButtonGroups() {

        genderButtonGroup.addOnButtonCheckedListener(new MaterialButtonToggleGroup.OnButtonCheckedListener() {
            @Override
            public void onButtonChecked(MaterialButtonToggleGroup group, int checkedId, boolean isChecked) {
                userProfileActivityController.buttonGroupClickHandler(genderButtonGroup.getCheckedButtonId(), fatPercentageButtonGroup.getCheckedButtonId());
            }
        });

        fatPercentageButtonGroup.addOnButtonCheckedListener(new MaterialButtonToggleGroup.OnButtonCheckedListener() {
            @Override
            public void onButtonChecked(MaterialButtonToggleGroup group, int checkedId, boolean isChecked) {
                userProfileActivityController.buttonGroupClickHandler(genderButtonGroup.getCheckedButtonId(), fatPercentageButtonGroup.getCheckedButtonId());
            }
        });


        dobContainer.getEditText().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar cal = Calendar.getInstance();
                int year = cal.get(Calendar.YEAR);
                int month = cal.get(Calendar.MONTH);
                int day = cal.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog dialog = new DatePickerDialog(
                        rootView.getContext(),
                        android.R.style.Theme_Holo_Light_Dialog_MinWidth,
                        userProfileActivityController.setDateChangeListener(
                                new UserProfileActivityController.DateChangeListener() {
                            @Override
                            public void onDateChanged(String newDate) {
                                dobContainer.getEditText().setText(newDate);
                            }
                        }),
                        year,month,day);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.show();
            }
        });

        createProfileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean validateResult = true;


                //Validate Fields
                if(!userProfileActivityController.validateField(firstNameContainer)
                | !userProfileActivityController.validateField(lastNameContainer)
                | !userProfileActivityController.validateField(dobContainer)
                | !userProfileActivityController.validateField(weightContainer)
                | !userProfileActivityController.validateField(heightContainer)
                | !userProfileActivityController.validateField(activityLevelContainer))
                    validateResult = false;


                if(fatPercentageButtonGroup.getCheckedButtonId() == R.id.directFatInputButton) {
                    if (!userProfileActivityController.validateField(fatPercentageContainer))
                        validateResult = false;
                }
                else if(genderButtonGroup.getCheckedButtonId() == R.id.maleButton) {
                    if (!userProfileActivityController.validateField(waistContainer)
                            | !userProfileActivityController.validateField(neckContainer))
                        validateResult = false;
                }else if(genderButtonGroup.getCheckedButtonId() == R.id.femaleButton) {
                    if (!(userProfileActivityController.validateField(waistContainer)
                            & userProfileActivityController.validateField(neckContainer)
                            & userProfileActivityController.validateField(hipContainer)))
                        validateResult = false;
                }

                //
                if(validateResult) {

                    String firstName, lastName, dob;
                    double weight, height, fat, waist, neck, hip;
                    int activityLevel;

                    firstName = firstNameContainer.getEditText().getText().toString();
                    lastName = lastNameContainer.getEditText().getText().toString();
                    dob = dobContainer.getEditText().getText().toString();
                    weight = Double.parseDouble(weightContainer.getEditText().getText().toString());
                    height = Double.parseDouble(heightContainer.getEditText().getText().toString());
                    activityLevel = activityLevelMap.get(activityLevelSelection.getText().toString());

                    if (fatPercentageButtonGroup.getCheckedButtonId() == R.id.directFatInputButton) {
                        fat = Double.parseDouble(fatPercentageContainer.getEditText().getText().toString());
                        userProfileActivityController.registerUserProfile(firstName, lastName, dob, weight,
                                height, fat, Double.NEGATIVE_INFINITY, Double.NEGATIVE_INFINITY, Double.NEGATIVE_INFINITY, activityLevel);
                    } else if (genderButtonGroup.getCheckedButtonId() == R.id.maleButton) {
                        waist = Double.parseDouble(waistContainer.getEditText().getText().toString());
                        neck = Double.parseDouble(neckContainer.getEditText().getText().toString());
                        userProfileActivityController.registerUserProfile(firstName, lastName, dob, weight,
                                height, Double.NEGATIVE_INFINITY, waist, neck, Double.NEGATIVE_INFINITY, activityLevel);
                    } else if (genderButtonGroup.getCheckedButtonId() == R.id.femaleButton) {
                        waist = Double.parseDouble(waistContainer.getEditText().getText().toString());
                        neck = Double.parseDouble(neckContainer.getEditText().getText().toString());
                        hip = Double.parseDouble(hipContainer.getEditText().getText().toString());
                        userProfileActivityController.registerUserProfile(firstName, lastName, dob, weight,
                                height, Double.NEGATIVE_INFINITY, waist, neck, hip, activityLevel);
                    }
                }
            }
        });
    }

    @Override
    public void adaptViewsVisibility(int type) {
        if(type == 1){
            fatPercentageContainer.setVisibility(View.VISIBLE);
            waistContainer.setVisibility(View.GONE);
            neckContainer.setVisibility(View.GONE);
            hipContainer.setVisibility(View.GONE);
        }else if(type == 2){
            fatPercentageContainer.setVisibility(View.GONE);
            waistContainer.setVisibility(View.VISIBLE);
            neckContainer.setVisibility(View.VISIBLE);
            hipContainer.setVisibility(View.GONE);
        }else if(type == 3){
            fatPercentageContainer.setVisibility(View.GONE);
            waistContainer.setVisibility(View.VISIBLE);
            neckContainer.setVisibility(View.VISIBLE);
            hipContainer.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public View getRootView() {
        return rootView;
    }

    @Override
    public void navigateToNewActivity(boolean isUserProfileCreated) {
        if (isUserProfileCreated)
            userProfileActivity.startActivity(new Intent(userProfileActivity, MainActivity.class));
        else
            userProfileActivity.startActivity(new Intent(userProfileActivity, UserProfileActivity.class));
        userProfileActivity.finish();
    }
}
