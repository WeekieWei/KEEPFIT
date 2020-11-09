package com.hoongyan.keepfit;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;

import com.facebook.AccessToken;
import com.facebook.login.LoginManager;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineDataSet;
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
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.hoongyan.keepfit.JavaClass.HistoryObject;
import com.hoongyan.keepfit.JavaClass.LocalDatabaseHelper;
import com.hoongyan.keepfit.JavaClass.UserDailyData;
import com.hoongyan.keepfit.JavaClass.UserHomePageData;
import com.hoongyan.keepfit.JavaClass.UserProfile;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Period;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalUnit;
import java.util.ArrayList;
import java.util.Date;
import java.util.UUID;

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

    private ArrayList<BarDataSet> card3ChartDataSet;
    private ArrayList<BarDataSet> card4BarDataSet;
    private ArrayList<LineDataSet> card4LineDataSet;


    public interface AuthenticationCallBack {
        void onCallBack(String title, String message, int icon);
    }

    public interface TaskResultStatus {
        void onResultReturn(boolean result);
    }

    public interface HistoryObjectCallback {
        void onDataFetched(ArrayList<HistoryObject> historyList);
    }

    public interface NetCalFetcher{
        void onDataFetched(ArrayList<BarEntry> barEntries, ArrayList<String> xAxisLabel, float avgNet, boolean isNotEmpty);
    }

    public interface Card4ChartFetcher{
        void onDataFetched(ArrayList<BarEntry> barEntries, ArrayList<Entry> lineEntries, ArrayList<String> xAxisLabel, float weight, boolean isNotEmpty);
    }

    public MVCModel(Activity activity) {
        this.activity = activity;
        auth = FirebaseAuth.getInstance();
        user = getCurrentUser();
        database = FirebaseFirestore.getInstance();
        if(user != null) setDatabaseReference();
        localStorage = activity.getSharedPreferences("KEEPFIT Storage", MODE_PRIVATE);
        localDatabase = new LocalDatabaseHelper(activity.getApplicationContext());
        card3ChartDataSet = null;
        card4BarDataSet = null;
        card4LineDataSet = null;
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
        card3ChartDataSet = null;
        card4BarDataSet = null;
        card4LineDataSet = null;
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
                    localStorage.edit().putBoolean("requestUpdateWeightToFirebase", true).apply();
                }else
                    status.onResultReturn(false);
            }
        });
    }

    public void saveUserProfileToLocalStorage(UserProfile userProfile){
        localStorage.edit().putFloat("calRequired", (float) userProfile.getCalRequired()).apply();
        localStorage.edit().putFloat("weightAdjust", (float) userProfile.getWeightAdjust()).apply();
        localStorage.edit().putFloat("bmi", (float) userProfile.getBmi()).apply();
        localStorage.edit().putFloat("weight", (float) userProfile.getWeight()).apply();
        localStorage.edit().putFloat("height", (float) userProfile.getHeight()).apply();
        localStorage.edit().putString("gender", userProfile.getGender()).apply();
        localStorage.edit().putString("dob", userProfile.getDob()).apply();
        localStorage.edit().putInt("activityLevel", userProfile.getActivityLevel()).apply();
    }

    //HOME
    public void updateLocalDatabase(int steps, Date date){

        if(steps == Integer.MIN_VALUE) steps = (int) getCurrentSteps();

        int currentStep = (int) localStorage.getFloat("currentSteps", 0f);


        if(!localStorage.contains("totalCalIn") || !localStorage.contains("totalCalOut"))
            loadTotalCalorieInOutToLocalStorage();
        float totalCalIn = localStorage.getFloat("totalCalIn", 0f);
        float totalCalOut = localStorage.getFloat("totalCalOut", 0f);
        float totalRequired = localStorage.getFloat("calRequired", 0f);
        float weightAdjust = localStorage.getFloat("weightAdjust", 0f);
        float target = totalRequired + weightAdjust;
        float netCal = -1 * (target - totalCalIn + totalCalOut + currentStep * 0.04f); //another copy down there

        if(localDatabase.updateDailyData(steps, netCal, date)){
            Log.i("UPDATE LOCAL DATABASE", "SUCCESS AT " + getCurrentTimeStamp());

            localStorage.edit().putString("Debug_LocalDatabaseUpdate", getCurrentTimeStamp()).apply();

            Cursor data = localDatabase.getData();

            while(data.moveToNext()){

                Log.i("DATA", data.getString(0) + "    " + data.getInt(1) + " " +
                        data.getFloat(2));
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
                float netCal = cursor.getFloat(2);

                userDailyData.setSteps(steps);
                userDailyData.setNetCal(netCal);
                String dateStr = cursor.getString(0);


                Date date = dateFormat.parse(dateStr);
                userDailyData.setTimestamp(new Timestamp(date));

                //Log.i("TIMESTAMP", userDailyData.getTimestamp().toString() + userDailyData.getSteps());

                Log.i("DATE", "\nToday: " + today + "\nData Date: " + date + "\n");

                if(dateStr.equals(today)){

                    if(steps == localStorage.getInt("lastFirebaseUpdateValue", -1)
                    && netCal == localStorage.getFloat("lastFirebaseUpdateValue2", -1f)){
                        Log.i("FirebaseUpdate", "Current value same with last update value");
                        break;
                    }

                    db_userData.document("dailyData").set(userDailyData).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            localStorage.edit().putInt("lastFirebaseUpdateValue", steps).apply();
                            localStorage.edit().putFloat("lastFirebaseUpdateValue2", netCal).apply();
                            resultStatus.onResultReturn(true);
                        }
                    });
                }else{
                    if(localStorage.getBoolean("requestUpdateWeightToFirebase", false)){
                        userDailyData.setWeight(localStorage.getFloat("weight", 0f));
                        localStorage.edit().putBoolean("requestUpdateWeightToFirebase", false).apply();
                    }

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

    public void loadUserProfileToLocalStorage(TaskResultStatus status){
        db_userProfile.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()){
                    saveUserProfileToLocalStorage(task.getResult().toObject(UserProfile.class));
                    status.onResultReturn(true);
                }
            }
        });
    }

    public boolean isUserProfileExistInLocalStorage(){
        return localStorage.contains("calRequired")
                && localStorage.contains("weightAdjust")
                && localStorage.contains("bmi")
                && localStorage.contains("weight")
                && localStorage.contains("height")
                && localStorage.contains("gender")
                && localStorage.contains("dob")
                && localStorage.contains("activityLevel");
    }

    public UserHomePageData getUserHomePageData(){
        int currentStep = (int) localStorage.getFloat("currentSteps", 0f);


        if(!localStorage.contains("totalCalIn") || !localStorage.contains("totalCalOut"))
            loadTotalCalorieInOutToLocalStorage();
        float totalCalIn = localStorage.getFloat("totalCalIn", 0f);
        float totalCalOut = localStorage.getFloat("totalCalOut", 0f) + currentStep * 0.04f;
        float totalRequired = localStorage.getFloat("calRequired", 0f);
        float weightAdjust = localStorage.getFloat("weightAdjust", 0f);
        float target = totalRequired + weightAdjust;
        float netCal = -1 * (target - totalCalIn + totalCalOut); //another copy up there

        String card2_5Text = "Your daily target calorie is " + String.format("%.2f", target) + " cal. ";
        if(LocalTime.now().getHour() >= 18) {
            if (Math.abs(netCal) < 200) {
                card2_5Text += "Your net calorie is around the expected value today, keep it up! ";

                if (weightAdjust < 0) {
                    card2_5Text += "Try to get as close to 0 net as possible or lesser if possible to lose weight. But don't stress out yourself";
                } else if (weightAdjust > 0) {
                    card2_5Text += "Try to get as close to 0 net as possible or more if possible to gain weight. But don't eat until you throw up";
                }
            } else if (netCal > 0) {
                card2_5Text += "You have consumed " + netCal + " cal extra today. ";

                if (weightAdjust < 0) {
                    card2_5Text += "Try to eat lesser and exercise more";
                } else if (weightAdjust > 0) {
                    card2_5Text += "If you can take it, keep it on!";
                } else {
                    card2_5Text += "If this keep on happening, you will start to gain weight";
                }

            } else if (netCal < 0) {
                card2_5Text += "You have consumed " + (-1 * netCal) + " cal less than target today. ";

                if (weightAdjust < 0) {
                    card2_5Text += "It is not recommended to have less than -200 net calorie, your body needs calorie to stay healthy too";
                } else if (weightAdjust > 0) {
                    card2_5Text += "You really need to eat more to gain weight";
                } else {
                    card2_5Text += "If this keep on happening, you will start to lose weight";
                }
            }
        }



        return new UserHomePageData(String.valueOf((int) totalCalIn), String.valueOf((int) totalCalOut),
                String.valueOf(currentStep), String.valueOf((int) netCal), card2_5Text);
    }

    public void getCard3ChartData(NetCalFetcher fetcher){
        if(card3ChartDataSet == null || localStorage.getBoolean("card3ChartNeedUpdate", true)){
            card3ChartDataSet = new ArrayList<>();

            SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
            String today = dateFormat.format(new Date());

            LocalDate last7Days = LocalDate.now().minus(7, ChronoUnit.DAYS);

            Date last7DaysDate = Date.from(last7Days.atStartOfDay(ZoneId.systemDefault()).toInstant());

            try {
                Date dateToday = dateFormat.parse(today);

                db_userProfile.collection("history").whereLessThan("timestamp", dateToday)
                        .whereGreaterThanOrEqualTo("timestamp", last7DaysDate).limit(7).get()
                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if(task.isSuccessful()){
                                    int i = 0;
                                    int n = 0;
                                    float avgNet = 0;
                                    boolean isNotEmpty = false;

                                    ArrayList<BarEntry> valueSet = new ArrayList<>();
                                    ArrayList<String> xAxisLabel = new ArrayList<>();

                                    QuerySnapshot result = task.getResult();
                                    int dummyData = 7 - result.size();

                                    while(i < dummyData){
                                        valueSet.add(new BarEntry(i, 0f));
                                        xAxisLabel.add(" ");
                                        i++;
                                    }

                                    for(QueryDocumentSnapshot document : result){
                                        UserDailyData userDailyData = document.toObject(UserDailyData.class);
                                        float netCal = userDailyData.getNetCal();
                                        valueSet.add(new BarEntry(i, netCal));
                                        avgNet += netCal;

                                        String date = new SimpleDateFormat("dd/MM")
                                                .format(userDailyData.getTimestamp().toDate());

                                        xAxisLabel.add(date);
                                        i++;
                                        n++;
                                    }

                                    if(n > 0){
                                        avgNet /= n;
                                        isNotEmpty = true;
                                    }

                                    fetcher.onDataFetched(valueSet, xAxisLabel, avgNet, isNotEmpty);
                                    localStorage.edit().putBoolean("card3ChartNeedUpdate", false).apply();
                                }
                            }
                        });

            }catch (Exception e){
                e.printStackTrace();
            }

        }
    }

    public void updateUserWeight(TaskResultStatus status, Float newWeight){

        float height = localStorage.getFloat("height", 0f);

        float bmi = newWeight / height / height * 10000;

        localStorage.edit().putFloat("weight", newWeight).apply();
        localStorage.edit().putFloat("bmi", bmi).apply();

        String dob = localStorage.getString("dob", "");

        //Age
        LocalDate localDate = LocalDate.parse(dob, DateTimeFormatter.ofPattern("dd/MM/yyyy"));
        int age = Period.between(localDate, LocalDate.now()).getYears();

        String gender = localStorage.getString("gender", "");

        //Daily Calorie Required calculation
        //Reference: https://www.calculator.net/calorie-calculator.html

        float calRequired;

        if(gender.equals("Male")){
            //Mifflin-St Jeor Equation
            calRequired = 10 * newWeight + 6.25f * height - 5 * age + 5;
        }else{
            calRequired = 10 * newWeight + 6.25f * height - 5 * age - 161;
        }

        int activityLevel = localStorage.getInt("activityLevel", 0);

        switch(activityLevel){
            case 1 : calRequired *= 1.2;
                break;
            case 2 : calRequired *= 1.375;
                break;
            case 3 : calRequired *= 1.55;
                break;
            case 4 : calRequired *= 1.725;
                break;
            case 5 : calRequired *= 1.9;
                break;
        }

        localStorage.edit().putFloat("calRequired", calRequired).apply();

        int weightAdjust;
        if(bmi < 18.5){ //Gain Weight Constant
            weightAdjust = 500;
        }else if(bmi > 25){ //Lose Weight Constant
            weightAdjust = -500;
        }else{ //Maintain Weight;
            weightAdjust = 0;
        }

        localStorage.edit().putFloat("weightAdjust", weightAdjust).apply();

        db_userProfile.update("bmi", bmi, "calRequired", calRequired, "weight", newWeight, "weightAdjust", weightAdjust)
        .addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                status.onResultReturn(task.isSuccessful());
            }
        });

        localStorage.edit().putBoolean("requestUpdateWeightToFirebase", true).apply();
        localStorage.edit().putBoolean("requestUpdateNewWeight", false).apply();
    }

    public void getCard4ChartData(Card4ChartFetcher fetcher){
        if(card4BarDataSet == null || card4LineDataSet == null || localStorage.getBoolean("card4ChartNeedUpdate", true)) {
            card4BarDataSet = new ArrayList<>();
            card4LineDataSet = new ArrayList<>();

            SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
            String today = dateFormat.format(new Date());

            LocalDate last90Days = LocalDate.now().minus(90, ChronoUnit.DAYS);

            Date last90DaysDate = Date.from(last90Days.atStartOfDay(ZoneId.systemDefault()).toInstant());

            try {
                Date dateToday = dateFormat.parse(today);

                db_userProfile.collection("history").whereLessThan("timestamp", dateToday)
                        .whereGreaterThanOrEqualTo("timestamp", last90DaysDate).get()
                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if(task.isSuccessful()){
                                    int i = 0;
                                    ArrayList<BarEntry> barEntries = new ArrayList<>(); //net Cal
                                    ArrayList<Entry>  lineEntries = new ArrayList<>(); //Weight
                                    ArrayList<String> xAxisLabel = new ArrayList<>();

                                    QuerySnapshot result = task.getResult();

                                    boolean isNotEmpty = !result.isEmpty();

                                    for(QueryDocumentSnapshot document : result){
                                        UserDailyData userDailyData = document.toObject(UserDailyData.class);
                                        float netCal = userDailyData.getNetCal();
                                        barEntries.add(new BarEntry(i, netCal));

                                        double weight = userDailyData.getWeight();
                                        if(weight > 0)
                                            lineEntries.add(new Entry(i, (float) weight));

                                        String date = new SimpleDateFormat("dd/MM")
                                                .format(userDailyData.getTimestamp().toDate());

                                        xAxisLabel.add(date);
                                        i++;
                                    }

                                    float weight = -1;
                                    if(localStorage.getBoolean("requestUpdateNewWeight", false)){
                                        weight = localStorage.getFloat("weight", 0f);
                                    }

                                    fetcher.onDataFetched(barEntries, lineEntries, xAxisLabel, weight, isNotEmpty);
                                    localStorage.edit().putBoolean("card4ChartNeedUpdate", false).apply();
                                }
                            }
                        });

            }catch (Exception e){
                e.printStackTrace();
            }


        }
    }

    public void saveTotalCalorieInToLocalStorage(float totalCalorie){
        localStorage.edit().putFloat("totalCalIn", totalCalorie).apply();
    }

    public void saveTotalCalorieOutToLocalStorage(float totalCalorie){
        localStorage.edit().putFloat("totalCalOut", totalCalorie).apply();
    }

    public void loadTotalCalorieInOutToLocalStorage(){

        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
        String today = dateFormat.format(new Date());

        try {
            Date date = dateFormat.parse(today);

            db_userProfile.collection("History")
                    .whereGreaterThanOrEqualTo("timestamp", date)
                    .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if(task.isSuccessful()){
                        double in = 0;
                        double out = 0;

                        for(QueryDocumentSnapshot document : task.getResult()){
                            HistoryObject obj = document.toObject(HistoryObject.class);
                            if(obj.getType() == 1){
                                in += obj.getTotalCalorie();
                            }else if(obj.getType() == 2){
                                out += obj.getTotalCalorie();
                            }
                        }
                        localStorage.edit().putFloat("totalCalIn", (float) in).apply();
                        localStorage.edit().putFloat("totalCalOut", (float) out).apply();
                    }
                }
            });
        } catch (ParseException e) {
            e.printStackTrace();
        }
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
            result = result + cursor.getString(0) + "\t:\t" + cursor.getInt(1) + "\t\t" + cursor.getFloat(2);
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
        localStorage.edit().putBoolean("requestUpdateNewWeight", true).apply();
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

        String documentID = "F: " + getCurrentTimeStamp() + UUID.randomUUID();

        HistoryObject obj = new HistoryObject(documentID,1, name, totalCalorie, new Timestamp(new Date()));

        db_userProfile.collection("History").document(documentID).set(obj).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    float oldTotal = localStorage.getFloat("totalCalIn", 0f);
                    float newTotal = oldTotal + (float) totalCalorie;
                    saveTotalCalorieInToLocalStorage(newTotal);
                }
                resultStatus.onResultReturn(task.isSuccessful());
            }
        });
    }

    //EXERCISE
    public void updateExercise(TaskResultStatus resultStatus, String name, double totalCalorie){

        String documentID = "E: " + getCurrentTimeStamp() + UUID.randomUUID();

        HistoryObject obj = new HistoryObject(documentID, 2, name, totalCalorie, new Timestamp(new Date()));

        db_userProfile.collection("History").document(documentID).set(obj).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    float oldTotal = localStorage.getFloat("totalCalOut", 0f);
                    float newTotal = oldTotal + (float) totalCalorie;
                    saveTotalCalorieOutToLocalStorage(newTotal);
                }
                resultStatus.onResultReturn(task.isSuccessful());
            }
        });
    }

    public String getUserWeight(){
        return String.valueOf(localStorage.getFloat("weight", 0f));
    }

    public String getUserHeight(){
        return String.valueOf(localStorage.getFloat("height", 0f));
    }

    public String getUserGender(){
        return localStorage.getString("gender", null);
    }

    public float getUserBmi(){return localStorage.getFloat("bmi", 0f);}

    public float getUserWeightAdjust(){return localStorage.getFloat("weightAdjust", 0f);}

    public String getUserAge(){
        String dob = localStorage.getString("dob", null);
        LocalDate localDate = LocalDate.parse(dob, DateTimeFormatter.ofPattern("dd/MM/yyyy"));
        int age = Period.between(localDate, LocalDate.now()).getYears();

        return String.valueOf(age);
    }

    //History
    public void getHistoryList(HistoryObjectCallback callback){

        ArrayList<HistoryObject> list = new ArrayList<>();

        db_userProfile.collection("History").orderBy("timestamp", Query.Direction.DESCENDING).limit(30).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()){
                    for(QueryDocumentSnapshot document: task.getResult()){
                        list.add(document.toObject(HistoryObject.class));
                    }
                    callback.onDataFetched(list);
                }
            }
        });
    }

    public void removeHistoryItem(TaskResultStatus status, String documentID){
        if(!isOnline()){
            status.onResultReturn(false);
            return;
        }

        db_userProfile.collection("History").document(documentID).delete().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                status.onResultReturn(task.isSuccessful());
            }
        });
    }

    public void updateTitle(TaskResultStatus status, String documentID, String newTitle){
        if(!isOnline()){
            status.onResultReturn(false);
            return;
        }

        db_userProfile.collection("History").document(documentID).update("historyTitle", newTitle).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                status.onResultReturn(task.isSuccessful());
            }
        });
    }

    private boolean isOnline() {
        Runtime runtime = Runtime.getRuntime();
        try {
            Process ipProcess = runtime.exec("/system/bin/ping -c 1 8.8.8.8");
            int     exitValue = ipProcess.waitFor();
            return (exitValue == 0);
        }
        catch (IOException | InterruptedException e) { e.printStackTrace(); }

        return false;
    }

    private static String getCurrentTimeStamp(){
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
