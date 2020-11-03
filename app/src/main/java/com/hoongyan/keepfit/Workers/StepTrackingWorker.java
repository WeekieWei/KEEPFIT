package com.hoongyan.keepfit.Workers;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.work.ForegroundInfo;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.hoongyan.keepfit.MVCModel;
import com.hoongyan.keepfit.R;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class StepTrackingWorker extends Worker implements SensorEventListener {

    //Constants
    private final String NOTIFICATION_CHANNEL_ID = getApplicationContext().getString(R.string.notification_pedometer_channel_id);
    private final String NOTIFICATION_CHANNEL_NAME = getApplicationContext().getString(R.string.notification_pedometer_channel_name);
    private final String NOTIFICATION_CHANNEL_DESCRIPTION = getApplicationContext().getString(R.string.notification_pedometer_channel_description);

    private final String NOTIFICATION_TITLE = getApplicationContext().getString(R.string.notification_pedometer_title);

    //MVC Components
    private MVCModel mvcModel;

    //Data Field
    private NotificationManager notificationManager;
    private SensorManager sensorManager;
    private Sensor stepsSensor, accelerometer;

    //private SharedPreferences storage;

    private static float currentSteps;
    private static float prevTotalSteps;

    //Accelerometer Stuff

    private static int SMOOTHING_WINDOW_SIZE = 20;

    private float mRawAccelValues[] = new float[3];

    // smoothing accelerometer signal variables
    private float mAccelValueHistory[][] = new float[3][SMOOTHING_WINDOW_SIZE];
    private float mRunningAccelTotal[] = new float[3];
    private float mCurAccelAvg[] = new float[3];
    private int mCurReadIndex = 0;

    private double mGraph1LastXValue = 0d;
    private double mGraph2LastXValue = 0d;

    private LineGraphSeries<DataPoint> mSeries1;
    private LineGraphSeries<DataPoint> mSeries2;

    private double lastMag = 0d;
    private double avgMag = 0d;
    private double netMag = 0d;

    //peak detection variables
    private double lastXPoint = 1d;
    double stepThreshold = 1.0d;
    double noiseThreshold = 2d;
    private int windowSize = 10;

    public StepTrackingWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
        mvcModel = new MVCModel(getApplicationContext(), false);
        notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        sensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        stepsSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);

        //Accelerometer Stuff

        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mSeries1 = new LineGraphSeries<>();
        mSeries2 = new LineGraphSeries<>();
    }

    @NonNull
    @Override
    public Result doWork() {

        currentSteps = mvcModel.getCurrentSteps();

        setForegroundAsync(new ForegroundInfo(
                Integer.parseInt(NOTIFICATION_CHANNEL_ID),
                getNotification(String.valueOf((int) currentSteps))
        ));

        if(stepsSensor != null) {
            sensorManager.registerListener(StepTrackingWorker.this, stepsSensor, SensorManager.SENSOR_DELAY_UI);
            mvcModel.notifySensorType(stepsSensor.getName());
        }
        else if(accelerometer != null) {
            sensorManager.registerListener(StepTrackingWorker.this, accelerometer, SensorManager.SENSOR_DELAY_UI);
            mvcModel.notifySensorType(accelerometer.getName());
        }

        try{
            Thread.sleep(10 * 1000);
            notificationManager.notify(Integer.parseInt(NOTIFICATION_CHANNEL_ID), getNotification(String.valueOf( (int) currentSteps)));

            while (true){
                Thread.sleep(15 * 60 * 1000);
                mvcModel.updateLocalDatabase((int) mvcModel.getCurrentSteps(), null);
            }
        }catch (InterruptedException e){
            e.printStackTrace();
        }

        return Result.retry();
    }

    private Notification getNotification(String currentSteps){

        //String yesterdaySteps = String.valueOf((int) storage.getFloat("yesterdaySteps", 0f));

        return new NotificationCompat.Builder(getApplicationContext(), NOTIFICATION_CHANNEL_ID)
                .setContentTitle(NOTIFICATION_TITLE + " " + currentSteps)
                .setTicker(NOTIFICATION_TITLE + " " + currentSteps)
               // .setContentText("Yesterday Steps: " + yesterdaySteps)
                .setSmallIcon(R.drawable.ic_work_steps)
                .setOngoing(true)
                .setPriority(NotificationCompat.PRIORITY_LOW)
                .setShowWhen(false)
                .build();
    }

    @Override
    public void onSensorChanged(final SensorEvent event) {
        float totalSteps = event.values[0];
        if(event.sensor.getType() == Sensor.TYPE_STEP_COUNTER){
            prevTotalSteps = mvcModel.isFirstTimeInstalled() ? totalSteps : mvcModel.getPrevTotalSteps();
            Log.i("Steps from sensor", totalSteps + "");
            currentSteps = mvcModel.getCurrentSteps();
            if(totalSteps < prevTotalSteps && prevTotalSteps > 0) {
                currentSteps = currentSteps + totalSteps;
            }else {
                currentSteps = currentSteps + totalSteps - prevTotalSteps;
            }

            if(currentSteps != prevTotalSteps){
                prevTotalSteps = totalSteps;
                mvcModel.saveSteps(currentSteps, prevTotalSteps);
                Log.i("Current Steps", currentSteps + "");
                notificationManager.notify(Integer.parseInt(NOTIFICATION_CHANNEL_ID), getNotification(String.valueOf( (int) currentSteps)));
            }
        }

        //Accelerometer Stuff
        else{
            prevTotalSteps = mvcModel.getCurrentSteps();

            mRawAccelValues[0] = event.values[0];
            mRawAccelValues[1] = event.values[1];
            mRawAccelValues[2] = event.values[2];

            lastMag = Math.sqrt(Math.pow(mRawAccelValues[0], 2) + Math.pow(mRawAccelValues[1], 2) + Math.pow(mRawAccelValues[2], 2));

            //Source: https://github.com/jonfroehlich/CSE590Sp2018
            for (int i = 0; i < 3; i++) {
                mRunningAccelTotal[i] = mRunningAccelTotal[i] - mAccelValueHistory[i][mCurReadIndex];
                mAccelValueHistory[i][mCurReadIndex] = mRawAccelValues[i];
                mRunningAccelTotal[i] = mRunningAccelTotal[i] + mAccelValueHistory[i][mCurReadIndex];
                mCurAccelAvg[i] = mRunningAccelTotal[i] / SMOOTHING_WINDOW_SIZE;
            }
            mCurReadIndex++;
            if(mCurReadIndex >= SMOOTHING_WINDOW_SIZE){
                mCurReadIndex = 0;
            }

            avgMag = Math.sqrt(Math.pow(mCurAccelAvg[0], 2) + Math.pow(mCurAccelAvg[1], 2) + Math.pow(mCurAccelAvg[2], 2));

            netMag = lastMag - avgMag; //removes gravity effect

            //update graph data points
            mGraph1LastXValue += 1d;
            mSeries1.appendData(new DataPoint(mGraph1LastXValue, lastMag), true, 60);

            mGraph2LastXValue += 1d;
            mSeries2.appendData(new DataPoint(mGraph2LastXValue, netMag), true, 60);

            peakDetection();

            if(prevTotalSteps != currentSteps)
                notificationManager.notify(Integer.parseInt(NOTIFICATION_CHANNEL_ID), getNotification(String.valueOf( (int) currentSteps)));
        }
    }

    private void peakDetection(){

        /* Peak detection algorithm derived from: A Step Counter Service for Java-Enabled Devices Using a Built-In Accelerometer, Mladenov et al.
         *Threshold, stepThreshold was derived by observing people's step graph
         * ASSUMPTIONS:
         * Phone is held vertically in portrait orientation for better results
         */

        double highestValX = mSeries2.getHighestValueX();

        if(highestValX - lastXPoint < windowSize){
            return;
        }

        Iterator<DataPoint> valuesInWindow = mSeries2.getValues(lastXPoint,highestValX);

        lastXPoint = highestValX;

        double forwardSlope = 0d;
        double downwardSlope = 0d;

        List<DataPoint> dataPointList = new ArrayList<DataPoint>();
        valuesInWindow.forEachRemaining(dataPointList::add); //This requires API 24 or higher

        for(int i = 0; i<dataPointList.size(); i++){
            if(i == 0) continue;
            else if(i < dataPointList.size() - 1){
                forwardSlope = dataPointList.get(i+1).getY() - dataPointList.get(i).getY();
                downwardSlope = dataPointList.get(i).getY() - dataPointList.get(i - 1).getY();

                if(forwardSlope < 0 && downwardSlope > 0 && dataPointList.get(i).getY() > stepThreshold && dataPointList.get(i).getY() < noiseThreshold){
                    currentSteps = mvcModel.getCurrentSteps();
                    currentSteps = currentSteps + 1;
                    mvcModel.saveCurrentSteps(currentSteps);
                }
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}
