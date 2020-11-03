package com.hoongyan.keepfit;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.util.Log;

import androidx.annotation.NonNull;

import com.facebook.AccessToken;
import com.facebook.login.LoginManager;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldPath;
import com.google.firebase.firestore.FirebaseFirestore;
import com.hoongyan.keepfit.JavaClass.FoodHistoryObject;
import com.hoongyan.keepfit.JavaClass.LocalDatabaseHelper;
import com.hoongyan.keepfit.JavaClass.UserDailyData;
import com.hoongyan.keepfit.JavaClass.UserProfile;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static android.content.Context.MODE_PRIVATE;

public class MVCModel {
    private Activity activity;
    private FirebaseAuth auth;
    private FirebaseUser user;
    private FirebaseFirestore database;
    private DocumentReference db_userProfile;
    private CollectionReference db_userData;
    private SharedPreferences localStorage;
    private LocalDatabaseHelper localDatabase;


    public interface AuthenticationCallBack {
        void onCallBack(String title, String message, int icon);
    }

    public interface TaskResultStatus {
        void onResultReturn(boolean result);
    }

    public MVCModel(Activity activity) {
        this.activity = activity;
        auth = FirebaseAuth.getInstance();
        user = getCurrentUser();
        database = FirebaseFirestore.getInstance();
        if(user != null) setDatabaseReference();
        localStorage = activity.getSharedPreferences("KEEPFIT Storage", MODE_PRIVATE);
        localDatabase = new LocalDatabaseHelper(activity.getApplicationContext());
    }

    public MVCModel(Context context, boolean firebaseAccess){
        if(firebaseAccess){
            auth = FirebaseAuth.getInstance();
            user = getCurrentUser();
            database = FirebaseFirestore.getInstance();
        }
        if(user != null) setDatabaseReference();
        localStorage = context.getSharedPreferences("KEEPFIT Storage", MODE_PRIVATE);
        localDatabase = new LocalDatabaseHelper(context);
    }

    public void setDatabaseReference(){
        db_userProfile = database.collection("users").document(getCurrentUser().getUid());
        db_userData = db_userProfile.collection("data");
    }

    public FirebaseUser getCurrentUser(){
        if(user != null)
            user.reload();
        else
            user = auth.getCurrentUser();
        return user;
    }


