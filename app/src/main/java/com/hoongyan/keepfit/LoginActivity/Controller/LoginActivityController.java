package com.hoongyan.keepfit.LoginActivity.Controller;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.util.Base64;
import android.util.Log;
import android.util.Patterns;
import android.widget.Toast;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseUser;
import com.hoongyan.keepfit.LoginActivity.View.LoginActivityView;
import com.hoongyan.keepfit.MVCModel;
import com.hoongyan.keepfit.R;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.regex.Pattern;

public class LoginActivityController implements LoginActivityControllerInterface {

    //MVC Components
    private LoginActivityView loginActivityView;
    private MVCModel mvcModel;

    //Password Pattern Regex

    private static final Pattern PASSWORD_PATTERN_UPPERCASE =
            Pattern.compile("^(?=.*[A-Z]).*$");

    private static final Pattern PASSWORD_PATTERN_LOWERCASE =
            Pattern.compile("^(?=.*[a-z]).*$");

    private static final Pattern PASSWORD_PATTERN_NUMBER =
            Pattern.compile("^(?=.*[0-9]).*$");

    private static final Pattern PASSWORD_PATTERN_LENGTH_NO_SPACE =
            Pattern.compile("^(?=\\S+$).{8,15}$");

    //Data Field
    private CallbackManager facebookCallbackManager;
    private FirebaseUser currentUser;

    public LoginActivityController(Activity activity, LoginActivityView loginActivityView) {
        this.loginActivityView = loginActivityView;
        mvcModel = new MVCModel(activity);

//        try {
//            PackageInfo info = activity.getPackageManager().getPackageInfo(
//                    "com.hoongyan.keepfit",
//                    PackageManager.GET_SIGNATURES);
//            for (Signature signature : info.signatures) {
//                MessageDigest md = MessageDigest.getInstance("SHA");
//                md.update(signature.toByteArray());
//                Log.d("KeyHash:", Base64.encodeToString(md.digest(), Base64.DEFAULT));
//            }
//        }
//        catch (PackageManager.NameNotFoundException e) {
//        }
//        catch (NoSuchAlgorithmException e) {
//        }
    }

    @Override
    public void checkPermissionsGranted() {

    }

    @Override
    public void isCurrentUserSignedIn() {
        currentUser = mvcModel.getCurrentUser();

        if (currentUser != null) {
            mvcModel.isUserProfileCreated(new MVCModel.TaskResultStatus() {
                @Override
                public void onResultReturn(boolean result) {
                    loginActivityView.navigateToNewActivity(result);
                }
            });
        }
    }

    @Override
    public void initializeFacebookSDK() {
        FacebookSdk.setApplicationId("1805409479607854");
        FacebookSdk.sdkInitialize(loginActivityView.getLoginActivityContext().getApplicationContext());
        AppEventsLogger.activateApp(loginActivityView.getLoginActivityContext().getApplication());
    }

    @Override
    public String isValidEmail(String email) {
        if(email.isEmpty()){
            return "The field can't be empty";
        }else if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            return "Please enter a valid email address";
        }else{
            return null;
        }
    }

    @Override
    public String isValidPassword(String password) {
        String resultString = "";
        boolean isTriggered = false;

        if(password.isEmpty()) {
            return  "The field can't be empty";
        }

        if(!PASSWORD_PATTERN_UPPERCASE.matcher(password).matches()){
            if(isTriggered) resultString += "\n";
            resultString += "•Must have at least 1 upper case";
            isTriggered = true;
        }

        if(!PASSWORD_PATTERN_LOWERCASE.matcher(password).matches()){
            if(isTriggered) resultString += "\n";
            resultString += "•Must have at least 1 lower case";
            isTriggered = true;
        }

        if(!PASSWORD_PATTERN_NUMBER.matcher(password).matches()){
            if(isTriggered) resultString += "\n";
            resultString += "•Must have at least 1 number";
            isTriggered = true;
        }

        if(!PASSWORD_PATTERN_LENGTH_NO_SPACE.matcher(password).matches()){
            if(isTriggered) resultString += "\n";
            resultString += "•Length must be 8-15 & without spaces";
        }

        return resultString.isEmpty() ? null : resultString;
    }

    @Override
    public void passwordSignUp(String email, String password) {

        mvcModel.signUp(new MVCModel.AuthenticationCallBack() {
            @Override
            public void onCallBack(String title, String message, int icon) {
                loginActivityView.generateAlertDialog(title, message, icon);
            }
        }, email, password);
    }

    @Override
    public void passwordSignIn(String email, String password) {

        mvcModel.signIn(new MVCModel.AuthenticationCallBack() {
            @Override
            public void onCallBack(String title, String message, int icon) {
                loginCallBackHandler(title, message, icon);
            }
        }, email, password);
    }

    @Override
    public void googleSignIn(final String idToken) {
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(idToken)
                .requestEmail()
                .build();

        final GoogleSignInClient googleSignInClient = GoogleSignIn.getClient(loginActivityView.getLoginActivityContext(), gso);

        Intent signInIntent = googleSignInClient.getSignInIntent();

        loginActivityView.getLoginActivityContext().startActivityForResult(signInIntent, 9001);
    }

    @Override
    public void facebookSignIn(LoginButton loginButton) {
        facebookCallbackManager = CallbackManager.Factory.create();
        //loginButton.setPermissions("email", "public_profile");
        loginButton.setReadPermissions("email", "public_profile");
        loginButton.registerCallback(facebookCallbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                Log.d("FACEBOOK", "facebook:onSuccess:" + loginResult);
                mvcModel.firebaseAuthWithFacebook(new MVCModel.AuthenticationCallBack() {
                    @Override
                    public void onCallBack(String title, String message, int icon) {
                        loginCallBackHandler(title, message, icon);
                    }
                }, loginResult.getAccessToken());
            }

            @Override
            public void onCancel() {
                Log.d("FACEBOOK", "facebook:onCancel");
            }

            @Override
            public void onError(FacebookException error) {
                Log.d("FACEBOOK ERROR", "facebook:onError", error);
                loginActivityView.generateAlertDialog("Sign In Error", error.getMessage(), R.drawable.ic_baseline_error_24);
            }
        });
    }

    @Override
    public void loginAPIHandler(int requestCode, int resultCode, Intent data){
        if (requestCode == 9001) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                Log.d("GOOGLE", "firebaseAuthWithGoogle:" + account.getId());
                mvcModel.firebaseAuthWithGoogle(new MVCModel.AuthenticationCallBack() {
                    @Override
                    public void onCallBack(String title, String message, int icon) {
                        loginCallBackHandler(title, message, icon);
                    }
                }, account.getIdToken());
            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
                loginActivityView.generateAlertDialog("GOOGLE SIGN IN FAILED", e.getMessage(), R.drawable.ic_baseline_error_24);
                Log.w("GOOGLE", "Google sign in failed", e);
            }
        } else {
            // ITS FACEBOOK
            facebookCallbackManager.onActivityResult(requestCode, resultCode, data);
        }
    }

    private void loginCallBackHandler(String title, String message, int icon) {
        if (title != null && message != null) {
            loginActivityView.generateAlertDialog(title, message, icon);
        } else {
            mvcModel.setDatabaseReference();
            mvcModel.isUserProfileCreated(new MVCModel.TaskResultStatus() {
                @Override
                public void onResultReturn(boolean result) {
                    loginActivityView.navigateToNewActivity(result);
                }
            });
            Toast.makeText(loginActivityView.getLoginActivityContext(), "LOGIN SUCCESS", Toast.LENGTH_LONG).show();
        }
    }

}
