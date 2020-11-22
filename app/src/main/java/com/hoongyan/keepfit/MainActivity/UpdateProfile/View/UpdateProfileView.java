package com.hoongyan.keepfit.MainActivity.UpdateProfile.View;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.TextView;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.button.MaterialButtonToggleGroup;
import com.google.android.material.textfield.TextInputLayout;
import com.hoongyan.keepfit.JavaClass.UserProfile;
import com.hoongyan.keepfit.MainActivity.MainActivity;
import com.hoongyan.keepfit.MainActivity.UpdateProfile.Controller.UpdateProfileController;
import com.hoongyan.keepfit.MainActivity.UpdateProfile.UpdateProfileFragment;
import com.hoongyan.keepfit.R;
import com.hoongyan.keepfit.UserProfileActivity.Controller.UserProfileActivityController;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class UpdateProfileView implements UpdateProfileViewInterface{

    //MVC Components
    private UpdateProfileController updateProfileController;

    //Data Field
    private UpdateProfileFragment fragment;
    private View rootView;
    private TextView mainTitle;

    private String[] activityLevelOptions;
    private Map<String, Integer> activityLevelMap;
    private ArrayAdapter<String> activityLevelOptionsAdapter;

    private TextInputLayout firstNameContainer, lastNameContainer, dobContainer, weightContainer,
            heightContainer, activityLevelContainer;

    private AutoCompleteTextView activityLevelSelection;

    private MaterialButtonToggleGroup genderButtonGroup, fatPercentageButtonGroup;

    private MaterialButton updateProfileButton, maleButton, femaleButton;

    public UpdateProfileView(UpdateProfileFragment fragment){
        this.fragment = fragment;

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
        activityLevelOptionsAdapter = new ArrayAdapter<>(fragment.getContext(), R.layout.support_simple_spinner_dropdown_item, activityLevelOptions);
    }

    public View inflateFragmentLayout(LayoutInflater inflater, ViewGroup container){
        rootView = inflater.inflate(R.layout.activity_user_profile, container, false);
        initializeViews();
        return rootView;
    }

    public void setMVCController(UpdateProfileController controller){
        updateProfileController = controller;
    }

    @Override
    public void initializeViews() {
        mainTitle = rootView.findViewById(R.id.profileMainTitle);
        mainTitle.setText("Update Your Profile");

        firstNameContainer = rootView.findViewById(R.id.firstNameContainer);
        lastNameContainer = rootView.findViewById(R.id.lastNameContainer);

        maleButton = rootView.findViewById(R.id.maleButton);
        femaleButton = rootView.findViewById(R.id.femaleButton);

        dobContainer = rootView.findViewById(R.id.dobContainer);
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
                        updateProfileController.setDateChangeListener(
                                new UpdateProfileController.DateChangeListener() {
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
        weightContainer = rootView.findViewById(R.id.weightContainer);
        heightContainer = rootView.findViewById(R.id.heightContainer);

        activityLevelContainer = rootView.findViewById(R.id.activityLevelContainer);
        activityLevelSelection = rootView.findViewById(R.id.activityLevelSelection);
        activityLevelSelection.setAdapter(activityLevelOptionsAdapter);

        genderButtonGroup = rootView.findViewById(R.id.genderButtonGroup);

        updateProfileButton = rootView.findViewById(R.id.createProfileButton);
        updateProfileButton.setText("Update Profile");
        updateProfileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int gender;
                boolean validateResult = true;


                //Validate Fields
                if(!updateProfileController.validateField(firstNameContainer)
                        | !updateProfileController.validateField(lastNameContainer)
                        | !updateProfileController.validateField(dobContainer)
                        | !updateProfileController.validateField(weightContainer)
                        | !updateProfileController.validateField(heightContainer)
                        | !updateProfileController.validateWH(weightContainer, heightContainer)
                        | !updateProfileController.validateField(activityLevelContainer))
                    validateResult = false;

                gender = genderButtonGroup.getCheckedButtonId() == R.id.maleButton ? 1 : 2;

                if(validateResult) {

                    String firstName, lastName, dob;
                    double weight, height;
                    int activityLevel;
                    firstName = firstNameContainer.getEditText().getText().toString();
                    lastName = lastNameContainer.getEditText().getText().toString();
                    dob = dobContainer.getEditText().getText().toString();
                    weight = Double.parseDouble(weightContainer.getEditText().getText().toString());
                    height = Double.parseDouble(heightContainer.getEditText().getText().toString());
                    activityLevel = activityLevelMap.get(activityLevelSelection.getText().toString());

                    updateProfileController.updateUserProfile(gender, firstName, lastName, dob, weight,
                            height, activityLevel);

                }
            }
        });

        fillDataField();
    }

    private void fillDataField() {
        UserProfile userProfile = updateProfileController.getCurrentData();

        if(firstNameContainer.getEditText().getText().toString().isEmpty())
            firstNameContainer.getEditText().setText(userProfile.getFirstName());
        if(lastNameContainer.getEditText().getText().toString().isEmpty())
            lastNameContainer.getEditText().setText(userProfile.getLastName());

        if(userProfile.getGender().equals("Male"))
            maleButton.setChecked(true);
        else
            femaleButton.setChecked(true);

        if(dobContainer.getEditText().getText().toString().equals("DD/MM/YYYY"))
            dobContainer.getEditText().setText(userProfile.getDob());

        if(weightContainer.getEditText().getText().toString().isEmpty())
            weightContainer.getEditText().setText(String.valueOf(userProfile.getWeight()));

        if(heightContainer.getEditText().getText().toString().isEmpty())
            heightContainer.getEditText().setText(String.valueOf(userProfile.getHeight()));

    }

    public void generateAlertDialog(double bmi, int weightAdjust) {

        AlertDialog alertDialog = new AlertDialog.Builder(rootView.getContext()).create();


        String title = "Your BMI: " + String.format("%.1f", bmi);

        String message = "The app is set to automatically help you to ";
        if (weightAdjust < 0) {
            title += " (Overweight)";
            message += "lose weight ";
        } else if (weightAdjust > 0){
            title += " (Underweight)";
            message += "gain weight";
        }else {
            title += " (Healthy Weight)";
            message += "maintain weight";
        }
        message += ", you can also change it in setting later.";

        alertDialog.setTitle(title);
        alertDialog.setMessage(message);
        alertDialog.setCanceledOnTouchOutside(false);
        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                fragment.getActivity().startActivity(new Intent(fragment.getActivity().getApplicationContext(), MainActivity.class));
                fragment.getActivity().finish();
            }
        });
        alertDialog.show();

    }

    @Override
    public View getRootView() {
        return rootView;
    }
}
