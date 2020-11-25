package com.hoongyan.keepfit.LoginActivity.View;

import android.app.Activity;
import android.view.ViewGroup;

import com.hoongyan.keepfit.LoginActivity.Controller.LoginActivityController;
import com.hoongyan.keepfit.MVCView;

public interface LoginActivityViewInterface extends MVCView {

    public void setController(LoginActivityController controller);
    public void inflateLayout(ViewGroup viewGroup);
    public void removeCover();
    public Activity getLoginActivityContext();
    public void attachListenersToViews();
    public void generateAlertDialog(String title, String Description, int icon);
    public void navigateToNewActivity(boolean isUserProfileCreated);
}
