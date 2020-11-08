package com.hoongyan.keepfit.Workers;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.work.ExistingWorkPolicy;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.hoongyan.keepfit.MVCModel;
import com.hoongyan.keepfit.R;

import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.TemporalUnit;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class DailyRefreshWorker extends Worker {

    //CONSTANTS
    private final String NOTIFICATION_CHANNEL_ID = getApplicationContext().getString(R.string.notification_pedometer_channel_id);
    private final String NOTIFICATION_CHANNEL_NAME = getApplicationContext().getString(R.string.notification_pedometer_channel_name);
    private final String NOTIFICATION_CHANNEL_DESCRIPTION = getApplicationContext().getString(R.string.notification_pedometer_channel_description);

    private final String NOTIFICATION_TITLE = getApplicationContext().getString(R.string.notification_pedometer_title);

    private NotificationManager notificationManager;


    //MVC Components
    MVCModel mvcModel;


    public DailyRefreshWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
        mvcModel = new MVCModel(getApplicationContext(), false);
        notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
    }

    @NonNull
    @Override
    public Result doWork() {
        float yesterdaySteps = mvcModel.getCurrentSteps();

        LocalDateTime yesterday = LocalDateTime.now().minusDays(1);
        Date date = Date.from(yesterday.atZone(ZoneId.systemDefault()).toInstant());

        mvcModel.updateLocalDatabase((int) yesterdaySteps, date);

        if(mvcModel.saveCurrentSteps(0)){
            notificationManager.notify(Integer.parseInt(NOTIFICATION_CHANNEL_ID), getNotification(String.valueOf(0)));

            mvcModel.updateLocalDatabase((int) 0, new Date());
            mvcModel.saveTotalCalorieInToLocalStorage(0);
            mvcModel.saveTotalCalorieOutToLocalStorage(0);

            OneTimeWorkRequest dailyRefreshWorkRequest = new OneTimeWorkRequest.Builder(DailyRefreshWorker.class)
                    .setInitialDelay(getDelayToNextDay())
                    .build();

            WorkManager.getInstance(getApplicationContext()).enqueueUniqueWork("DailyRefreshWorker", ExistingWorkPolicy.APPEND_OR_REPLACE, dailyRefreshWorkRequest);

            mvcModel.notifyResetSuccess();

            return Result.success();
        }
        else
            return Result.retry();
    }

    private Notification getNotification(String currentSteps){

        return new NotificationCompat.Builder(getApplicationContext(), NOTIFICATION_CHANNEL_ID)
                .setContentTitle(NOTIFICATION_TITLE + " " + currentSteps)
                .setTicker(NOTIFICATION_TITLE + " " + currentSteps)
                .setSmallIcon(R.drawable.ic_work_steps)
                .setOngoing(true)
                .setPriority(NotificationCompat.PRIORITY_LOW)
                .setShowWhen(false)
                .build();
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
}