    //LOGIN & SIGN UP METHODS
    public void signUp(final AuthenticationCallBack callBack, final String email, final String password){
        auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(activity, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        String title, message;
                        int icon;
                        if (task.isSuccessful()) {
                            user = auth.getCurrentUser();
                            user.sendEmailVerification();
                            title = "Registration Successful";
                            message = "Email Verification sent to\n" + email;
                            icon = R.drawable.ic_baseline_email_24;
                        } else {
                            title = "Sign Up Error";
                            message = task.getException().getMessage();
                            icon = R.drawable.ic_baseline_error_24;
                        }
                        callBack.onCallBack(title, message, icon);
                    }
                });
    }

    public void signIn(final AuthenticationCallBack callBack, final String email, final String password){
        auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(activity, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        getCurrentUser();
                        String title = null, message = null;
                        int icon = 0;
                        if(task.isSuccessful()){

                            if(!user.isEmailVerified()) {
                                title = "Sign In Failed";
                                message = "Please verify your email at \n" + email;
                                icon = R.drawable.ic_baseline_error_24;
                                user.sendEmailVerification();
                            }
                        } else {
                            title = "Sign In Failed";
                            message = task.getException().getMessage();
                            icon = R.drawable.ic_baseline_error_24;
                        }
                        callBack.onCallBack(title, message, icon);
                    }
                });
    }

    public void firebaseAuthWithGoogle(final AuthenticationCallBack callBack, final String idToken){
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        auth.signInWithCredential(credential)
                .addOnCompleteListener(activity, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        String title = null, message = null;
                        int icon = 0;
                        if (!task.isSuccessful()) {
                            title = "Google Sign In Failed";
                            message = task.getException().getMessage();
                            icon = R.drawable.ic_baseline_error_24;
                        }
                        callBack.onCallBack(title, message, icon);
                    }
                });
    }

    public void firebaseAuthWithFacebook(final AuthenticationCallBack callBack, final AccessToken token){
        Log.d("FACEBOOK", "handleFacebookAccessToken:" + token);

        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        auth.signInWithCredential(credential)
                .addOnCompleteListener(activity, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        String title = null, message = null;
                        int icon = 0;
                        if (!task.isSuccessful()) {
                            // If sign in fails, display a message to the user.
                            Log.w("FACEBOOK", "signInWithCredential:failure", task.getException());
                            title = "Facebook Sign In Failed";
                            message = task.getException().getMessage();
                            icon = R.drawable.ic_baseline_error_24;
                            LoginManager.getInstance().logOut();
                        }
                        callBack.onCallBack(title, message, icon);
                    }
                });
    }

    public void isUserProfileCreated(final TaskResultStatus status){

        db_userProfile.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()){
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()){
                        status.onResultReturn(true);
                    } else
                        status.onResultReturn(false);
                }
            }
        });
    }

    public void createUserProfile(final TaskResultStatus status, UserProfile userProfile){

        db_userProfile.set(userProfile).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    status.onResultReturn(true);
                }else
                    status.onResultReturn(false);
            }
        });
    }

    //STEP COUNTER METHODS
    public boolean isFirstTimeInstalled(){
        if(localStorage.getBoolean("firstTimeInstallation", true)){
            localStorage.edit().putBoolean("firstTimeInstallation", false).apply();
            Log.i("FIRST TIME INSTALLATION", "YES");
            return true;
        }else
            return false;
    }

    public void setLoginStatus(boolean isLoggedIn){
        if(isLoggedIn){
            localStorage.edit().putBoolean("isLoggedIn", true).apply();
        }else{
            localStorage.edit().putBoolean("isLoggedIn", false).apply();
        }
    }

    public boolean getLoginStatus(){
        return localStorage.getBoolean("isLoggedIn", false);
    }

    public float getPrevTotalSteps(){
        return localStorage.getFloat("prevTotalSteps", 0f);
    }

    public void getCurrentStepsFromFirebase(){
        db_userData.document("dailyData").get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if(task.isSuccessful()){
                                DocumentSnapshot document = task.getResult();

                                if(!document.exists()){
                                    saveCurrentSteps(0);
                                    return;
                                }

                                Timestamp timestamp = document.getTimestamp("timestamp");

                                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

                                String todayDate = dateFormat.format(new Date());
                                String dataDate = dateFormat.format(timestamp.toDate());

                                if(!todayDate.equals(dataDate))
                                    saveCurrentSteps(0);
                                else {
                                    float steps = document.getDouble("steps").floatValue();
                                    saveCurrentSteps(steps);
                                }
                            }
                        }
                });

    }

    public float getCurrentSteps(){
        return localStorage.getFloat("currentSteps", 0f);
    }

    public void saveSteps(float currentSteps, float prevTotalSteps){
        localStorage.edit().putFloat("currentSteps" , (float) currentSteps).apply();
        localStorage.edit().putFloat("prevTotalSteps" , (float) prevTotalSteps).apply();
    }

    public boolean saveCurrentSteps(float currentSteps){
        return localStorage.edit().putFloat("currentSteps" , currentSteps).commit();
    }

    //HOME
    public void updateLocalDatabase(int steps, Date date){

        if(steps == Integer.MIN_VALUE) steps = (int) getCurrentSteps();

        if(localDatabase.updateDailyData(steps, date)){
            Log.i("UPDATE LOCAL DATABASE", "SUCCESS AT " + getCurrentTimeStamp());

            localStorage.edit().putString("Debug_LocalDatabaseUpdate", getCurrentTimeStamp()).apply();

            Cursor data = localDatabase.getData();

            while(data.moveToNext()){

                Log.i("DATA", data.getString(0) + "    " + data.getInt(1));
            }
            localDatabase.close();
        }else{
            Log.i("UPDATE LOCAL DATABASE", "FAILED AT " + getCurrentTimeStamp());
        }
    }

    public void updateFirebase(TaskResultStatus resultStatus){

        Cursor cursor = localDatabase.getData();

        if(cursor.getCount() < 1){
            cursor.close();
            localDatabase.close();
            return;
        }

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String today = dateFormat.format(new Date());

        UserDailyData userDailyData = new UserDailyData();

        while(cursor.moveToNext()){

            try {
                int steps = cursor.getInt(1);

                userDailyData.setSteps(cursor.getInt(1));
                String dateStr = cursor.getString(0);


                Date date = dateFormat.parse(dateStr);
                userDailyData.setTimestamp(new Timestamp(date));

                //Log.i("TIMESTAMP", userDailyData.getTimestamp().toString() + userDailyData.getSteps());

                Log.i("DATE", "\nToday: " + today + "\nData Date: " + date + "\n");

                if(dateStr.equals(today)){

                    if(steps == localStorage.getInt("lastFirebaseUpdateValue", -1)){
                        Log.i("FirebaseUpdate", "Current value same with last update value");
                        break;
                    }

                    db_userData.document("dailyData").set(userDailyData).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            localStorage.edit().putInt("lastFirebaseUpdateValue", steps).apply();
                            resultStatus.onResultReturn(true);
                        }
                    });
                }else{
                    db_userProfile.collection("history").document(dateStr).set(userDailyData).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            localDatabase.deleteDateData(dateStr);
                            resultStatus.onResultReturn(true);
                        }
                    });
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        cursor.close();
        localDatabase.close();
    }

    //DEBUG
    public String getUserID(){
        return user.getUid();
    }

    public void notifySensorType(String type){
        localStorage.edit().putString("Debug_SensorType", type).apply();
    }

    public String getSensorType(){
        return localStorage.getString("Debug_SensorType", "N/A");
    }

    public String getLocalDatabaseContent(){
        Cursor cursor = localDatabase.getData();
        int count = cursor.getCount();

        if(count < 1){
            cursor.close();
            localDatabase.close();
            return "**Empty**\n**Wait for 15 minutes**";
        }

        String result = "";
        int i = 1;

        while(cursor.moveToNext()){
            result = result + cursor.getString(0) + "\t:\t" + cursor.getInt(1);
            if(i < count) result = result + "\n";
            i++;
        }

        cursor.close();
        localDatabase.close();
        return result;
    }

    public String getLocalDatabaseLastUpdate(){
        return localStorage.getString("Debug_LocalDatabaseUpdate", "**null**");
    }

    public void notifyResetSuccess(){
        localStorage.edit().putString("Debug_LastDailyResetTrigger", getCurrentTimeStamp()).apply();
    }

    public String getLastDailyResetTrigger(){
        return localStorage.getString("Debug_LastDailyResetTrigger", "**Haven't Reset Before**");
    }

    public void notifyFirebaseTrigger(){
        localStorage.edit().putString("Debug_LastFirebaseTrigger", getCurrentTimeStamp()).apply();
    }

    public void notifyFirebaseUpdate(){
        localStorage.edit().putString("Debug_LastFirebaseUpdate", getCurrentTimeStamp()).apply();
    }

    public String getLastFirebaseTrigger(){
        return localStorage.getString("Debug_LastFirebaseTrigger", "**Haven't Triggered before**");
    }

    public String getLastFirebaseUpdate(){
        return localStorage.getString("Debug_LastFirebaseUpdate", "**Haven't Updated before**");
    }

    public String getLastFirebaseUpdateValue(){
        int val = localStorage.getInt("lastFirebaseUpdateValue", -1);

        if(val == -1)
            return "null";
        else
            return String.valueOf(val);
    }

    //FOOD
    public void updateFood(TaskResultStatus resultStatus, String name, double totalCalorie){
        FoodHistoryObject obj = new FoodHistoryObject(name, totalCalorie, new Timestamp(new Date()));

        db_userProfile.collection("foodHistory").document(getCurrentTimeStamp()).set(obj).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                resultStatus.onResultReturn(task.isSuccessful());
            }
        });
    }

    //EXERCISE
    public void updateExercise(TaskResultStatus resultStatus, String name, double totalCalorie){
        FoodHistoryObject obj = new FoodHistoryObject(name, totalCalorie, new Timestamp(new Date()));

        db_userProfile.collection("ExerciseHistory").document(getCurrentTimeStamp()).set(obj).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                resultStatus.onResultReturn(task.isSuccessful());
            }
        });
    }

    public static String getCurrentTimeStamp(){
        try {

            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String currentDateTime = dateFormat.format(new Date()); // Find todays date

            return currentDateTime;
        } catch (Exception e) {
            e.printStackTrace();

            return null;
        }
    }
}
