package com.hoongyan.keepfit.MainActivity.ExerciseFragment.View;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.hoongyan.keepfit.MVCView;
import com.hoongyan.keepfit.MainActivity.ExerciseFragment.Controller.ExerciseFragmentController;
import com.hoongyan.keepfit.MainActivity.FoodFragment.Controller.FoodFragmentController;

import org.json.JSONException;
import org.json.JSONObject;

public interface ExerciseFragmentViewInterface extends MVCView {
    public View inflateFragmentLayout(LayoutInflater inflater, ViewGroup container);

    public void setMVCController(ExerciseFragmentController mvcController);

    public void nlpLayoutHide();

    public void nlpLayoutReveal();

    public void notifyNLPProcessFinish();

    public void createFoodItem(final int calorieViewID, JSONObject foodObject) throws JSONException;

    public void onActivityResult(Intent data);

    public void notifyFirebaseUpdate(boolean status);
}
