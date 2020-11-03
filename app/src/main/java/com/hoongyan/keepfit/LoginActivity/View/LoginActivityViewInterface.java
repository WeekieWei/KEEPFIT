package com.hoongyan.keepfit.LoginActivity.View;

import android.app.Activity;
import android.content.Context;

import com.hoongyan.keepfit.MVCView;

public interface LoginActivityViewInterface extends MVCView {

    public Activity getLoginActivityContext();
    public void attachListenersToViews();
    public void generateAlertDialog(String title, String Description, int icon);
    public void navigateToNewActivity(boolean isUserProfileCreated);
}
