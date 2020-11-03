package com.hoongyan.keepfit.MainActivity.HomeFragment.Controller;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
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

import com.hoongyan.keepfit.MVCModel;
import com.hoongyan.keepfit.MainActivity.HomeFragment.View.HomeFragmentView;
import com.hoongyan.keepfit.MainActivity.MainActivityView;
import com.hoongyan.keepfit.R;
import com.hoongyan.keepfit.Workers.DailyRefreshWorker;
import com.hoongyan.keepfit.Workers.StepTrackingWorker;
import com.hoongyan.keepfit.Workers.UpdateFireBaseWorker;

import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
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

        createNotificationChannel();

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

    @RequiresApi(Build.VERSION_CODES.O)
    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

            final String NOTIFICATION_PEDOMETER_CHANNEL_ID = context.getString(R.string.notification_pedometer_channel_id);
            final String NOTIFICATION_PEDOMETER_CHANNEL_NAME = context.getString(R.string.notification_pedometer_channel_name);
            final String NOTIFICATION_PEDOMETER_CHANNEL_DESCRIPTION = context.getString(R.string.notification_pedometer_channel_description);

            NotificationChannel pedometerChannel = new NotificationChannel(NOTIFICATION_PEDOMETER_CHANNEL_ID, NOTIFICATION_PEDOMETER_CHANNEL_NAME, NotificationManager.IMPORTANCE_LOW);
            pedometerChannel.setDescription(NOTIFICATION_PEDOMETER_CHANNEL_DESCRIPTION);


            final String NOTIFICATION_FIREBASE_CHANNEL_ID = context.getString(R.string.notification_firebase_channel_id);
            final String NOTIFICATION_FIREBASE_CHANNEL_NAME = context.getString(R.string.notification_firebase_channel_name);
            final String NOTIFICATION_FIREBASE_CHANNEL_DESCRIPTION = context.getString(R.string.notification_firebase_channel_description);

            NotificationChannel firebaseChannel = new NotificationChannel(NOTIFICATION_FIREBASE_CHANNEL_ID, NOTIFICATION_FIREBASE_CHANNEL_NAME, NotificationManager.IMPORTANCE_LOW);
            firebaseChannel.setDescription(NOTIFICATION_FIREBASE_CHANNEL_DESCRIPTION);

            if (notificationManager != null) {
                notificationManager.createNotificationChannel(pedometerChannel);
                notificationManager.createNotificationChannel(firebaseChannel);
            }
        }
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
