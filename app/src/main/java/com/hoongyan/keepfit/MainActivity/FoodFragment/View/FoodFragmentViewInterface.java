package com.hoongyan.keepfit.MainActivity.FoodFragment.View;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.hoongyan.keepfit.JavaClass.CalorieSlotDataObject;
import com.hoongyan.keepfit.MVCView;
import com.hoongyan.keepfit.MainActivity.FoodFragment.Controller.FoodFragmentController;
import org.json.JSONException;
import org.json.JSONObject;


public interface FoodFragmentViewInterface extends MVCView {

    public View inflateFragmentLayout(LayoutInflater inflater, ViewGroup container);

    public void setMVCController(FoodFragmentController mvcController);

    public void nlpLayoutHide();

    public void nlpLayoutReveal();

    public void notifyNLPProcessFinish();

    public void createFoodItem(final int calorieViewID, JSONObject foodObject) throws JSONException;

    public void onActivityResult(Intent data);

    public void notifyFirebaseUpdate(boolean status);

    public void updateCalSlotsData(CalorieSlotDataObject object, int selectedSlotIndex);
}
