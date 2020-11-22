package com.hoongyan.keepfit.MainActivity.UpdateProfile.Controller;

import android.app.DatePickerDialog;
import android.content.Context;
import android.util.Log;
import android.widget.DatePicker;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputLayout;
import com.hoongyan.keepfit.JavaClass.UserProfile;
import com.hoongyan.keepfit.MVCModel;
import com.hoongyan.keepfit.MainActivity.UpdateProfile.View.UpdateProfileView;
import com.hoongyan.keepfit.R;
import com.hoongyan.keepfit.UserProfileActivity.Controller.UserProfileActivityController;

import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;

public class UpdateProfileController implements UpdateProfileControllerInterface{

    public interface DateChangeListener{
        void onDateChanged(String newDate);
    }

    //MVC Components
    private UpdateProfileView updateProfileView;
    private MVCModel mvcModel;

    public UpdateProfileController(Context context, UpdateProfileView updateProfileView){
        this.updateProfileView = updateProfileView;
        mvcModel = new MVCModel(context, true);
    }

    public DatePickerDialog.OnDateSetListener setDateChangeListener(final UpdateProfileController.DateChangeListener dateChangeListener){
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
        return result;
    }

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

    public void updateUserProfile(int gender, String firstName, String lastName, String dob, double weight,
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
                                Toast.makeText(updateProfileView.getRootView().getContext(),
                                        "Profile Successfully Updated", Toast.LENGTH_SHORT).show();
                                updateProfileView.generateAlertDialog(bmi, weightAdjust);
                                mvcModel.saveUserProfileToLocalStorage(userProfile);
                            }
                        }
                    });
                }
            }
        }, userProfile);

    }

    public UserProfile getCurrentData(){
        return mvcModel.getCurrentUserProfileFromLocalStorage();
    }
}
