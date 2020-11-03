package com.hoongyan.keepfit.LoginActivity.Controller;

import android.content.Intent;

import com.facebook.login.widget.LoginButton;

public interface LoginActivityControllerInterface {
    public void checkPermissionsGranted();
    public void isCurrentUserSignedIn();
    public void initializeFacebookSDK();
    public String isValidEmail(String email);
    public String isValidPassword(String password);
    public void passwordSignUp(String email, String password);
    public void passwordSignIn(String email, String password);
    public void googleSignIn(String idToken);
    public void facebookSignIn(LoginButton loginButton);
    public void loginAPIHandler(int requestCode, int resultCode, Intent data);

}
