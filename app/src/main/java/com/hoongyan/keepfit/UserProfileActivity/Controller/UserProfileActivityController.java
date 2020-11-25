package com.hoongyan.keepfit.UserProfileActivity.Controller;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.util.Log;
import android.widget.DatePicker;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputLayout;
import com.hoongyan.keepfit.MVCModel;
import com.hoongyan.keepfit.R;
import com.hoongyan.keepfit.JavaClass.UserProfile;
import com.hoongyan.keepfit.UserProfileActivity.View.UserProfileActivityView;

import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;

public class UserProfileActivityController implements UserProfileActivityControllerInterface {

    public interface DateChangeListener{
        void onDateChanged(String newDate);
    }

    //MVC Components
    private UserProfileActivityView userProfileActivityView;
    private MVCModel mvcModel;

    //Data Field

    public UserProfileActivityController(UserProfileActivityView userProfileActivityView, Activity userProfileActivity) {
        this.userProfileActivityView = userProfileActivityView;
        this.mvcModel = new MVCModel(userProfileActivity);
    }

    @Override
    public void buttonGroupClickHandler(int genderCheckedId, int fatPercentageCheckedId) {
//        int visibilityType = 0;
//
//        if(fatPercentageCheckedId == R.id.directFatInputButton)
//            visibilityType = 1;
//        else if(genderCheckedId == R.id.maleButton && fatPercentageCheckedId == R.id.indirectFatInputButton)
//            visibilityType = 2;
//        else if(genderCheckedId == R.id.femaleButton && fatPercentageCheckedId == R.id.indirectFatInputButton)
//            visibilityType = 3;
//
//        userProfileActivityView.adaptViewsVisibility(visibilityType);
    }

