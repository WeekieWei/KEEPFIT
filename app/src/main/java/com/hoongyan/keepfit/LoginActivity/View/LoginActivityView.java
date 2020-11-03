package com.hoongyan.keepfit.LoginActivity.View;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.facebook.login.widget.LoginButton;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputLayout;
import com.hoongyan.keepfit.LoginActivity.Controller.LoginActivityController;
import com.hoongyan.keepfit.MainActivity.MainActivity;
import com.hoongyan.keepfit.R;
import com.hoongyan.keepfit.UserProfileActivity.UserProfileActivity;

public class LoginActivityView implements LoginActivityViewInterface {

    //MVC Components
    private LoginActivityController loginActivityController;

    //Data Field
    private Activity loginActivity;
    private View rootView;

    //Views
    private TextInputLayout emailInputLayout, passwordInputLayout;
    private MaterialButton signUpButton, signInButton, googleLoginButton, facebookLoginButtonDummy;
    private LoginButton facebookLoginButton;

    public LoginActivityView(Activity loginActivity, ViewGroup viewGroup) {
        this.loginActivity = loginActivity;
    }

    public void setController(LoginActivityController controller){
        this.loginActivityController = controller;
        inflateLayout(null);
    }

    public void inflateLayout(ViewGroup viewGroup){
        loginActivityController.initializeFacebookSDK();
        rootView = LayoutInflater.from(loginActivity).inflate(R.layout.activity_login, viewGroup);
    }

    @Override
    public void initializeViews() {
        emailInputLayout = rootView.findViewById(R.id.emailTextContainer);
        passwordInputLayout = rootView.findViewById(R.id.passwordTextContainer);
        signUpButton = rootView.findViewById(R.id.signUpButton);
        signInButton = rootView.findViewById(R.id.signInButton);
        facebookLoginButton = rootView.findViewById(R.id.facebookLoginButton);
        facebookLoginButtonDummy = rootView.findViewById(R.id.facebookLoginButtonDummy);
        googleLoginButton = rootView.findViewById(R.id.googleLoginButton);
    }

    @Override
    public void attachListenersToViews() {

        emailInputLayout.getEditText().addTextChangedListener(new TextWatcher() {

            Handler handler = new Handler(Looper.getMainLooper());
            Runnable runnable;
            String emailValidationResult = null;

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                handler.removeCallbacks(runnable);
            }

            @Override
            public void afterTextChanged(final Editable s) {
                runnable = new Runnable() {
                    @Override
                    public void run() {
                        emailValidationResult = loginActivityController.isValidEmail(s.toString());
                        if(emailValidationResult != null){
                            emailInputLayout.setError(emailValidationResult);
                        }else{
                            emailInputLayout.setErrorEnabled(false);
                        }
                    }
                };
                handler.postDelayed(runnable, 1000);
            }
        });

        passwordInputLayout.getEditText().addTextChangedListener(new TextWatcher() {

            Handler handler = new Handler(Looper.getMainLooper());
            Runnable runnable;
            String passwordValidationResult = null;

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                handler.removeCallbacks(runnable);
            }

            @Override
            public void afterTextChanged(final Editable s) {
                runnable = new Runnable() {
                    @Override
                    public void run() {
                        passwordValidationResult = loginActivityController.isValidPassword(s.toString());
                        if(passwordValidationResult != null){
                            passwordInputLayout.setError(passwordValidationResult);
                        }else{
                            passwordInputLayout.setErrorEnabled(false);
                        }
                    }
                };
                handler.postDelayed(runnable, 1000);
            }
        });

        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(emailInputLayout.isErrorEnabled() || passwordInputLayout.isErrorEnabled()) {
                    generateAlertDialog("Sign Up Error", "Email/Password field(s) isn't filled correctly", R.drawable.ic_baseline_error_24);
                }
                else{
                    loginActivityController.passwordSignUp(emailInputLayout.getEditText().getText().toString(), passwordInputLayout.getEditText().getText().toString());
                }
            }
        });

        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(emailInputLayout.isErrorEnabled() || passwordInputLayout.isErrorEnabled()) {
                    generateAlertDialog("Sign In Error", "Email/Password field(s) isn't filled correctly", R.drawable.ic_baseline_error_24);
                }
                else {
                    loginActivityController.passwordSignIn(emailInputLayout.getEditText().getText().toString(), passwordInputLayout.getEditText().getText().toString());
                }
            }
        });

        googleLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String idToken = rootView.getContext().getString(R.string.default_web_client_id);
                loginActivityController.googleSignIn(idToken);
            }
        });

        facebookLoginButtonDummy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                facebookLoginButton.performClick();
            }
        });

        facebookLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginActivityController.facebookSignIn(facebookLoginButton);
            }
        });
    }

    @Override
    public void generateAlertDialog(String title, String description, int icon) {

        AlertDialog alertDialog = new AlertDialog.Builder(rootView.getContext()).create();

        alertDialog.setTitle(title);
        alertDialog.setMessage(description);
        alertDialog.setIcon(icon);
        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "OK", (DialogInterface.OnClickListener) null);
        alertDialog.show();
    }

    @Override
    public void navigateToNewActivity(boolean isUserProfileCreated) {
        if (isUserProfileCreated)
            loginActivity.startActivity(new Intent(loginActivity, MainActivity.class));
        else
            loginActivity.startActivity(new Intent(loginActivity, UserProfileActivity.class));
        loginActivity.finish();
    }

    @Override
    public View getRootView() {
        return rootView;
    }

    @Override
    public Activity getLoginActivityContext() {
        return loginActivity;
    }
}
