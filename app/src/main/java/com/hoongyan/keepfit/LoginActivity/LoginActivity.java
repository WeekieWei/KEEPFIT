package com.hoongyan.keepfit.LoginActivity;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import com.facebook.FacebookSdk;
import com.hoongyan.keepfit.LoginActivity.Controller.LoginActivityController;
import com.hoongyan.keepfit.LoginActivity.View.LoginActivityView;

public class LoginActivity extends AppCompatActivity {

    //MVC Components
    LoginActivityView loginActivityView;
    LoginActivityController loginActivityController;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        loginActivityView = new LoginActivityView(LoginActivity.this, null);
        loginActivityController = new LoginActivityController(LoginActivity.this, loginActivityView);
        loginActivityView.setController(loginActivityController);

        setContentView(loginActivityView.getRootView());
        loginActivityView.initializeViews();
        loginActivityView.attachListenersToViews();
    }

    @Override
    protected void onStart() {
        super.onStart();
        loginActivityController.isCurrentUserSignedIn();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        loginActivityController.loginAPIHandler(requestCode, resultCode, data);
    }
}