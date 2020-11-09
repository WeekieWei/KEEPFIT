package com.hoongyan.keepfit.MainActivity.HomeFragment.Controller;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Build;

import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.work.Constraints;
import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.ExistingWorkPolicy;
import androidx.work.NetworkType;
import androidx.work.OneTimeWorkRequest;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.CombinedData;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.hoongyan.keepfit.JavaClass.UserHomePageData;
import com.hoongyan.keepfit.MVCModel;
import com.hoongyan.keepfit.MainActivity.HomeFragment.View.HomeFragmentView;
import com.hoongyan.keepfit.MainActivity.MainActivityView;
import com.hoongyan.keepfit.R;
import com.hoongyan.keepfit.Workers.DailyRefreshWorker;
import com.hoongyan.keepfit.Workers.StepTrackingWorker;
import com.hoongyan.keepfit.Workers.UpdateFireBaseWorker;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class HomeFragmentController implements HomeFragmentControllerInterface {

    //MVC Components
    private HomeFragmentView homeFragmentView;
    private MVCModel mvcModel;

    //Data Field
    private Fragment homeFragment;
    private Context context;



    public HomeFragmentController(final Fragment homeFragment, final HomeFragmentView homeFragmentView){
        this.homeFragment = homeFragment;
        this.homeFragmentView = homeFragmentView;
        this.context = homeFragment.getActivity().getApplicationContext();
        this.mvcModel = new MVCModel(homeFragment.getActivity());

        if(!mvcModel.getLoginStatus())mvcModel.getCurrentStepsFromFirebase();

        mvcModel.setLoginStatus(true);

        scheduleWorks();

    }

    public void getUpdatedHomePageData(){
        if(mvcModel.isUserProfileExistInLocalStorage()){
            homeFragmentView.bindDataToViews(mvcModel.getUserHomePageData());
        }else{
            mvcModel.loadUserProfileToLocalStorage(new MVCModel.TaskResultStatus() {
                @Override
                public void onResultReturn(boolean result) {
                    if(result)
                        homeFragmentView.bindDataToViews(mvcModel.getUserHomePageData());
                }
            });
        }

        mvcModel.getCard3ChartData(new MVCModel.NetCalFetcher() {
            @Override
            public void onDataFetched(ArrayList<BarEntry> barEntries, ArrayList<String> xAxisLabel, float avgNet, boolean isNotEmpty) {


                BarDataSet barDataSet = new BarDataSet(barEntries, "Net Cal");
                barDataSet.setColors(ColorTemplate.COLORFUL_COLORS);

                BarData barData = new BarData(barDataSet);

                homeFragmentView.bindDataToCard3Chart(barData, xAxisLabel, avgNet, isNotEmpty);
            }
        });

        mvcModel.getCard4ChartData(new MVCModel.Card4ChartFetcher() {
            @Override
            public void onDataFetched(ArrayList<BarEntry> barEntries, ArrayList<Entry> lineEntries, ArrayList<String> xAxisLabel, float weight, boolean isNotEmpty) {

                BarDataSet barDataSet = new BarDataSet(barEntries, "Net Cal");
                barDataSet.setColors(ColorTemplate.COLORFUL_COLORS);
                barDataSet.setAxisDependency(YAxis.AxisDependency.RIGHT);

                LineDataSet lineDataSet = new LineDataSet(lineEntries, "Weight");
                lineDataSet.setAxisDependency(YAxis.AxisDependency.LEFT);
                lineDataSet.setColor(Color.BLUE);
                lineDataSet.setCircleColor(Color.BLUE);

                CombinedData combinedData = new CombinedData();
                BarData barData = new BarData(barDataSet);
                combinedData.setData(barData);
                combinedData.setData(new LineData(lineDataSet));

                homeFragmentView.bindDataToCard4Chart(combinedData, xAxisLabel, weight, isNotEmpty);
            }
        });
    }

    public boolean updateWeight(Float newWeight){
        if(isOnline()){
            mvcModel.updateUserWeight(new MVCModel.TaskResultStatus() {
                @Override
                public void onResultReturn(boolean result) {
                    homeFragmentView.notifyUpdateWeightSuccess(mvcModel.getUserBmi(), mvcModel.getUserWeightAdjust());
                }
            },newWeight);
            return true;
        }
        return false;
    }

    private void scheduleWorks() {
        OneTimeWorkRequest workRequest = new OneTimeWorkRequest.Builder(StepTrackingWorker.class)
                .build();

        WorkManager.getInstance(context).enqueueUniqueWork("StepTrackingWorker", ExistingWorkPolicy.KEEP, workRequest);

        OneTimeWorkRequest dailyRefreshWorkRequest = new OneTimeWorkRequest.Builder(DailyRefreshWorker.class)
                .setInitialDelay(getDelayToNextDay())
                .build();

        WorkManager.getInstance(context).enqueueUniqueWork("DailyRefreshWorker", ExistingWorkPolicy.KEEP, dailyRefreshWorkRequest);

        PeriodicWorkRequest updateFireBaseWorkRequest = new PeriodicWorkRequest.Builder(UpdateFireBaseWorker.class, 1, TimeUnit.HOURS)
                .setConstraints(new Constraints.Builder()
                        .setRequiredNetworkType(NetworkType.CONNECTED)
                        .build())
                .setInitialDelay(18, TimeUnit.MINUTES)
                .build();

        WorkManager.getInstance(context).enqueueUniquePeriodicWork("UpdateFirebaseWorker", ExistingPeriodicWorkPolicy.KEEP, updateFireBaseWorkRequest);
    }

    private Duration getDelayToNextDay(){
        LocalDateTime targetTime = LocalDate.now().atStartOfDay().plusDays(1).plusSeconds(5);
        LocalDateTime timeNow = LocalDateTime.now();

        return Duration.between(timeNow, targetTime);
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

}
