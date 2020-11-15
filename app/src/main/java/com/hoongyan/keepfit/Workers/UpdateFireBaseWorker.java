package com.hoongyan.keepfit.Workers;

import android.app.Notification;
import android.content.Context;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.work.ForegroundInfo;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.google.firebase.Timestamp;
import com.hoongyan.keepfit.JavaClass.UserDailyData;
import com.hoongyan.keepfit.MVCModel;
import com.hoongyan.keepfit.R;

public class UpdateFireBaseWorker extends Worker {

    //Constants
    private final String NOTIFICATION_FIREBASE_CHANNEL_ID = getApplicationContext().getString(R.string.notification_firebase_channel_id);
    private final String NOTIFICATION_FIREBASE_TITLE = getApplicationContext().getString(R.string.notification_firebase_title);

    //MVC Components
    private MVCModel mvcModel;

    //Data Field
    private boolean status;

    public UpdateFireBaseWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
        mvcModel = new MVCModel(context, true);
    }

    @NonNull
    @Override
    public Result doWork() {

        setForegroundAsync(getForegroundInfo());

        mvcModel.notifyFirebaseTrigger();

        mvcModel.updateFirebase(new MVCModel.TaskResultStatus() {
            @Override
            public void onResultReturn(boolean result) {
                status = result;
            }

        }, false);

        try {
            Thread.sleep(4 * 1000);
            if(status) {
                mvcModel.notifyFirebaseUpdate();
                return Result.success();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return Result.failure();
    }

    private ForegroundInfo getForegroundInfo(){

        Notification notification = new NotificationCompat.Builder(getApplicationContext(), NOTIFICATION_FIREBASE_CHANNEL_ID)
                .setContentTitle(NOTIFICATION_FIREBASE_TITLE)
                .setTicker(NOTIFICATION_FIREBASE_TITLE)
                .setSmallIcon(R.drawable.ic_work_sync)
                .setOngoing(true)
                .setPriority(NotificationCompat.PRIORITY_LOW)
                .build();

        return new ForegroundInfo(Integer.parseInt(NOTIFICATION_FIREBASE_CHANNEL_ID), notification);
    }
}