    @Override
    public DatePickerDialog.OnDateSetListener setDateChangeListener(final DateChangeListener dateChangeListener){
        DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                month = month + 1;

                String monthStr = "" + month + "";
                String dayStr = "" + day + "";


                Log.d("DATE", "onDateSet: mm/dd/yyy: " + month + "/" + day + "/" + year);

                if (month < 10) monthStr = "0" + month;
                if (day < 10) dayStr = "0" + day;

                String date = dayStr + "/" + monthStr + "/" + year;
                dateChangeListener.onDateChanged(date);
            }
        };
        return dateSetListener;
    }

    @Override
    public boolean validateField(TextInputLayout container) {

        boolean result = true;

        /* General Rules for All Fields */

        //check for empty field
        if(container.getEditText().getText().toString().isEmpty()){
            container.setError("Empty Field");
            result = false;
        } else
            container.setErrorEnabled(false);

        /* Rules for Individual Field */

        //Name Field
        if(container.getId() == R.id.firstNameContainer || container.getId() == R.id.lastNameContainer){
            String data = container.getEditText().getText().toString();

            if(data.matches(".*\\d.*")){
                container.setError("Name can't have numbers");
                result = false;
            }else if(!data.isEmpty())
                container.setErrorEnabled(false);
        }

        //DOB
        if(container.getId() == R.id.dobContainer){
            if (container.getEditText().getText().toString().equals("DD/MM/YYYY")) {
                container.setError("DOB hasn't set");
                result = false;
            }else
                container.setErrorEnabled(false);
        }

        //Weight
        if(container.getId() == R.id.weightContainer){
            String data = container.getEditText().getText().toString();

            if(!data.isEmpty()) {
                double value = Double.parseDouble(data);
                if (value < 30 || value > 200) {
                    container.setError("Illogical Weight Value");
                    result = false;
                }else
                    container.setErrorEnabled(false);
            }
        }

        //Height
        if(container.getId() == R.id.heightContainer){
            String data = container.getEditText().getText().toString();

            if(!data.isEmpty()) {
                double value = Double.parseDouble(data);
                if (value < 50 || value > 250) {
                    container.setError("Illogical Height Value");
                    result = false;
                }else
                    container.setErrorEnabled(false);
            }
        }

        //Fat Percentage (RAW)
//        if(container.getId() == R.id.fatPercentageContainer){
//            String data = container.getEditText().getText().toString();
//
//            if(!data.isEmpty()) {
//                double value = Double.parseDouble(data);
//                if (value < 0 || value > 60) {
//                    container.setError("Illogical Fat Percentage Value");
//                    result = false;
//                }else
//                    container.setErrorEnabled(false);
//            }
//        }

        //Waist
//        if(container.getId() == R.id.waistContainer){
//            String data = container.getEditText().getText().toString();
//
//            if(!data.isEmpty()) {
//                double value = Double.parseDouble(data);
//                if (value < 20 || value > 150) {
//                    container.setError("Illogical Waist Circumference Value");
//                    result = false;
//                }else
//                    container.setErrorEnabled(false);
//            }
//        }

        //Neck
//        if(container.getId() == R.id.neckContainer){
//            String data = container.getEditText().getText().toString();
//
//            if(!data.isEmpty()) {
//                double value = Double.parseDouble(data);
//                if (value < 20 || value > 60) {
//                    container.setError("Illogical Neck Circumference Value");
//                    result = false;
//                }else
//                    container.setErrorEnabled(false);
//            }
//        }

        //Hip
//        if(container.getId() == R.id.hipContainer){
//            String data = container.getEditText().getText().toString();
//
//            if(!data.isEmpty()) {
//                double value = Double.parseDouble(data);
//                if (value < 20 || value > 150) {
//                    container.setError("Illogical Hip Circumference Value");
//                    result = false;
//                }else
//                    container.setErrorEnabled(false);
//            }
//        }
        return result;


    }

    @Override
    public boolean validateWH(TextInputLayout weightContainer, TextInputLayout heightContainer){
        String weight = weightContainer.getEditText().getText().toString();
        String height = heightContainer.getEditText().getText().toString();
        if(!weight.isEmpty() && !height.isEmpty()){
            double weightVal = Double.parseDouble(weight);
            double heightVal = Double.parseDouble(height);

            if (heightVal < weightVal) {
                weightContainer.setError("Weight > Height??");
                heightContainer.setError("Height < Weight??");
                return false;
            }else {
                weightContainer.setErrorEnabled(false);
                heightContainer.setErrorEnabled(false);
            }
        }
        return true;
    }

    @Override
    public void registerUserProfile(int gender, String firstName, String lastName, String dob, double weight,
                                    double height, int activityLevel){
        double calRequired;

        //Age
        LocalDate localDate = LocalDate.parse(dob, DateTimeFormatter.ofPattern("dd/MM/yyyy"));
        int age = Period.between(localDate, LocalDate.now()).getYears();


        //Daily Calorie Required calculation
        //Reference: https://www.calculator.net/calorie-calculator.html

        if(gender == 1){
            //Mifflin-St Jeor Equation
            calRequired = 10 * weight + 6.25 * height - 5 * age + 5;
        }else if(gender == 2){
            calRequired = 10 * weight + 6.25 * height - 5 * age - 161;
        }else{
            calRequired = 0;
        }

        switch(activityLevel){
            case 1 : calRequired *= 1.2;
                break;
            case 2 : calRequired *= 1.375;
                break;
            case 3 : calRequired *= 1.55;
                break;
            case 4 : calRequired *= 1.725;
                break;
            case 5 : calRequired *= 1.9;
                break;
        }

        double bmi = weight / height / height * 10000;

        int weightAdjust;
        if(bmi < 18.5){ //Gain Weight Constant
            weightAdjust = 500;
        }else if(bmi > 25){ //Lose Weight Constant
            weightAdjust = -500;
        }else{ //Maintain Weight;
            weightAdjust = 0;
        }

        String genderStr;
        if(gender == 1)
            genderStr = "Male";
        else
            genderStr = "Female";

        UserProfile userProfile = new UserProfile(firstName, lastName, genderStr, dob, weight, height, calRequired, bmi, weightAdjust, activityLevel);
        mvcModel.createUserProfile(new MVCModel.TaskResultStatus() {
            @Override
            public void onResultReturn(boolean result) {
                if(result){
                    mvcModel.isUserProfileCreated(new MVCModel.TaskResultStatus() {
                        @Override
                        public void onResultReturn(boolean result) {
                            if(result){
                                Toast.makeText(userProfileActivityView.getRootView().getContext(),
                                        "Profile Successfully Created", Toast.LENGTH_SHORT).show();
                                userProfileActivityView.generateAlertDialog(bmi, weightAdjust);
                                mvcModel.saveUserProfileToLocalStorage(userProfile);
                            }
                        }
                    });
                }
            }
        }, userProfile);

    }

    @Override
    public void registerUserProfile(int gender, String firstName, String lastName, String dob, double weight,
                                    double height, double fat, double waist, double neck, double hip, int activityLevel) {

        double calRequired;

        //if fat is not input directly
        if(fat == Double.NEGATIVE_INFINITY) {

            if (hip == Double.NEGATIVE_INFINITY) {
                //male
                gender = 1;
                fat = (495 / (1.0324 - 0.19077 * Math.log10(waist - neck) + 0.15456 * Math.log10(height))) - 450;
            } else {
                //female
                gender = 2;
                fat = (495 / (1.29579 - 0.35004 * Math.log10(waist + hip - neck) + 0.22100 * Math.log10(height))) - 450;
            }
        }

        //Age
        LocalDate localDate = LocalDate.parse(dob, DateTimeFormatter.ofPattern("dd/MM/yyyy"));
        int age = Period.between(localDate, LocalDate.now()).getYears();


        //Daily Calorie Required calculation
        //Reference: https://www.calculator.net/calorie-calculator.html

        if(gender == 1){
            //Mifflin-St Jeor Equation
            calRequired = 10 * weight + 6.25 * height - 5 * age + 5;
        }else if(gender == 2){
            calRequired = 10 * weight + 6.25 * height - 5 * age - 161;
        }else{
            //Katch-Mcardle Formula
            //calRequired = 370 + 21.6 * (1 - fat) * weight;
            calRequired = 0;
        }

        switch(activityLevel){
            case 1 : calRequired *= 1.2;
            break;
            case 2 : calRequired *= 1.375;
            break;
            case 3 : calRequired *= 1.55;
            break;
            case 4 : calRequired *= 1.725;
            break;
            case 5 : calRequired *= 1.9;
            break;
        }

        double bmi = weight / height / height * 10000;

        int weightAdjust;
        if(bmi < 18.5){ //Gain Weight Constant
            weightAdjust = 500;
        }else if(bmi > 25){ //Lose Weight Constant
            weightAdjust = -500;
        }else{ //Maintain Weight;
            weightAdjust = 0;
        }

        String genderStr;
        if(gender == 1)
            genderStr = "Male";
        else
            genderStr = "Female";

//        UserProfile userProfile = new UserProfile(firstName, lastName, genderStr, dob, weight, height, fat, calRequired, bmi, weightAdjust, activityLevel);
//        mvcModel.createUserProfile(new MVCModel.TaskResultStatus() {
//            @Override
//            public void onResultReturn(boolean result) {
//                if(result){
//                    mvcModel.isUserProfileCreated(new MVCModel.TaskResultStatus() {
//                        @Override
//                        public void onResultReturn(boolean result) {
//                            if(result){
//                                Toast.makeText(userProfileActivityView.getRootView().getContext(),
//                                        "Profile Successfully Created", Toast.LENGTH_SHORT).show();
//                                userProfileActivityView.generateAlertDialog(bmi, weightAdjust);
//                                mvcModel.saveUserProfileToLocalStorage(userProfile);
//                            }
//                        }
//                    });
//                }
//            }
//        }, userProfile);
    }
}
