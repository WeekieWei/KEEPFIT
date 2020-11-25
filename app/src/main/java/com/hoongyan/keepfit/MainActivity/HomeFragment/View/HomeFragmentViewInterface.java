package com.hoongyan.keepfit.MainActivity.HomeFragment.View;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.CombinedData;
import com.hoongyan.keepfit.JavaClass.UserHomePageData;
import com.hoongyan.keepfit.MVCView;
import com.hoongyan.keepfit.MainActivity.HomeFragment.Controller.HomeFragmentController;

import java.util.ArrayList;

public interface HomeFragmentViewInterface extends MVCView {

    public View inflateFragmentLayout(LayoutInflater inflater, ViewGroup container);

    public void setMVCController(HomeFragmentController controller);

    public void notifyUpdateWeightSuccess(float bmi, float weightAdjust);

    public void onPause();

    public void onResume();

    public void bindDataToViews(UserHomePageData data);

    public void bindDataToCard3Chart(BarData barData, ArrayList<String> xAxisLabel, float avgNet, boolean isNotEmpty);

    public void bindDataToCard4Chart(CombinedData combinedData, ArrayList<String> xAxisLabel, float weight, boolean isNotEmpty);
}
