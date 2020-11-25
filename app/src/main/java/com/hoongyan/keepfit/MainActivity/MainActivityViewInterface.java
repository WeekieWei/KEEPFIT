package com.hoongyan.keepfit.MainActivity;

import android.widget.TextView;

import androidx.navigation.ui.NavigationUI;

import com.hoongyan.keepfit.MVCView;

public interface MainActivityViewInterface extends MVCView {
    public void setController(MainActivityController mainActivityController);
    public TextView getTextTitle();
    public void setupActionBarWithNavController();
}
