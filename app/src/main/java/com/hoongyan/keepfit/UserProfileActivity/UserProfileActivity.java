package com.hoongyan.keepfit.UserProfileActivity;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.hoongyan.keepfit.UserProfileActivity.View.UserProfileActivityView;

public class UserProfileActivity extends AppCompatActivity {

    //MVC Components
    private UserProfileActivityView userProfileActivityView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        userProfileActivityView = new UserProfileActivityView(UserProfileActivity.this, null);
        setContentView(userProfileActivityView.getRootView());
        userProfileActivityView.initializeViews();
        userProfileActivityView.attachListenersToButtonGroups();
    }
}